package net.lax1dude.eaglercraft.v1_8.opengl;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer;

/**+
 * Pools the IntBuffers backing WorldRenderer.State - the persistent, resortable
 * copy of a chunk's translucent / realistic-water geometry.
 *
 * These buffers are allocated in WorldRenderer.func_181672_a() and freed in
 * WorldRenderer.State.release(), and that happens very often: every time a
 * chunk finishes (re)compiling with translucent geometry, and every time the
 * transparency sort re-runs as the player moves, the old State's buffer gets
 * freed and a new one gets allocated at a (usually similar) size. Recycling
 * same-size-class buffers instead of always calling
 * EagRuntime.allocateIntBuffer/freeIntBuffer cuts that allocation churn down.
 *
 * Buckets are sized to the next power of two so buffers of similar size share
 * a bucket and can be reused; a returned buffer's own capacity (not the amount
 * of data written into it) decides its bucket, and WorldRenderer.func_181672_a()
 * already relies on flip() to bound reads to what was actually written, so
 * handing out a buffer bigger than requested is safe.
 *
 * Each bucket caps how many idle buffers it holds, so a burst of chunk
 * rebuilds (e.g. loading into a new world, or a big explosion) can't pin down
 * memory forever - once a bucket is full, returned buffers are freed
 * immediately instead of queued.
 *
 * All methods are synchronized rather than built on java.util.concurrent,
 * since this class needs to work identically on both the desktop (real
 * threads) and TeaVM/web (limited java.util.concurrent support) targets.
 */
public class IntBufferPool {

	private static final int MIN_BUCKET_LOG2 = 4; // smallest bucket is 16 ints
	private static final int MAX_BUFFERS_PER_BUCKET = 32; // caps idle memory per size class

	private static final Map<Integer, ArrayDeque<IntBuffer>> pools = new HashMap<Integer, ArrayDeque<IntBuffer>>();

	private static long checkoutCount = 0L;
	private static long hitCount = 0L;
	private static long missCount = 0L;
	private static long pooledInts = 0L;
	private static int pooledBufferCount = 0;

	private static int bucketFor(int minCapacity) {
		int bucketLog2 = MIN_BUCKET_LOG2;
		while ((1 << bucketLog2) < minCapacity) {
			++bucketLog2;
		}
		return 1 << bucketLog2;
	}

	/**
	 * Borrow a buffer with capacity >= minCapacity. The returned buffer is
	 * cleared (position 0, limit == capacity) and may be larger than requested.
	 */
	public static synchronized IntBuffer checkout(int minCapacity) {
		++checkoutCount;
		int bucket = bucketFor(minCapacity);
		ArrayDeque<IntBuffer> queue = pools.get(Integer.valueOf(bucket));
		IntBuffer buf = (queue != null) ? queue.poll() : null;
		if (buf != null) {
			--pooledBufferCount;
			pooledInts -= bucket;
			++hitCount;
			buf.clear();
			return buf;
		}
		++missCount;
		return EagRuntime.allocateIntBuffer(bucket);
	}

	/**
	 * Return a buffer that is no longer referenced by any WorldRenderer.State.
	 * The caller must not touch the buffer again after this.
	 */
	public static synchronized void release(IntBuffer buf) {
		int bucket = buf.capacity();
		ArrayDeque<IntBuffer> queue = pools.get(Integer.valueOf(bucket));
		if (queue == null) {
			queue = new ArrayDeque<IntBuffer>();
			pools.put(Integer.valueOf(bucket), queue);
		}
		if (queue.size() >= MAX_BUFFERS_PER_BUCKET) {
			// this size class is already fully stocked, don't hoard memory
			EagRuntime.freeIntBuffer(buf);
			return;
		}
		buf.clear();
		queue.add(buf);
		++pooledBufferCount;
		pooledInts += bucket;
	}

	public static synchronized long getCheckoutCount() {
		return checkoutCount;
	}

	public static synchronized long getHitCount() {
		return hitCount;
	}

	public static synchronized long getMissCount() {
		return missCount;
	}

	public static synchronized long getPooledInts() {
		return pooledInts;
	}

	public static synchronized int getPooledBufferCount() {
		return pooledBufferCount;
	}
}
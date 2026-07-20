package net.minecraft.apolloclient.event.events;

public class RenderEvent {
    public float partialTicks;

    public RenderEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
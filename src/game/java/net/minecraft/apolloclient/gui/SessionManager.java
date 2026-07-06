package net.minecraft.apolloclient.gui;

public class SessionManager {

    private static long sessionStartTime = System.currentTimeMillis();

    public static void startSessionTime() {
        sessionStartTime = System.currentTimeMillis();
    }

    public static String getSessionTime() {
        long elapsed = System.currentTimeMillis() - sessionStartTime;

        long totalSeconds = elapsed / 1000;

        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }
}
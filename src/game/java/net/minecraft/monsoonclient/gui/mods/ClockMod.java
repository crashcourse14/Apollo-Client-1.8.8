package net.minecraft.monsoonclient.gui.mods;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.util.ResourceLocation;

public class ClockMod extends HudMod {

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm:ss a");

    public ClockMod() {
        super("Clock", 5, 90, Category.HUD);

        this.textFormat = "%VALUE%";

        this.description = "Display local current time";

        //Set the icon for the clock mod
        //this.icon = new ResourceLocation(null, null);
    }

    private String getCurrentTime() {
        return LocalTime.now().format(TIME_FORMAT);
    }

    @Override
    public void draw() {
        drawModText(formatText(getCurrentTime()), getX(), getY());
        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(formatText(getCurrentTime()));
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}
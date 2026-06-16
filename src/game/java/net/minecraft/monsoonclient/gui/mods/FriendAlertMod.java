package net.minecraft.monsoonclient.gui.mods;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.monsoonclient.gui.hud.Category;

/**
 * Sends a highlighted chat alert when a watched player joins the server.
 * Detection happens in GuiNewChat.printChatMessageWithOptionalDeletion.
 * Names are stored as a comma-separated string and split at runtime.
 */
public class FriendAlertMod extends HudMod {

    /** Comma-separated names to watch, e.g. "friend1,friend2" */
    public String watchNames = "";

    public FriendAlertMod() {
        super("Friend Alert", 0, 0, Category.MISC);
        this.supportedOptions = new ModOption[] { ModOption.WATCH_NAMES };
    }

    /** Returns true if the given player name is in the watch list. */
    public boolean isWatched(String name) {
        if (name == null || watchNames.trim().isEmpty()) return false;
        for (String entry : watchNames.split(",")) {
            if (entry.trim().equalsIgnoreCase(name.trim())) return true;
        }
        return false;
    }

    @Override public void draw() {}
    @Override public int getWidth()  { return 0; }
    @Override public int getHeight() { return 0; }
}
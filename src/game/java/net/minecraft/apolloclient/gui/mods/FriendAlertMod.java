package net.minecraft.apolloclient.gui.mods;

import net.minecraft.apolloclient.gui.HudMod;
import net.minecraft.apolloclient.gui.ModOption;
import net.minecraft.apolloclient.gui.hud.Category;

/**
 * Sends a highlighted chat alert when a watched player joins the server.
 * Detection happens in GuiNewChat.printChatMessageWithOptionalDeletion.
 * Names are stored as a comma-separated string and split at runtime.
 */
public class FriendAlertMod extends HudMod {

    public String watchNames = "";

    public FriendAlertMod() {
        super("Friend Alert", 0, 0, Category.MISC);
        // Tell the GUI to ONLY show this custom option
        this.supportedOptions = new ModOption[] { ModOption.WATCH_NAMES };
    }

    public boolean isWatched(String name) {
        if (name == null || watchNames.trim().isEmpty()) return false;
        for (String entry : watchNames.split(",")) {
            if (entry.trim().equalsIgnoreCase(name.trim())) return true;
        }
        return false;
    }


    @Override
    public String getOptionString(ModOption opt) {
        if (opt == ModOption.WATCH_NAMES) {
            return this.watchNames;
        }
        return super.getOptionString(opt);
    }

    @Override
    public void setOptionString(ModOption opt, String val) {
        if (opt == ModOption.WATCH_NAMES) {
            this.watchNames = val;
        } else {
            super.setOptionString(opt, val);
        }
    }

    @Override public void draw() {}
    @Override public int getWidth()  { return 0; }
    @Override public int getHeight() { return 0; }
}
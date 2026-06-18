package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.ModOption;
import net.minecraft.monsoonclient.gui.hud.Category;

public class AntiSpamMod extends HudMod {

    public static int lastUsedChatId = 1000; // Start at 1000, will increment for every new message
    public static String lastMessage = "";
    public static int duplicateCount = 1;

    public AntiSpamMod() {
        super("Anti Spam", 0, 0, Category.MISC);
        this.supportedOptions = new ModOption[0];
        this.enabled = true; 
    }

    @Override public void draw() {}
    @Override public int getWidth()  { return 0; }
    @Override public int getHeight() { return 0; }
}
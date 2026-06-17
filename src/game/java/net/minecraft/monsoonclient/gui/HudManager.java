package net.minecraft.monsoonclient.gui;

import java.util.ArrayList;

import net.minecraft.monsoonclient.gui.mods.TestMod;
import net.minecraft.monsoonclient.gui.mods.CoordinatesMod;
import net.minecraft.monsoonclient.gui.mods.FPSMod;
import net.minecraft.monsoonclient.gui.mods.FullBrightMod;
import net.minecraft.monsoonclient.gui.hud.Category;
import net.minecraft.monsoonclient.gui.hud.CategoryManager;
import net.minecraft.monsoonclient.gui.mods.DispatchDisplayMod;
import net.minecraft.monsoonclient.gui.mods.ClockMod;
import net.minecraft.monsoonclient.gui.mods.PingMod;
import net.minecraft.monsoonclient.gui.mods.CustomTextMod;
import net.minecraft.monsoonclient.gui.mods.SoundMod;
import net.minecraft.monsoonclient.gui.mods.SessionTimeMod;
import net.minecraft.monsoonclient.gui.mods.FriendAlertMod;
import net.minecraft.monsoonclient.gui.mods.BlockOverlayMod;
import net.minecraft.monsoonclient.gui.mods.ItemCounterMod;
import net.minecraft.monsoonclient.gui.NotificationManager;
import net.minecraft.client.Minecraft;

public class HudManager {
    public ArrayList<HudMod> hudMods = new ArrayList<>();

    public TestMod testMod;
    public CoordinatesMod coordinatesMod;
    public FPSMod fpsMod;
    public FullBrightMod fullBrightMod;
    public DispatchDisplayMod dispatchDisplayMod;
    public ClockMod clockMod;
    public PingMod pingMod;
    public CustomTextMod customTextMod;
    public SoundMod soundMod;
    public SessionTimeMod sessionTimeMod;
    public FriendAlertMod friendAlertMod;
    public BlockOverlayMod blockOverlayMod;
    public ItemCounterMod itemCounterMod;

    public CategoryManager categoryManager;
    public NotificationManager notificationManager;

    public HudManager() {
        hudMods.add(testMod = new TestMod());
        hudMods.add(coordinatesMod = new CoordinatesMod());
        hudMods.add(fpsMod = new FPSMod());
        hudMods.add(fullBrightMod = new FullBrightMod());
        hudMods.add(dispatchDisplayMod = new DispatchDisplayMod());
        hudMods.add(clockMod = new ClockMod());
        hudMods.add(pingMod = new PingMod());
        hudMods.add(customTextMod = new CustomTextMod());
        hudMods.add(soundMod = new SoundMod());
        hudMods.add(sessionTimeMod = new SessionTimeMod());
        hudMods.add(friendAlertMod = new FriendAlertMod());
        hudMods.add(blockOverlayMod = new BlockOverlayMod());
        hudMods.add(itemCounterMod = new ItemCounterMod());

        notificationManager = new NotificationManager(Minecraft.getMinecraft());
        categoryManager     = new CategoryManager(this);
    }

    public void renderMods() {
        for (HudMod m : hudMods) {
            if (m.isEnabled()) {
                m.draw();
            }
        }
    }
}
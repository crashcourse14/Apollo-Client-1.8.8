package net.minecraft.apolloclient.gui;

import java.util.ArrayList;

import net.minecraft.apolloclient.gui.mods.TestMod;
import net.minecraft.apolloclient.gui.mods.CoordinatesMod;
import net.minecraft.apolloclient.gui.mods.FPSMod;
import net.minecraft.apolloclient.gui.mods.FullBrightMod;
import net.minecraft.apolloclient.gui.hud.Category;
import net.minecraft.apolloclient.gui.hud.CategoryManager;
import net.minecraft.apolloclient.gui.mods.DispatchDisplayMod;
import net.minecraft.apolloclient.gui.mods.ClockMod;
import net.minecraft.apolloclient.gui.mods.PingMod;
import net.minecraft.apolloclient.gui.mods.CustomTextMod;
import net.minecraft.apolloclient.gui.mods.SoundMod;
import net.minecraft.apolloclient.gui.mods.SessionTimeMod;
import net.minecraft.apolloclient.gui.mods.FriendAlertMod;
import net.minecraft.apolloclient.gui.mods.BlockOverlayMod;
import net.minecraft.apolloclient.gui.mods.ItemCounterMod;
import net.minecraft.apolloclient.gui.mods.ToggleSprintMod;
import net.minecraft.apolloclient.config.ConfigManager; 
import net.minecraft.apolloclient.gui.mods.AntiSpamMod;
import net.minecraft.apolloclient.gui.mods.SelfNameTagMod;
import net.minecraft.apolloclient.gui.mods.ModernKeystrokesMod;
import net.minecraft.apolloclient.gui.mods.WavePointMod;
import net.minecraft.apolloclient.gui.mods.CPSMod;

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
    public ToggleSprintMod toggleSprintMod;
    public AntiSpamMod antiSpamMod;
    public SelfNameTagMod selfNameTagMod;
    public ModernKeystrokesMod modernKeystrokesMod;
    public WavePointMod wavePointMod;
    public CPSMod cpsMod;
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
        hudMods.add(toggleSprintMod = new ToggleSprintMod());
        hudMods.add(selfNameTagMod = new SelfNameTagMod());
        hudMods.add(antiSpamMod = new AntiSpamMod());
        hudMods.add(modernKeystrokesMod = new ModernKeystrokesMod());
        hudMods.add(wavePointMod = new WavePointMod());
        hudMods.add(cpsMod = new CPSMod());

        notificationManager = new NotificationManager(Minecraft.getMinecraft());
        categoryManager     = new CategoryManager(this);
    }

    // Pass partialTicks so the notification manager can calculate smooth slide animations
    public void renderMods(float partialTicks) {
        for (HudMod m : hudMods) {
            if (m.isEnabled()) {
                m.draw();
            }
        }
        
        // Render the notification on top of everything else
        if (notificationManager != null) {
            notificationManager.render(partialTicks);
        }
    }
}
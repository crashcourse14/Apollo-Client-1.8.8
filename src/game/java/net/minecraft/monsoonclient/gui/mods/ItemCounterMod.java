package net.minecraft.monsoonclient.gui.mods;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.monsoonclient.gui.HudMod;
import net.minecraft.monsoonclient.gui.hud.Category;

public class ItemCounterMod extends HudMod {

    public ItemCounterMod() {
        super("Item Count", 5, 90, Category.HUD);

        this.textFormat = "%VALUE%";
    }

    private int getHeldItemCount() {
        if(mc.thePlayer == null) {
            return 0;
        }

        ItemStack held = mc.thePlayer.getHeldItem();

        if(held == null) {
            return 0;
        }

        Item heldItem = held.getItem();
        int count = 0;

        for(int i = 0; i < mc.thePlayer.inventory.mainInventory.length; i++) {
            ItemStack stack = mc.thePlayer.inventory.mainInventory[i];

            if(stack != null && stack.getItem() == heldItem) {
                count += stack.stackSize;
            }
        }

        return count;
    }

    private String getDisplayText() {
        if(mc.thePlayer == null) {
            return "Item: 0";
        }

        ItemStack held = mc.thePlayer.getHeldItem();

        if(held == null) {
            return "No Item";
        }

        return held.getDisplayName() + ": " + getHeldItemCount();
    }

    @Override
    public void draw() {
        drawModText(
            formatText(getDisplayText()),
            getX(),
            getY()
        );

        super.draw();
    }

    @Override
    public int getWidth() {
        return getTextWidth(
            formatText(getDisplayText())
        );
    }

    @Override
    public int getHeight() {
        return getTextHeight();
    }
}
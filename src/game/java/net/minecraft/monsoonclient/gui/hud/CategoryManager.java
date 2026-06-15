package net.minecraft.monsoonclient.gui.hud;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import net.minecraft.monsoonclient.gui.HudManager; 
import net.minecraft.monsoonclient.gui.HudMod;

public class CategoryManager {

    private final Map<Category, List<HudMod>> categorizedMods = new EnumMap<>(Category.class);

    public CategoryManager(HudManager hudManager) {
        for (Category category : Category.values()) {
            categorizedMods.put(category, new ArrayList<HudMod>());
        }
        refresh(hudManager);
    }

    public void refresh(HudManager hudManager) {
        for (List<HudMod> mods : categorizedMods.values()) {
            mods.clear();
        }
        for (HudMod mod : hudManager.hudMods) {
            categorizedMods.get(mod.category).add(mod);
        }
    }

    public List<HudMod> getMods(Category category) {
        return categorizedMods.get(category);
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categorizedMods.keySet());
    }
}
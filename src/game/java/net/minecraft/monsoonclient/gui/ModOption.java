package net.minecraft.monsoonclient.gui;

/**
 * Declares which options a HudMod supports.
 * Each mod specifies a subset of these in its supportedOptions array.
 * GuiModList reads this array and only renders the relevant controls.
 */
public enum ModOption {
    /** Text format string with %VALUE% placeholder */
    TEXT_FORMAT,
    /** Hex color picker for the rendered text */
    TEXT_COLOR,
    /** Float scale multiplier for the rendered text */
    TEXT_SCALE,
    /** Toggle text drop-shadow */
    TEXT_SHADOW,
    /** Per-category sound volume sliders */
    SOUND_SLIDERS
}
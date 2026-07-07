package net.minecraft.apolloclient.gui;

public enum ModOption {
    TEXT_FORMAT(OptionType.STRING),
    TEXT_COLOR(OptionType.COLOR),
    TEXT_SCALE(OptionType.NUMBER),
    TEXT_SHADOW(OptionType.BOOLEAN),
    RENDER_BACKGROUND(OptionType.BOOLEAN),
    BACKGROUND_COLOR(OptionType.COLOR),
    BACKGROUND_OPACITY(OptionType.NUMBER),
    WATCH_NAMES(OptionType.STRING),
    OUTLINE_COLOR(OptionType.COLOR),
    HOVER_COLOR(OptionType.COLOR),
    HOVER_OPACITY(OptionType.NUMBER),
    CLICK_COLOR(OptionType.COLOR),
    SOUND_SLIDERS(OptionType.CUSTOM),
    BEAM_COLOR(OptionType.COLOR),
    BEAM_OPACITY(OptionType.NUMBER),
    BEAM_TYPE(OptionType.STRING);

    public enum OptionType { STRING, COLOR, NUMBER, BOOLEAN, CUSTOM }
    public final OptionType type;

    ModOption(OptionType type) {
        this.type = type;
    }
}
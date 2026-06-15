package net.minecraft.monsoonclient.rank;

import java.util.HashMap;
import java.util.Map;

public class RankManager {

    private static final Map<String, String> RANKS = new HashMap<>();

    static {
        RANKS.put("983kk", "§4⊛§r ");
        RANKS.put("Haydyn", "§b✦§r ");
    }

    public static String getRank(String username) {
        return RANKS.get(username);
    }

}
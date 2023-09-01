package com.gitsh01.libertyvillagers;

public class LibertyVillagersMod {
    public static final String MOD_ID = "libertyvillagers";
    public static void init() {
        System.out.println(LibertyVillagersExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}

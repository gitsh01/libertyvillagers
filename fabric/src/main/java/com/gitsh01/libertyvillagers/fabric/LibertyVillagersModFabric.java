package com.gitsh01.libertyvillagers.fabric;

import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import net.fabricmc.api.ModInitializer;

public class LibertyVillagersModFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LibertyVillagersMod.init();
    }
}

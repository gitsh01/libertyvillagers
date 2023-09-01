package com.gitsh01.libertyvillagers.forge;

import dev.architectury.platform.forge.EventBuses;
import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LibertyVillagersMod.MOD_ID)
public class LibertyVillagersModForge {
    public LibertyVillagersModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(LibertyVillagersMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        LibertyVillagersMod.init();
    }
}

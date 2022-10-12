package com.gitsh01.libertyvillagers;

import com.gitsh01.libertyvillagers.cmds.VillagerStats;
import com.gitsh01.libertyvillagers.config.LibertyVillagersConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;

public class LibertyVillagersMod implements ModInitializer {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static ConfigHolder<LibertyVillagersConfig> CONFIG_MANAGER;
    public static LibertyVillagersConfig CONFIG;

    static {
        CONFIG_MANAGER = AutoConfig.register(LibertyVillagersConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LibertyVillagersConfig.class).getConfig();

    }

    @Override
    public void onInitialize() {
        VillagerStats.register();
    }
}

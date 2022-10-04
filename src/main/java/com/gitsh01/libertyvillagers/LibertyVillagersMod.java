package com.gitsh01.libertyvillagers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.ConfigHolder;
import net.fabricmc.api.ModInitializer;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import com.gitsh01.libertyvillagers.config.LibertyVillagersConfig;

public class LibertyVillagersMod implements ModInitializer {
    public static ConfigHolder<LibertyVillagersConfig> CONFIG_MANAGER;
    public static LibertyVillagersConfig CONFIG;

    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    static {
        CONFIG_MANAGER = AutoConfig.register(LibertyVillagersConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(LibertyVillagersConfig.class).getConfig();

    }

    @Override
    public void onInitialize() {}
}

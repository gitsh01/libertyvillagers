package com.gitsh01.libertyvillagers.fabric;

import com.gitsh01.libertyvillagers.config.BaseConfig;
import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;

public class LibertyVillagersModFabric implements ModInitializer {
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();
    public static ConfigHolder<BaseConfig> CONFIG_MANAGER;
    public static BaseConfig CONFIG;

    static {
        CONFIG_MANAGER = AutoConfig.register(BaseConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(BaseConfig.class).getConfig();
    }

    @Override
    public void onInitialize() {
        LibertyVillagersMod.init();
    }
}

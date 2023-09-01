package com.gitsh01.libertyvillagers;

import com.gitsh01.libertyvillagers.config.BaseConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class LibertyVillagersMod {
    public static final String MOD_ID = "libertyvillagers";

    public static ConfigHolder<BaseConfig> CONFIG_MANAGER;
    public static BaseConfig CONFIG;

    static {
        CONFIG_MANAGER = AutoConfig.register(BaseConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(BaseConfig.class).getConfig();
    }

    public static void init() {
        System.out.println(LibertyVillagersExpectPlatform.getConfigDirectory().toAbsolutePath().normalize().toString());
    }
}

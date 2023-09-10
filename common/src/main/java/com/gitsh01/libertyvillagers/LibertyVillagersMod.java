package com.gitsh01.libertyvillagers;

import com.gitsh01.libertyvillagers.cmds.VillagerInfo;
import com.gitsh01.libertyvillagers.cmds.VillagerReset;
import com.gitsh01.libertyvillagers.cmds.VillagerSetPOI;
import com.gitsh01.libertyvillagers.cmds.VillagerStats;
import com.gitsh01.libertyvillagers.config.BaseConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import dev.architectury.event.events.common.*;

public class LibertyVillagersMod {
    public static final String MOD_ID = "libertyvillagers";

    public static ConfigHolder<BaseConfig> CONFIG_MANAGER;
    public static BaseConfig CONFIG;

    static {
        CONFIG_MANAGER = AutoConfig.register(BaseConfig.class, GsonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(BaseConfig.class).getConfig();
    }

    public static void init() {
        CommandRegistrationEvent.EVENT.register(LibertyVillagersMod::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess access,
                                         CommandManager.RegistrationEnvironment registrationEnvironment) {
        VillagerInfo.register(dispatcher);
        VillagerReset.register(dispatcher);
        VillagerSetPOI.register(dispatcher);
        VillagerStats.register(dispatcher);
    }
}

package com.gitsh01.libertyvillagers.integration;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.gitsh01.libertyvillagers.config.LibertyVillagersConfig;
import me.shedaniel.autoconfig.AutoConfig;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return  screen -> AutoConfig.getConfigScreen(LibertyVillagersConfig.class, screen).get();
    }
}
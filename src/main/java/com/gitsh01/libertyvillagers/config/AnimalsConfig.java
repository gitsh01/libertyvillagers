package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "AnimalsConfig")
public class AnimalsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean beesAvoidTrapdoors = true;

}

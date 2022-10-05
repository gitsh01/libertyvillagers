package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "LibertyVillagers")
@Config.Gui.Background("minecraft:textures/block/barrel_side.png")
public class LibertyVillagersConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public int findPOIRange = 128;

    @ConfigEntry.Gui.Tooltip
    public boolean healOnWake = true;

    @ConfigEntry.Gui.Tooltip
    public int findCropRange = 10;

    @ConfigEntry.Gui.Tooltip
    public boolean preferPlantSameCrop = true;

}

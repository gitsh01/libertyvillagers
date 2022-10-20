package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "VillagersProfession")
public class VillagersProfessionConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean armorerHealsGolems = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int armorerHealsGolemsRange = 32;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public int findCropRangeHorizontal = 10;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public int findCropRangeVertical = 2;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean preferPlantSameCrop = true;
}

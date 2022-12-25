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

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean clericThrowsPotionsAtPlayers = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean clericThrowsPotionsAtVillagers = true;
    @ConfigEntry.Gui.Tooltip(count = 2)
    public int clericThrowsPotionsAtRange = 32;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public int findCropRangeHorizontal = 10;

    @ConfigEntry.Gui.Tooltip(count = 3)
    public int findCropRangeVertical = 3;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean preferPlantSameCrop = true;

    @ConfigEntry.Gui.Tooltip
    public boolean farmersHarvestMelons = true;

    @ConfigEntry.Gui.Tooltip
    public boolean farmersHarvestPumpkins = true;

    @ConfigEntry.Gui.Tooltip
    public boolean butchersFeedCows = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean leatherworkersFeedCows = true;

    @ConfigEntry.Gui.Tooltip
    public int butchersFeedCowsRange = 20;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int butchersMaxCows = 30;
}

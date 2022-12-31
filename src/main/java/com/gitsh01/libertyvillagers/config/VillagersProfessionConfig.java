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

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean butchersFeedChickens = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean butchersFeedCows = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean butchersFeedPigs = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean butchersFeedRabbits = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean butchersFeedSheep = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean fletchersFeedChickens = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean leatherworkersFeedCows = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean shepherdsFeedSheep = false;

    @ConfigEntry.Gui.Tooltip
    public int feedAnimalsRange = 20;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int feedMaxAnimals = 30;

    @ConfigEntry.Gui.Tooltip
    public boolean fishermanFish = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int fishermanFindWaterRange = 10;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int fishermanFishingWaterRange = 5;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean librariansLookAtBooks = true;

}

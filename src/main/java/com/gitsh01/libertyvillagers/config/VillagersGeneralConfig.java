package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "VillagersGeneral")
public class VillagersGeneralConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int findPOIRange = 128;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int pathfindingMaxRange = 256;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int minimumPOISearchDistance = 3;

    @ConfigEntry.Gui.Tooltip
    public boolean healOnWake = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidCactus = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidWater = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagerWanderingFix = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int walkTowardsTaskMaxRunTime = 2400;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int walkTowardsTaskMinCompletionRange = 3;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontClimb = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int villagerSafeFallDistance = 2;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersDontBreed = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagerBabiesRequireWorkstationAndBed = false;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontLookForWorkstationsAtNight = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersEatMelons = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersEatPumpkinPie = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontTrampleCrops = true;

    @ConfigEntry.Gui.Tooltip
    public boolean noNitwitVillagers = false;

    @ConfigEntry.Gui.Tooltip
    public boolean allNitwitVillagers = false;

    @ConfigEntry.Gui.Tooltip
    public int growUpTime = 24000;

    @ConfigEntry.Gui.Tooltip
    public boolean allBabyVillagers = false;

    @ConfigEntry.Gui.Tooltip
    public boolean foreverYoung = false;
}

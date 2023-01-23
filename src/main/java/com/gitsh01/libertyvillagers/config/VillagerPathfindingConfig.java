package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "VillagerPathfinding")
public class VillagerPathfindingConfig implements ConfigData {
    @ConfigEntry.Gui.Tooltip(count = 2)
    public int findPOIRange = 128;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int pathfindingMaxRange = 256;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int minimumPOISearchDistance = 3;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidCactus = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidWater = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidRail = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidTrapdoor = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidPowderedSnow = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagersAvoidGlassPanes = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean villagerWanderingFix = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int walkTowardsTaskMaxRunTime = 2400;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontClimb = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int villagerSafeFallDistance = 2;
}

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
    public int pathfindingMaxRange = 256;

    @ConfigEntry.Gui.Tooltip
    public boolean healOnWake = true;

    @ConfigEntry.Gui.Tooltip
    public int findCropRange = 10;

    @ConfigEntry.Gui.Tooltip
    public boolean preferPlantSameCrop = true;

    @ConfigEntry.Gui.Tooltip
    public boolean golemsAvoidCactus = true;

    @ConfigEntry.Gui.Tooltip
    public boolean golemsAvoidWater = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersAvoidCactus = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersAvoidWater = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontClimb = true;

    @ConfigEntry.Gui.Tooltip
    public boolean armorerHealsGolems = true;

    @ConfigEntry.Gui.Tooltip
    public int armorerHealsGolemsRange = 32;

    @ConfigEntry.Gui.Tooltip
    public int villagerStatRange = 256;
}

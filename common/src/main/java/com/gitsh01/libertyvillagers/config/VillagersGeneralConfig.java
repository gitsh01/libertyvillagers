package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "VillagersGeneral")
public class VillagersGeneralConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean healOnWake = true;

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
    public boolean villagersEatCookedFish = true;

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

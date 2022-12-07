package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "GolemsConfig")
public class GolemsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean golemsAvoidCactus = true;

    @ConfigEntry.Gui.Tooltip
    public boolean golemsAvoidWater = true;

    @ConfigEntry.Gui.Tooltip
    public boolean golemsAvoidRail = true;

    @ConfigEntry.Gui.Tooltip
    public boolean golemsDontAttackPlayer = true;

    @ConfigEntry.Gui.Tooltip
    public boolean golemsDontTrampleCrops = true;

    @ConfigEntry.Gui.Tooltip
    public boolean villagersDontSummonGolems = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean golemSpawnLimit = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int golemSpawnLimitCount = 10;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int golemSpawnLimitRange = 128;
}

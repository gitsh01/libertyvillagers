package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "CatsConfig")
public class CatsConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean catsSpawnLimit = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int catsSpawnLimitCount = 10;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int catsSpawnLimitRange = 128;

    @ConfigEntry.Gui.Tooltip
    public boolean villageCatsDontDespawn = false;

    @ConfigEntry.Gui.Tooltip
    public boolean blackCatsAtAnyTime = false;

    @ConfigEntry.Gui.Tooltip
    public boolean allBlackCats = false;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public boolean catsStayNearBell = true;

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int catsMaxBellRange = 128;

    @ConfigEntry.Gui.Tooltip
    public boolean catsDontClimb = true;
}

package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

@Config(name = "Debug")
public class DebugConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip(count = 2)
    public int villagerStatRange = 256;

    @ConfigEntry.Gui.Tooltip
    public boolean enableVillagerInfoOverlay = false;

    @ConfigEntry.Gui.Tooltip
    public boolean villagerInfoShowsPath = false;

    @ConfigEntry.Gui.Tooltip
    public boolean enableVillagerBrainDebug = false;
}

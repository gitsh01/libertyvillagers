package com.gitsh01.libertyvillagers.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.PartitioningSerializer;

@Config(name = "LibertyVillagers")
@Config.Gui.Background("minecraft:textures/block/emerald_block.png")
@Config.Gui.CategoryBackground(category = "VillagersGeneral", background = "minecraft:textures/block/copper_block.png")
@Config.Gui.CategoryBackground(category = "VillagersPathfinding",
        background = "minecraft:textures/block/honeycomb_block" + ".png")
@Config.Gui.CategoryBackground(category = "VillagersProfession",
        background = "minecraft:textures/block/diamond_block" + ".png")
@Config.Gui.CategoryBackground(category = "Golems", background = "minecraft:textures/block/emerald_block.png")
@Config.Gui.CategoryBackground(category = "Debug", background = "minecraft:textures/block/redstone_block.png")
@Config.Gui.CategoryBackground(category = "Cats", background = "minecraft:textures/block/lapis_block.png")
@Config.Gui.CategoryBackground(category = "Animals", background = "minecraft:textures/block/beehive_front_honey.png")
public class BaseConfig extends PartitioningSerializer.GlobalData implements ConfigData {
    @ConfigEntry.Category(value = "VillagersGeneral")
    @ConfigEntry.Gui.TransitiveObject
    public final VillagersGeneralConfig villagersGeneralConfig = new VillagersGeneralConfig();

    @ConfigEntry.Category(value = "VillagerPathfinding")
    @ConfigEntry.Gui.TransitiveObject
    public final VillagerPathfindingConfig villagerPathfindingConfig = new VillagerPathfindingConfig();

    @ConfigEntry.Category(value = "VillagersProfession")
    @ConfigEntry.Gui.TransitiveObject
    public final VillagersProfessionConfig villagersProfessionConfig = new VillagersProfessionConfig();

    @ConfigEntry.Category(value = "Golems")
    @ConfigEntry.Gui.TransitiveObject
    public final GolemsConfig golemsConfig = new GolemsConfig();

    @ConfigEntry.Category(value = "Cats")
    @ConfigEntry.Gui.TransitiveObject
    public final CatsConfig catsConfig = new CatsConfig();

    @ConfigEntry.Category(value = "Animals")
    @ConfigEntry.Gui.TransitiveObject
    public final AnimalsConfig animalsConfig = new AnimalsConfig();

    @ConfigEntry.Category(value = "Debug")
    @ConfigEntry.Gui.TransitiveObject
    public final DebugConfig debugConfig = new DebugConfig();
}

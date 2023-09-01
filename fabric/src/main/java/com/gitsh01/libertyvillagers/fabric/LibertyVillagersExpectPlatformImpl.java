package com.gitsh01.libertyvillagers.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LibertyVillagersExpectPlatformImpl {
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path getConfigFolder() {
        return FabricLoader.getInstance().getConfigDir();
    }
}

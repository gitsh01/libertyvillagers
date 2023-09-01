package com.gitsh01.libertyvillagers.fabric;

import com.gitsh01.libertyvillagers.LibertyVillagersExpectPlatform;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class LibertyVillagersExpectPlatformImpl {
    /**
     * This is our actual method to {@link LibertyVillagersExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
}

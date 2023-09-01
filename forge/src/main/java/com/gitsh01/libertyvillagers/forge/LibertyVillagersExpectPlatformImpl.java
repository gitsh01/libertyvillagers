package com.gitsh01.libertyvillagers.forge;

import com.gitsh01.libertyvillagers.LibertyVillagersExpectPlatform;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class LibertyVillagersExpectPlatformImpl {
    /**
     * This is our actual method to {@link LibertyVillagersExpectPlatform#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
}

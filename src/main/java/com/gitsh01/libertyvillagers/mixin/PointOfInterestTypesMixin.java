package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(PointOfInterestTypes.class)
public class PointOfInterestTypesMixin {

    @ModifyArg(method = "registerAndGetDefault(Lnet/minecraft/registry/Registry;)Lnet/minecraft/world/poi/PointOfInterestType;",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/poi/PointOfInterestTypes;register(Lnet/minecraft/registry/Registry;" +
                    "Lnet/minecraft/registry/RegistryKey;Ljava/util/Set;II)" +
                    "Lnet/minecraft/world/poi/PointOfInterestType;"),
            index = 4)
    private static int modifySearchDistance(int searchDistance) {
        return Math.max(searchDistance, CONFIG.villagerPathfindingConfig.minimumPOISearchDistance);
    }
}
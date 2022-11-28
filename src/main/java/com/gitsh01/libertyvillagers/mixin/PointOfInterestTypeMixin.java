package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(PointOfInterestType.class)
public class PointOfInterestTypeMixin {

    @ModifyArg(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/poi/PointOfInterestType;register(Ljava/lang/String;Ljava/util/Set;II)" +
                    "Lnet/minecraft/world/poi/PointOfInterestType;"),
            index = 3)
    private static int modifySearchDistance(int searchDistance) {
        return Math.max(searchDistance, CONFIG.villagersGeneralConfig.minimumPOISearchDistance);
    }
}
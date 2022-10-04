package com.gitsh01.libertyvillagers.mixin;

import com.mojang.datafixers.DataFixer;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.poi.PointOfInterestSet;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.storage.SerializingRegionBasedStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.function.Predicate;

@Mixin(PointOfInterestStorage.class)
public abstract class PointOfInterestStorageMixin  extends SerializingRegionBasedStorage<PointOfInterestSet> {

    public PointOfInterestStorageMixin(Path path, DataFixer dataFixer, boolean dsync, DynamicRegistryManager registryManager, HeightLimitView world) {
        super(path, PointOfInterestSet::createCodec, PointOfInterestSet::new, dataFixer, DataFixTypes.POI_CHUNK, dsync, registryManager, world);
    }

    @Inject(at = @At("HEAD"), method = "getInCircle(Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;")
    void logGetInCircle(Predicate<RegistryEntry<PointOfInterestType>> typePredicate, BlockPos pos, int radius, PointOfInterestStorage.OccupationStatus occupationStatus, CallbackInfoReturnable cir) {
        //System.out.printf("Radius is %d\n", radius);
    }
}



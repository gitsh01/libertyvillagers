package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkHomeTask.class)
public abstract class WalkHomeTaskMixin extends Task<LivingEntity> {

    public WalkHomeTaskMixin() {
        super(ImmutableMap.of());
    }

    /* Prevents villagers from getting confused about a door directly below their bed. */
    @Redirect(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;getSquaredDistance(Lnet/minecraft/util/math/Vec3i;)D"))
    private double replaceSquaredDistanceWithManhattan(BlockPos origin, Vec3i dest) {
        return origin.getManhattanDistance(dest);
    }

    @ModifyArgs(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getNearestPosition(Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/Optional;"))
    protected void modifyShouldRunGetNearestPositionArgs(Args args) {
        args.set(2, CONFIG.villagersGeneralConfig.findPOIRange);
        args.set(3, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
    }

    @Redirect(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getPositions" +
                    "(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                    "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"))
    public Stream<BlockPos> modifyGetTypesAndPositions(
            PointOfInterestStorage pointOfInterestStorage, Predicate<PointOfInterestType> typePredicate,
            Predicate<BlockPos> posPredicate, BlockPos pos, int radius,
            PointOfInterestStorage.OccupationStatus occupationStatus) {
        return pointOfInterestStorage.getSortedPositions(typePredicate, posPredicate, pos,
                CONFIG.villagersGeneralConfig.findPOIRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
    }
}

package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkHomeTask.class)
public abstract class WalkHomeTaskMixin extends Task<LivingEntity> {

    private ServerWorld world;
    private LivingEntity entity;

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

    @ModifyConstant(
            method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            constant = @Constant(doubleValue = 4.0))
    private double replaceSquaredDistanceWithManhattanConstant(double constant) {
        return 2.0f;
    }

    @ModifyArg(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getNearestPosition" +
                            "(Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                            "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/Optional;"),
            index = 2)
    protected int modifyShouldRunGetNearestPositionArgs(int findPOIRange) {
        return CONFIG.villagerPathfindingConfig.findPOIRange;
    }

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)V",
        at = @At("HEAD"))
    protected void runHead(ServerWorld world, LivingEntity entity, long time, CallbackInfo ci) {
        this.world = world;
        this.entity = entity;
    }

    @Redirect(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getPositions" +
                    "(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                    "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"))
    public Stream<BlockPos> modifyGetTypesAndPositions(
            PointOfInterestStorage pointOfInterestStorage, Predicate<PointOfInterestType> typePredicate,
            Predicate<BlockPos> posPredicate, BlockPos pos, int radius,
            PointOfInterestStorage.OccupationStatus occupationStatus) {
        Predicate<BlockPos> newBlockPosPredicate = blockPos -> {
            if (isBedOccupiedByOthers(this.world, blockPos, this.entity)) {
                return false;
            }
            return posPredicate.test(blockPos);
        };

        Set<BlockPos> set =
                pointOfInterestStorage.getSortedPositions(typePredicate, newBlockPosPredicate, pos,
                CONFIG.villagerPathfindingConfig.findPOIRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE).collect(
                        Collectors.toSet());

        if (!set.isEmpty()) {
            return set.stream();
        }

        // All beds are occupied, go back to default behavior of meeping around the nearest bed at night, worst
        // roommate ever.
        return pointOfInterestStorage.getSortedPositions(typePredicate, posPredicate, pos,
                CONFIG.villagerPathfindingConfig.findPOIRange, occupationStatus);
    }

    private boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED) && !entity.isSleeping();
    }
}

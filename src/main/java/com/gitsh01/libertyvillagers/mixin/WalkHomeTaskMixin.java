package com.gitsh01.libertyvillagers.mixin;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.mutable.MutableLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkHomeTask.class)
public abstract class WalkHomeTaskMixin {

    private static ServerWorld world;

    /* Prevents villagers from getting confused about a door directly below their bed. */
    @SuppressWarnings("target")
    @Redirect(method = "method_47054",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;getSquaredDistance(Lnet/minecraft/util/math/Vec3i;)D"))
    private static double replaceSquaredDistanceWithManhattan(BlockPos origin, Vec3i dest) {
        return origin.getManhattanDistance(dest);
    }

    @ModifyConstant(method = "method_47054",
            constant = @Constant(doubleValue = 4.0))
    private static double replaceSquaredDistanceWithManhattanConstant(double constant) {
        return 2.0f;
    }

    @SuppressWarnings("target")
    @ModifyArgs(method = "method_47054",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getNearestPosition(Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/Optional;"))
    private static void modifyShouldRunGetNearestPositionArgs(Args args) {
        args.set(2, CONFIG.villagerPathfindingConfig.findPOIRange);
    }

    @SuppressWarnings("target")
    @Inject(method = "method_47054",
            at = @At("HEAD"))
    private static void runHead(MutableLong mutableLong, Long2LongMap map, MemoryQueryResult result, float speed,
                           ServerWorld serverWorld,
                           PathAwareEntity entity,
                           long time, CallbackInfoReturnable<Boolean> cir) {
        world = serverWorld;
    }

    @Redirect(method = "method_47054",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getTypesAndPositions" +
                    "(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                    "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"))
    private static Stream<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> modifyGetTypesAndPositions(
            PointOfInterestStorage pointOfInterestStorage, Predicate<RegistryEntry<PointOfInterestType>> typePredicate,
            Predicate<BlockPos> posPredicate, BlockPos pos, int radius,
            PointOfInterestStorage.OccupationStatus occupationStatus) {
        Predicate<BlockPos> newBlockPosPredicate = blockPos -> {
            if (isBedOccupied(world, blockPos)) {
                return false;
            }
            return posPredicate.test(blockPos);
        };

        Stream<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> stream =
                pointOfInterestStorage.getSortedTypesAndPositions(typePredicate, newBlockPosPredicate, pos,
                        CONFIG.villagerPathfindingConfig.findPOIRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set = stream.collect(Collectors.toSet());

        if (!set.isEmpty()) {
            return set.stream();
        }

        // All beds are occupied, go back to default behavior of meeping around the nearest bed at night, worst
        // roommate ever.
        return pointOfInterestStorage.getSortedTypesAndPositions(typePredicate, posPredicate, pos,
                CONFIG.villagerPathfindingConfig.findPOIRange, occupationStatus);
    }

    private static boolean isBedOccupied(ServerWorld world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED);
    }
}

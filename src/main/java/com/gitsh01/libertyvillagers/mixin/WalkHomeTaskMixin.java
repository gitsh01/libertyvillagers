package com.gitsh01.libertyvillagers.mixin;

import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.poi.PointOfInterestStorage;
import org.apache.commons.lang3.mutable.MutableLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.function.Predicate;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkHomeTask.class)
public abstract class WalkHomeTaskMixin {

    private static ServerWorld world;

    /* Prevents villagers from getting confused about a door directly below their bed. */
    @SuppressWarnings("target")
    @Redirect(method = "method_47054(Lorg/apache/commons/lang3/mutable/MutableLong;" +
            "Lit/unimi/dsi/fastutil/longs/Long2LongMap;Lbjh;FLahm;Lbep;J)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/BlockPos;getSquaredDistance(Lnet/minecraft/util/math/Vec3i;)D"))
    private static double replaceSquaredDistanceWithManhattan(BlockPos origin, Vec3i dest) {
        return origin.getManhattanDistance(dest);
    }

    @SuppressWarnings("target")
    @ModifyArgs(method = "method_47054(Lorg/apache/commons/lang3/mutable/MutableLong;" +
            "Lit/unimi/dsi/fastutil/longs/Long2LongMap;Lbjh;FLahm;Lbep;J)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getNearestPosition(Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/Optional;"))
    private static void modifyShouldRunGetNearestPositionArgs(Args args) {
        args.set(2, CONFIG.villagersGeneralConfig.findPOIRange);
        args.set(3, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
    }

    @SuppressWarnings("target")
    @Inject(method = "method_47054(Lorg/apache/commons/lang3/mutable/MutableLong;" +
            "Lit/unimi/dsi/fastutil/longs/Long2LongMap;Lbjh;FLahm;Lbep;J)Z",
            at = @At("HEAD"))
    private static void runHead(MutableLong mutableLong, Long2LongMap map, MemoryQueryResult result, float speed,
                           ServerWorld serverWorld,
                           PathAwareEntity entity,
                           long time, CallbackInfoReturnable<Boolean> cir) {
        world = serverWorld;
    }

    @SuppressWarnings("target")
    @ModifyArgs(method = "method_47054(Lorg/apache/commons/lang3/mutable/MutableLong;" +
            "Lit/unimi/dsi/fastutil/longs/Long2LongMap;Lbjh;FLahm;Lbep;J)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;getTypesAndPositions(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"))
    private static void modifyShouldRunGetTypesAndPositions(Args args) {
        Predicate<BlockPos> posPredicate = args.get(1);
        Predicate<BlockPos> newBlockPosPredicate = blockPos -> {
            if (isBedOccupied(world, blockPos)) {
                return false;
            }
            return posPredicate.test(blockPos);
        };
        args.set(1, newBlockPosPredicate);
        args.set(3, CONFIG.villagersGeneralConfig.findPOIRange);
        args.set(4, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
    }

    private static boolean isBedOccupied(ServerWorld world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED);
    }
}

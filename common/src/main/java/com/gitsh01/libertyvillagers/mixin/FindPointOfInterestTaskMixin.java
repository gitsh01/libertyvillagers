package com.gitsh01.libertyvillagers.mixin;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.mutable.MutableLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.Optional;
import java.util.function.Predicate;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FindPointOfInterestTask.class)
public abstract class FindPointOfInterestTaskMixin {

    private static final long TICKS_PER_DAY = 24000;
    private static final long TIME_NIGHT = 13000;
    static private ServerWorld lastUsedWorld;

    // Calling into the lambda for Task.trigger.
    @SuppressWarnings("target")
    @Inject(method = "method_46885",
           at = @At(value = "Head"), cancellable = true)
    static private void dontFindWorkstationsAtNight(boolean onlyRunIfChild, MutableLong mutableLong,
                                                    Long2ObjectMap objectMap,
                                                    Predicate predicate, MemoryQueryResult result, Optional optional,
                                                    ServerWorld serverWorld, PathAwareEntity entity, long time,
                                                    CallbackInfoReturnable<Boolean> cir) {
        lastUsedWorld = serverWorld;
        long timeOfDay = serverWorld.getTimeOfDay() % TICKS_PER_DAY;
        // Let villagers still find beds at night.
        MemoryQueryResultAccessorMixin accessorMixin = (MemoryQueryResultAccessorMixin) ((Object) result);
        if (accessorMixin.getMemory() == MemoryModuleType.HOME) {
            return;
        }
        if (CONFIG.villagersGeneralConfig.villagersDontLookForWorkstationsAtNight &&
                entity.getType() == EntityType.VILLAGER && timeOfDay > TIME_NIGHT) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @SuppressWarnings("target")
    @ModifyArg(method = "method_46885",
               at = @At(value = "Invoke", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;" +
                    "getSortedTypesAndPositions(Ljava/util/function/Predicate;Ljava/util/function/Predicate;" +
                       "Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"),
               index = 1)
    static private Predicate<BlockPos> filterForOccupiedBeds(Predicate<BlockPos> oldPredicate) {
        Predicate<BlockPos> newPredicate = (blockPos -> {
            if (isBedOccupied(blockPos)) {
                return false;
            }
            return oldPredicate.test(blockPos);
        });
        return newPredicate;
    }

    @SuppressWarnings("target")
    @ModifyArg(method = "method_46885",
               at = @At(value = "Invoke", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;" +
                    "getSortedTypesAndPositions(Ljava/util/function/Predicate;Ljava/util/function/Predicate;" +
                    "Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"),
               index = 3)
    static private int increasePOIRange(int radius) {
        return Math.max(radius, CONFIG.villagerPathfindingConfig.findPOIRange);
    }

    private static boolean isBedOccupied(BlockPos pos) {
        BlockState blockState = lastUsedWorld.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED);
    }

    @ModifyArg(method = "findPathToPoi(Lnet/minecraft/entity/mob/MobEntity;Ljava/util/Set;)" +
            "Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;findPathTo" +
                    "(Ljava/util/Set;I)Lnet/minecraft/entity/ai/pathing/Path;"),
    index = 1)
    static private int increaseMinimumPOIClaimDistance(int distance) {
        return Math.max(distance, CONFIG.villagerPathfindingConfig.minimumPOISearchDistance);
    }
}

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
import java.util.function.BiPredicate;
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
           at = @At(value = "HEAD"), cancellable = true)
    static private void dontFindWorkstationsAtNight(boolean bl, MutableLong mutableLong, Long2ObjectMap long2ObjectMap, Predicate predicate, BiPredicate biPredicate, MemoryQueryResult memoryQueryResult, Optional optional, ServerWorld serverWorld, PathAwareEntity entity, long time, CallbackInfoReturnable<Boolean> cir) {
        lastUsedWorld = serverWorld;
        long timeOfDay = serverWorld.getTimeOfDay() % TICKS_PER_DAY;
        // Let villagers still find beds at night.
        MemoryQueryResultAccessorMixin accessorMixin = (MemoryQueryResultAccessorMixin) ((Object) memoryQueryResult);
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
    @ModifyArgs(method = "method_46885",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/poi/PointOfInterestStorage;" +
                    "getSortedTypesAndPositions(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)Ljava/util/stream/Stream;"))
    static private void filterForOccupiedBedsAndIncreasePOIRange(Args args) {
        Predicate<BlockPos> oldPredicate = args.get(1);
        Predicate<BlockPos> newPredicate = (blockPos -> {
            if (isBedOccupied(blockPos)) {
                return false;
            }
            return oldPredicate.test(blockPos);
        });
        args.set(1, newPredicate);

        int radius = args.get(3);
        args.set(3, Math.max(radius, CONFIG.villagerPathfindingConfig.findPOIRange));
    }

    private static boolean isBedOccupied(BlockPos pos) {
        BlockState blockState = lastUsedWorld.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED);
    }

    @ModifyArgs(method = "findPathToPoi(Lnet/minecraft/entity/mob/MobEntity;Ljava/util/Set;)" +
            "Lnet/minecraft/entity/ai/pathing/Path;", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;findPathTo" +
                    "(Ljava/util/Set;I)Lnet/minecraft/entity/ai/pathing/Path;"))
    static private void increaseMinimumPOIClaimDistance(Args args) {
        int distance = args.get(1);
        args.set(1, Math.max(distance, CONFIG.villagerPathfindingConfig.minimumPOISearchDistance));
    }
}

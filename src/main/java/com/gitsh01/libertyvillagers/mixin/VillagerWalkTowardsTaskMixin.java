package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerWalkTowardsTask.class)
public abstract class VillagerWalkTowardsTaskMixin {
    @ModifyVariable(method = "create(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)Lnet/minecraft/entity/ai/brain/task/SingleTickTask;",
            at = @At("HEAD"),
            ordinal = 0)
    private static int increaseCompletionRange(int completionRange) {
        return CONFIG.villagerPathfindingConfig.minimumPOISearchDistance;
    }

    @ModifyVariable(method = "create(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)Lnet/minecraft/entity/ai/brain/task/SingleTickTask;",
            at = @At("HEAD"),
            ordinal = 1)
    private static int increaseMaxDistance(int maxDistance) {
        return CONFIG.villagerPathfindingConfig.pathfindingMaxRange;
    }

    @ModifyVariable(method = "create(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)Lnet/minecraft/entity/ai/brain/task/SingleTickTask;",
            at = @At("HEAD"),
            ordinal = 2)
    private static int increaseMaxRunTime(int maxRunTime) {
        return CONFIG.villagerPathfindingConfig.walkTowardsTaskMaxRunTime;
    }
}
package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerWalkTowardsTask.class)
public abstract class VillagerWalkTowardsTaskMixin extends Task<VillagerEntity> {
    public VillagerWalkTowardsTaskMixin() {
        super(ImmutableMap.of());
    }

    @Shadow
    @Mutable
    private int maxRunTime;

    @Shadow
    @Mutable
    private int completionRange;

    @Shadow
    @Mutable
    private int maxRange;

    @Inject(method = "<init>(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)V",
    at = @At("TAIL"))
    private void increaseMaxRunTime(CallbackInfo ci) {
        maxRunTime = CONFIG.villagerPathfindingConfig.walkTowardsTaskMaxRunTime;
        completionRange = CONFIG.villagerPathfindingConfig.walkTowardsTaskMinCompletionRange;
        maxRange = CONFIG.villagerPathfindingConfig.pathfindingMaxRange;
    }
}
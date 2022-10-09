package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FindPointOfInterestTask.class)
public abstract class FindPointOfInterestTaskMixin extends Task<VillagerEntity> {

    public FindPointOfInterestTaskMixin() {
        super(ImmutableMap.of());
    }

    @ModifyConstant(
            method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            constant = @Constant(intValue = 48))
    private int injected(int value) {
        return CONFIG.findPOIRange;
    }
}
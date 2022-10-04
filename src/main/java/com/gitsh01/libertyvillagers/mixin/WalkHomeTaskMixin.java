package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkHomeTask.class)
public abstract class WalkHomeTaskMixin extends Task<LivingEntity> {

    public WalkHomeTaskMixin() {
        super(ImmutableMap.of());
    }

    @ModifyConstant(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z", constant = @Constant(intValue = 48))
    private int injectedShouldRun(int value) {
        return CONFIG.findPOIRange;
    }

    @ModifyConstant(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)V", constant = @Constant(intValue = 48))
    private int injectedRun(int value) {
        return CONFIG.findPOIRange;
    }
}
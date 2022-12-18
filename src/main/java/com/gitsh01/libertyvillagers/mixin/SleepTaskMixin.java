package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.SleepTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(SleepTask.class)
public class SleepTaskMixin {

    @ModifyConstant(
            method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            constant = @Constant(doubleValue = 2.0))
    private double replaceGetReachableBedDistance(double value) {
        return CONFIG.villagerPathfindingConfig.minimumPOISearchDistance + 1;
    }
}

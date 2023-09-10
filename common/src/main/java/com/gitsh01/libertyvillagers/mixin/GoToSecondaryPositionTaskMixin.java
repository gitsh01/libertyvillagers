package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.GoToSecondaryPositionTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.apache.commons.lang3.mutable.MutableLong;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoToSecondaryPositionTask.class)
public class GoToSecondaryPositionTaskMixin {

    @Inject(method = "method_47160", at = @At("HEAD"), cancellable = true)
    static private void dontRunIfFishing(TaskTriggerer.TaskContext<PathAwareEntity> context,
                                         MemoryQueryResult walkTarget,
                                         MemoryQueryResult secondaryPositions, int completionRange,
                                         MutableLong mutableLong, MemoryQueryResult primaryPositions, float walkSpeed,
                                         int primaryPositionActivationDistance,
                                         ServerWorld serverWorld, VillagerEntity villagerEntity, long time,
                                         CallbackInfoReturnable<Boolean> cir) {
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FISHERMAN &&
                villagerEntity.getMainHandStack().isOf(Items.FISHING_ROD)) {
                cir.setReturnValue(false);
                cir.cancel();
        }
    }
}

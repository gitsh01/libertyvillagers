package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.GoToSecondaryPositionTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoToSecondaryPositionTask.class)
public class GoToSecondaryPositionTaskMixin {

    @Inject(method = "shouldRun", at = @At("HEAD"), cancellable = true)
    void dontRunIfFishing(ServerWorld serverWorld, VillagerEntity villagerEntity, CallbackInfoReturnable<Boolean> cir) {
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FISHERMAN &&
                villagerEntity.getMainHandStack().isOf(Items.FISHING_ROD)) {
                cir.setReturnValue(false);
                cir.cancel();
        }
    }
}

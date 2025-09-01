package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.FollowCustomerTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FollowCustomerTask.class)
public class FollowCustomerTaskMixin {

    @Inject(method = "shouldRun", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldRun(ServerWorld serverWorld, VillagerEntity villager,
                                    CallbackInfoReturnable<Boolean> cir) {
        if (villager.getVillagerData().getProfession() == VillagerProfession.FISHERMAN &&
                villager.getMainHandStack().isOf(Items.FISHING_ROD)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

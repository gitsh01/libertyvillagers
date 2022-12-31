package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.task.GoToNearbyPositionTask;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GoToNearbyPositionTask.class)
public class GoToIfNearbyTaskMixin {

    @Inject(method = "shouldRun",
    at = @At("HEAD"),
    cancellable = true)
    void dontRunIfFishing(ServerWorld serverWorld, PathAwareEntity pathAwareEntity,
                          CallbackInfoReturnable<Boolean> cir){
        if (pathAwareEntity.getType() == EntityType.VILLAGER) {
            VillagerEntity villager = (VillagerEntity) pathAwareEntity;
            if (villager.getVillagerData().getProfession() == VillagerProfession.FISHERMAN &&
                    villager.getMainHandStack().isOf(Items.FISHING_ROD)) {
                    cir.setReturnValue(false);
                    cir.cancel();
            }
        }
    }
}

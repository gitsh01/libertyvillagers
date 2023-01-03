package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.FollowMobTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(FollowMobTask.class)
public class FollowMobTaskMixin {

    @Inject(method = "method_47063",
            at = @At("HEAD"),
            cancellable = true)
    static private void dontRunIfFishing(TaskTriggerer.TaskContext<PathAwareEntity> context, MemoryQueryResult result,
                          Predicate predicate, float walkSpeed, MemoryQueryResult result2,
                          ServerWorld serverWorld, LivingEntity entity, long time,
                          CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType() == EntityType.VILLAGER) {
            VillagerEntity villager = (VillagerEntity) entity;
            if (villager.getVillagerData().getProfession() == VillagerProfession.FISHERMAN &&
                    villager.getMainHandStack().isOf(Items.FISHING_ROD)) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }
}

package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WalkToNearestVisibleWantedItemTask.class)
public abstract class WalkToNearestVisibleWantedItemTaskMixin {

    @Shadow
    abstract ItemEntity getNearestVisibleWantedItem(LivingEntity entity);

    @Inject(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true)
    protected void shouldRun(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (this.getNearestVisibleWantedItem(entity).isInRange(entity, 0)) {
            // Already on top of the nearest visible item.
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}

package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(WalkToNearestVisibleWantedItemTask.class)
public abstract class WalkToNearestVisibleWantedItemTaskMixin {

    // Injecting into the lambda of the TaskTriggerer.
    @SuppressWarnings({"target", "descriptor"})
    @Inject(method = "method_46945",
            at = @At("HEAD"),
            cancellable = true)
    static private void dontMoveIfOnTopOfItem(TaskTriggerer.TaskContext context,
                                              MemoryQueryResult nearestVisibleWantedItem,
                                              MemoryQueryResult itemPickupCooldownTicks,
                                              Predicate startCondition,
                                              int radius,
                                              float speed,
                                              MemoryQueryResult walkTarget,
                                              MemoryQueryResult lookTarget,
                                              ServerWorld world,
                                              LivingEntity entity,
                                              long time,
                                              CallbackInfoReturnable<Boolean> cir) {
        if (entity.getType() != EntityType.VILLAGER) {
            return;
        }
        @SuppressWarnings("unchecked")
        ItemEntity itemEntity = (ItemEntity)context.getValue(nearestVisibleWantedItem);
        if (itemEntity.isInRange(entity, 0)) {
            // Already on top of the nearest visible item.
            cir.setReturnValue(false);
            cir.cancel();
        }
        // If our inventory is full, don't move towards the item.
        ItemStack stack = itemEntity.getStack();
        if (!((VillagerEntity)entity).getInventory().canInsert(stack)) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}

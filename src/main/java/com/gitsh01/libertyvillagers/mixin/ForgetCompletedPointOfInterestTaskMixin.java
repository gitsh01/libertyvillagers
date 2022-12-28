package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryQueryResult;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(ForgetCompletedPointOfInterestTask.class)
public abstract class ForgetCompletedPointOfInterestTaskMixin  {

    @SuppressWarnings("target")
    @Inject(method = "method_47187",
            at = @At("HEAD"),
            cancellable = true)
    private static void shouldRun(TaskTriggerer.TaskContext context, MemoryQueryResult poiPos, Predicate predicate,
                             ServerWorld world,
                             LivingEntity entity,
                             long time,
                             CallbackInfoReturnable<Boolean> cir) {
        @SuppressWarnings("unchecked")
        GlobalPos globalPos = (GlobalPos)context.getValue(poiPos);
        BlockPos blockPos = globalPos.getPos();
        // Replace isWithinDistance with the Manhattan distance to avoid being confused by beds placed near stairs.
        if (world.getRegistryKey() != globalPos.getDimension() ||  (blockPos.getManhattanDistance(entity.getBlockPos()) >= 4))  {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}


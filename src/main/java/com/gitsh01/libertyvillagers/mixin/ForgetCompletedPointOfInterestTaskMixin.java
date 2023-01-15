package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgetCompletedPointOfInterestTask.class)
public abstract class ForgetCompletedPointOfInterestTaskMixin  {

    @Shadow
    private MemoryModuleType<GlobalPos> memoryModule;

    @Inject(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true)
    protected void shouldRun(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getBrain().getOptionalMemory(this.memoryModule).isPresent()) {
            GlobalPos globalPos = entity.getBrain().getOptionalMemory(this.memoryModule).get();
            // Replace isWithinDistance with the Manhattan distance to avoid being confused by stairs.
            cir.setReturnValue(world.getRegistryKey() == globalPos.getDimension() &&
                    globalPos.getPos().getManhattanDistance(entity.getBlockPos()) < 4);
            cir.cancel();
        }
    }
}


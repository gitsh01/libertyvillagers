package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.ForgetCompletedPointOfInterestTask;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)V",
    at = @At("HEAD"))
    protected void run(ServerWorld world, LivingEntity entity, long time, CallbackInfo ci) {
        Brain<?> brain = entity.getBrain();
        if (brain.getOptionalMemory(this.memoryModule).isPresent()) {
            GlobalPos globalPos = brain.getOptionalMemory(this.memoryModule).get();
            BlockPos blockPos = globalPos.getPos();
            ServerWorld serverWorld = world.getServer().getWorld(globalPos.getDimension());
            if (serverWorld == null || this.hasCompletedPointOfInterest(serverWorld, blockPos)) {
                System.out.printf("%s Forgetting memory module %s\n", entity.getCustomName(), this.memoryModule.toString());
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            } else if (this.isBedOccupiedByOthers(serverWorld, blockPos, entity)) {
                System.out.printf("%s Someone is in my bed %s at %s\n", entity.getCustomName(), this.memoryModule.toString(),
                        blockPos.toShortString());
                brain.forget(MemoryModuleType.WALK_TARGET);
                brain.forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
            }
        }
    }

    @Shadow
    public abstract boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity);

    @Shadow
    public abstract boolean hasCompletedPointOfInterest(ServerWorld world, BlockPos pos);
}


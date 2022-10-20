package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Task.class)
public abstract class TaskMixin<E extends LivingEntity> {

    private static final int WANDER_AROUND_RUN_TIME = 20 * 60;

    @Shadow
    private Task.Status status;

    @Shadow
    private long endTime;

    public TaskMixin() {
        super();
    }

    @Inject(method = "Lnet/minecraft/entity/ai/brain/task/Task;tryStarting(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)Z",
            at = @At(value = "RETURN"))
    public void replaceTryStartingEndTime(ServerWorld world, E entity, long time, CallbackInfoReturnable<Boolean> cir) {
        if ((((Object) this).getClass() == WanderAroundTask.class) && entity.getType() == EntityType.VILLAGER &&
                this.status == Task.Status.RUNNING) {
            this.endTime = WANDER_AROUND_RUN_TIME + time;
        }
    }
}

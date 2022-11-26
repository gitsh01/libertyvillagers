package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FindWalkTargetTask;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FindWalkTargetTask.class)
public class FindWalkTargetTaskMixin {

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;J)V",
            at = @At("RETURN"))
    protected void runReturn(ServerWorld world, LivingEntity entity, long time, CallbackInfo ci) {
        if (CONFIG.debugConfig.enableVillagerWalkTargetDebug) {
            Optional<WalkTarget> walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
            walkTarget.ifPresent(
                    target -> System.out.printf("FindWalkTargetTask: %s is walking to %s\n", entity.getName(),
                            target.getLookTarget().getBlockPos().toShortString()));
        }
    }
}

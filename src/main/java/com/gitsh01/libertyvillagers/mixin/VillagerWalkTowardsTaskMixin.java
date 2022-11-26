package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerWalkTowardsTask.class)
public abstract class VillagerWalkTowardsTaskMixin extends Task<VillagerEntity> {
    public VillagerWalkTowardsTaskMixin() {
        super(ImmutableMap.of());
    }

    @Shadow
    @Mutable
    private int maxRunTime;

    @Shadow
    @Mutable
    private int completionRange;

    @Shadow
    private MemoryModuleType<GlobalPos> destination;

    @Inject(method = "<init>(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)V",
    at = @At("TAIL"))
    private void increaseMaxRunTime(CallbackInfo ci) {
        maxRunTime = CONFIG.villagersGeneralConfig.walkTowardsTaskMaxRunTime;
        completionRange = CONFIG.villagersGeneralConfig.walkTowardsTaskMinCompletionRange;
    }

    @Inject(method = "exceedsMaxRange(Lnet/minecraft/entity/passive/VillagerEntity;" +
            "Lnet/minecraft/util/dynamic/GlobalPos;)Z", at = @At("HEAD"), cancellable = true)
    private void exceedsMaxRange(VillagerEntity villager, GlobalPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(pos.getPos().getManhattanDistance(villager.getBlockPos()) >
                CONFIG.villagersGeneralConfig.pathfindingMaxRange);
        cir.cancel();
    }

    @Inject(method = "giveUp(Lnet/minecraft/entity/passive/VillagerEntity;J)V",
    at = @At("HEAD"))
    private void giveUp(VillagerEntity villager, long time, CallbackInfo ci) {
        System.out.printf("VillagerWalkTowardsTask: %s giving up %s\n", villager.getName(), destination);
    }

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;J)V",
            at = @At("RETURN"))
    protected void runReturn(ServerWorld world, VillagerEntity entity, long time, CallbackInfo ci) {
        if (CONFIG.debugConfig.enableVillagerWalkTargetDebug) {
            Optional<WalkTarget> walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
            walkTarget.ifPresent(
                    target -> System.out.printf("VillagerWalkTowardsTask: %s is walking to %s at %s from %s\n",
                            entity.getName(), destination.toString(),
                            target.getLookTarget().getBlockPos().toShortString(),
                            entity.getBlockPos().toShortString()));
        }
    }

}
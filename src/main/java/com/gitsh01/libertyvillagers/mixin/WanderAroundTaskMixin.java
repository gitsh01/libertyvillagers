package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WanderAroundTask.class)
public abstract class WanderAroundTaskMixin extends Task<MobEntity> {

    @Shadow
    @Nullable
    private Path path;

    @Shadow
    @Nullable
    private BlockPos lookTargetPos;

    @Shadow
    protected abstract boolean hasReached(MobEntity entity, WalkTarget walkTarget);

    public WanderAroundTaskMixin() {
        super(ImmutableMap.of());
    }
/*
    @Inject(
            method = "shouldKeepRunning(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/MobEntity;" +
                    "J)Z",
            at = @At(value = "HEAD"),
            cancellable = true
    )
    protected void REPLACEshouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l,
                                     CallbackInfoReturnable<Boolean> cir) {
        if (mobEntity.getType() != EntityType.VILLAGER) {
            return;
        }
        if (this.path == null || this.lookTargetPos == null) {
            System.out.println("should keep running is false");
            cir.setReturnValue(false);
            cir.cancel();
        }
        Optional<WalkTarget> optional = mobEntity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
        EntityNavigation entityNavigation = mobEntity.getNavigation();
        Boolean test = !entityNavigation.isIdle() && optional.isPresent() && !this.hasReached(mobEntity,
                optional.get());
        if (test == false) {
            System.out.printf("should keep running is (try 2) isIdle %s isPresent %s, hasReached %s\n",
                    entityNavigation.isIdle() ? "true" : "false", optional.isPresent() ? "true" : "false",
                    optional.isPresent() && this.hasReached(mobEntity, optional.get()) ? "true" : "false");
        }
        cir.setReturnValue(test);
        cir.cancel();
    }
 */
}

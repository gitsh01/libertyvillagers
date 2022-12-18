package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.WalkTowardJobSiteTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkTowardJobSiteTask.class)
public class WalkTowardJobSiteTaskMixin {

    @Inject(method = "<init>(F)V",
            at = @At("TAIL"))
    public void replaceRequiredMemoryModule(float speed, CallbackInfo ci) {
        // Prevent the task from spamming walk towards job site over and over once a walk target
        // has already been set.
        ((TaskAccessorMixin)this).setRequiredMemoryStates(
                ImmutableMap.of(MemoryModuleType.POTENTIAL_JOB_SITE, MemoryModuleState.VALUE_PRESENT,
                        MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
    }

    @Inject(method = "keepRunning",
            at = @At("HEAD"),
            cancellable = true)
    private void dontSetWalkTargetIfAlreadySet(ServerWorld serverWorld, VillagerEntity villagerEntity, long l,
                                               CallbackInfo ci) {
        // Prevent the villager from spamming the brain over and over with the same walk target.
        if (villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
            ci.cancel();
        }
    }

    @ModifyArg(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/passive/VillagerEntity;J)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/brain/task/LookTargetUtil;walkTowards" +
                    "(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/BlockPos;FI)V"), index = 3)
    private int replaceCompletionRangeInClaimSite(int completionRange) {
        return Math.max(completionRange, CONFIG.villagerPathfindingConfig.minimumPOISearchDistance);
    }
}

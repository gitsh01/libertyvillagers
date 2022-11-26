package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.WalkTowardJobSiteTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkTowardJobSiteTask.class)
public class WalkTowardJobSiteTaskMixin {

    @ModifyArg(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/passive/VillagerEntity;J)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/ai/brain/task/LookTargetUtil;walkTowards" +
                    "(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/BlockPos;FI)V"), index = 3)
    private int replaceCompletionRangeInClaimSite(int completionRange) {
        return Math.max(completionRange, CONFIG.villagersGeneralConfig.minimumPOISearchDistance);
    }

    @Inject(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/passive/VillagerEntity;J)V",
            at = @At("RETURN"))
    protected void keepRunningReturn(ServerWorld world, VillagerEntity entity, long time, CallbackInfo ci) {
        if (CONFIG.debugConfig.enableVillagerWalkTargetDebug) {
            Optional<WalkTarget> walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
            walkTarget.ifPresent(
                    target -> System.out.printf("WalkTowardsJobSiteTask: %s is walking to %s\n", entity.getName(),
                            target.getLookTarget().getBlockPos().toShortString()));
        }
    }
}

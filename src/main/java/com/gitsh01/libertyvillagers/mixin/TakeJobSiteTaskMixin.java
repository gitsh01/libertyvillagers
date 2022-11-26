package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.TakeJobSiteTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(TakeJobSiteTask.class)
public class TakeJobSiteTaskMixin {

    @ModifyArg(method = "claimSite(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/util/math/BlockPos;Z)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/LookTargetUtil;walkTowards" +
                    "(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/BlockPos;FI)V"), index = 3)
    private int replaceCompletionRangeInClaimSite(int completionRange) {
        return Math.max(completionRange, CONFIG.villagersGeneralConfig.minimumPOISearchDistance + 1);
    }

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;J)V",
            at = @At("RETURN"))
    protected void runReturn(ServerWorld world, VillagerEntity entity, long time, CallbackInfo ci) {
        if (CONFIG.debugConfig.enableVillagerWalkTargetDebug) {
            Optional<WalkTarget> walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
            walkTarget.ifPresent(target -> System.out.printf("TakeJobSiteTask: %s is walking to %s\n", entity.getName(),
                    target.getLookTarget().getBlockPos().toShortString()));
        }
    }
}

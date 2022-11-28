package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.WalkTowardJobSiteTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
}

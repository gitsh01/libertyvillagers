package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.TakeJobSiteTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

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
}

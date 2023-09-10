package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.TakeJobSiteTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(TakeJobSiteTask.class)
public class TakeJobSiteTaskMixin {

    // Injecting into a lambda in create().
    @SuppressWarnings("target")
    @ModifyArg(method = "method_47212",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/brain/task/LookTargetUtil;walkTowards" +
                    "(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/math/BlockPos;FI)V"), index = 3)
    static private int replaceCompletionRangeInClaimSite(int completionRange) {
        return Math.max(completionRange, CONFIG.villagerPathfindingConfig.minimumPOISearchDistance + 1);
    }

}

package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.passive.VillagerEntity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(VillagerWalkTowardsTask.class)
public abstract class VillagerWalkTowardsTaskMixin extends Task<VillagerEntity> {
    public VillagerWalkTowardsTaskMixin() {
        super(ImmutableMap.of());
    }

    /*
    Don't enable this function, as using the default exceeds of 100/150 blocks (for meeting/home) is preferred.
    If the location is too far away the villager uses no penalty targeting to go in the right direction a short
    distance which prevents the villager from comitting to a path and then getting frustrated when they get stuck.
    Leaving this comment here to prevent myself from making this mistake again.
    @Inject(method = "exceedsMaxRange(Lnet/minecraft/entity/passive/VillagerEntity;" +
            "Lnet/minecraft/util/dynamic/GlobalPos;)Z", at = @At("HEAD"), cancellable = true)
    private void exceedsMaxRange(VillagerEntity villager, GlobalPos pos, CallbackInfoReturnable<Boolean> cir) {
        return pos.getPos().getManhattanDistance(villager.getBlockPos()) > this.maxRange;
        cir.setReturnValue(pos.getPos().getManhattanDistance(villager.getBlockPos()) >
                CONFIG.villagersGeneralConfig.pathfindingMaxRange);
        cir.cancel();
    }
     */
}
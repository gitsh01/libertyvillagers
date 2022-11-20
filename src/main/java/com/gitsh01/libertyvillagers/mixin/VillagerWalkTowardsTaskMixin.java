package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.dynamic.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerWalkTowardsTask.class)
public abstract class VillagerWalkTowardsTaskMixin extends Task<VillagerEntity> {
    public VillagerWalkTowardsTaskMixin() {
        super(ImmutableMap.of());
    }

    @Inject(method = "exceedsMaxRange(Lnet/minecraft/entity/passive/VillagerEntity;" +
            "Lnet/minecraft/util/dynamic/GlobalPos;)Z", at = @At("HEAD"), cancellable = true)
    private void exceedsMaxRange(VillagerEntity villager, GlobalPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(pos.getPos().getManhattanDistance(villager.getBlockPos()) >
                CONFIG.villagersGeneralConfig.pathfindingMaxRange);
        cir.cancel();
    }
}
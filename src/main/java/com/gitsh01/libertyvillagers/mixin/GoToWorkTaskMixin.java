package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.GoToWorkTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(GoToWorkTask.class)
public class GoToWorkTaskMixin extends Task<VillagerEntity> {

    public GoToWorkTaskMixin() {
        super(ImmutableMap.of());
    }

    @Inject(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;" +
            ")Z", at = @At("HEAD"), cancellable = true)
    protected void shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity,
                             CallbackInfoReturnable<Boolean> cir) {
        if (villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).isPresent()) {
            BlockPos blockPos =
                    villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE).get().getPos();
            cir.setReturnValue(blockPos.isWithinDistance(villagerEntity.getPos(),
                    CONFIG.villagersGeneralConfig.minimumPOISearchDistance + 1) || villagerEntity.isNatural());
            cir.cancel();
        }
    }

    /*
    I'd rather do this, but it never seems to work correctly (no ref map).
    @ModifyArg(
            method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)Z",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3i;isWithinDistance(Lnet/minecraft/util/math/Position;D)Z"),
            index = 1)
    private double modifyDistanceInShouldRun(double distance) {
        return Math.max(distance, CONFIG.villagersGeneralConfig.minimumPOISearchDistance + 1);
    }
     */
}

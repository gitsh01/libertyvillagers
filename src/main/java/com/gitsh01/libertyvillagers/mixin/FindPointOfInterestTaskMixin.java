package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.dynamic.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FindPointOfInterestTask.class)
public abstract class FindPointOfInterestTaskMixin extends Task<PathAwareEntity> {

    private static final long TIME_NIGHT = 13000;
    private PathAwareEntity entity;
    @Shadow
    private MemoryModuleType<GlobalPos> targetMemoryModuleType;

    public FindPointOfInterestTaskMixin() {
        super(ImmutableMap.of());
    }


    @Inject(method = "shouldRun(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;)Z",
            at = @At(value = "Head"), cancellable = true)
    protected void dontFindWorkstationsAtNight(ServerWorld serverWorld, PathAwareEntity pathAwareEntity,
                                               CallbackInfoReturnable<Boolean> cir) {
        long timeOfDay = serverWorld.getTimeOfDay() % 24000;
        if (CONFIG.villagersGeneralConfig.villagersDontLookForWorkstationsAtNight &&
                pathAwareEntity.getType() == EntityType.VILLAGER &&
                (targetMemoryModuleType == MemoryModuleType.POTENTIAL_JOB_SITE ||
                        targetMemoryModuleType == MemoryModuleType.MEETING_POINT) && timeOfDay > TIME_NIGHT) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At("Head"))
    private void getLivingEntityForRun(ServerWorld world, PathAwareEntity entity, long l, CallbackInfo ci) {
        this.entity = entity;
    }

    @ModifyArg(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;" + "getSortedPositions" +
                            "(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                            "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)" +
                            "Ljava/util/stream/Stream;"), index = 3)
    protected int modifyRunGetSortedPositionsArgs(int range) {
        if (this.entity.getType() == EntityType.VILLAGER) {
            return CONFIG.villagersGeneralConfig.findPOIRange;
        }
        return range;
    }
}

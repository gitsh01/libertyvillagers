package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WanderAroundTask.class)
public abstract class WanderAroundTaskMixin {

    @Nullable
    @Shadow
    private Path path;

    private WalkTarget walkTarget = null;

    @Shadow
    abstract boolean hasReached(MobEntity entity, WalkTarget walkTarget);

    @Inject(method = "hasFinishedPath(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/entity/ai/brain/WalkTarget;J)Z",
            at = @At("HEAD"), cancellable = true)
    private void storeWalkTargetFromHasFinishedPath(MobEntity entity, WalkTarget walkTarget, long time,
                                                    CallbackInfoReturnable<Boolean> cir) {
        this.walkTarget = walkTarget;
    }

    @Inject(method = "hasFinishedPath(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/entity/ai/brain/WalkTarget;J)Z",
            at = @At("RETURN"), cancellable = true)
    private void lastDitchAttemptToFindPath(MobEntity entity, WalkTarget walkTarget, long time,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.villagersGeneralConfig.villagerWanderingFix && entity.getType() == EntityType.VILLAGER &&
                entity.getBrain().hasMemoryModule(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE) && this.path != null) {
            BlockPos blockPos = this.path.getCurrentNodePos();
            Vec3d desiredPos = new Vec3d(blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f);

            if (!this.hasReached(entity, walkTarget) && !this.path.isFinished() && (this.path.getLength() == 1) &&
                    (this.path.getCurrentNodeIndex() == 0) && !desiredPos.equals(entity.getPos()) &&
                    blockPos.isWithinDistance(entity.getPos(), 1.f)) {
                // Fix for really difficult pathing situations such as the armorer's house in the SkyVillage mod.
                Vec3d vec3d = FuzzyTargeting.findTo((PathAwareEntity) entity, 5, 5, Vec3d.ofBottomCenter(blockPos));
                if (vec3d != null) {
                    this.path = entity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                }
                cir.setReturnValue(this.path != null);
            }
        }
    }

    @ModifyArg(
            method = "hasFinishedPath(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/entity/ai/brain/WalkTarget;J)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;findPathTo" +
                    "(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;"), index = 1)
    int replaceDistanceInFindPathToInHasFinishedPath(int distance) {
        // The default of 0 means that the villagers won't try to get as close as they can to a POI where the completion
        // distance is greater than zero, so replace it with the completion range.
        if (walkTarget != null) {
            return Math.min(walkTarget.getCompletionRange(), 0);
        }
        return distance;
    }
}

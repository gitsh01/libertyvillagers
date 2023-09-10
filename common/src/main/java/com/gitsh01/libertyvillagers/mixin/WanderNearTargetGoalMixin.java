package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.WanderNearTargetGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WanderNearTargetGoal.class)
public abstract class WanderNearTargetGoalMixin {

    @Shadow
    private PathAwareEntity mob;

    void checkForValidTarget(CallbackInfoReturnable<Boolean> cir) {
        if (this.mob.getType() != EntityType.IRON_GOLEM) {
            return;
        }
        if (this.mob.getTarget() == null) {
            return;
        }
        if (CONFIG.golemsConfig.golemStayNearBell) {
            Vec3d targetPos = this.mob.getTarget().getPos();
            ServerWorld serverWorld = (ServerWorld) this.mob.getWorld();
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();

            Optional<BlockPos> nearestBell = pointOfInterestStorage.getNearestPosition(
                    poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING), this.mob.getBlockPos(),
                    2 * CONFIG.golemsConfig.golemMaxBellRange, PointOfInterestStorage.OccupationStatus.ANY);

            if (nearestBell.isPresent()) {
                BlockPos nearestBellPos = nearestBell.get();
                if (!nearestBellPos.isWithinDistance(targetPos, CONFIG.golemsConfig.golemMaxBellRange)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
        if (CONFIG.golemsConfig.golemsAvoidWater) {
            if (this.mob.getTarget().isTouchingWater()) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    private void canStartIfNotTooFarFromBell(CallbackInfoReturnable<Boolean> cir) {
        checkForValidTarget(cir);
    }

    @Inject(method = "shouldContinue", at = @At("HEAD"), cancellable = true)
    public void shouldContinueIfNotTooFarFromBell(CallbackInfoReturnable<Boolean> cir) {
        checkForValidTarget(cir);
    }
}

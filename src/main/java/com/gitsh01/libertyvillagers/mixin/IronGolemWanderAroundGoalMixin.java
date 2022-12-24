package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(IronGolemWanderAroundGoal.class)
public abstract class IronGolemWanderAroundGoalMixin extends WanderAroundGoal {

    public IronGolemWanderAroundGoalMixin() {
        super(null, 0, 240, false);
    }

    @Inject(method = "getWanderTarget", at = @At("RETURN"), cancellable = true)
    private void getWanderTargetDoesNotExceedRange(CallbackInfoReturnable<Vec3d> cir) {
        if (!CONFIG.golemsConfig.golemStayNearBell) {
            return;
        }
        Vec3d dest = cir.getReturnValue();
        if (dest == null) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld) this.mob.world;
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();

        Optional<BlockPos> nearestBell =
                pointOfInterestStorage.getNearestPosition(poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING),
                        this.mob.getBlockPos(), 2 * CONFIG.golemsConfig.golemMaxBellRange,
                        PointOfInterestStorage.OccupationStatus.ANY);

        if (nearestBell.isPresent()) {
            BlockPos nearestBellPos = nearestBell.get();
            if (!nearestBellPos.isWithinDistance(dest, CONFIG.golemsConfig.golemMaxBellRange)) {
                // Wander back towards the bell.
                dest = FuzzyTargeting.findTo(this.mob, 10, 7, Vec3d.of(nearestBellPos));
                cir.setReturnValue(dest);
                cir.cancel();
            }
        }
    }
}

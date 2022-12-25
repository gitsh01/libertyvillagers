package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.goal.IronGolemWanderAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(IronGolemWanderAroundGoal.class)
public abstract class IronGolemWanderAroundGoalMixin extends WanderAroundGoal {

    public IronGolemWanderAroundGoalMixin() {
        super(null, 0, 240, false);
    }

    @Inject(method = "getWanderTarget", at = @At("HEAD"), cancellable = true)
    private void getWanderTargetMovesToShore(CallbackInfoReturnable<Vec3d> cir) {
        if (CONFIG.golemsConfig.golemMoveToShore) {
            if (this.mob.isTouchingWater()) {
                ServerWorld serverWorld = (ServerWorld) this.mob.world;
                BlockPos blockPos = this.mob.getBlockPos();
                BlockPos.Mutable mutable = new BlockPos.Mutable();
                ShapeContext shapeContext = ShapeContext.of(this.mob);
                for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, CONFIG.golemsConfig.golemMoveToShoreRange,
                        CONFIG.golemsConfig.golemMoveToShoreRange, CONFIG.golemsConfig.golemMoveToShoreRange)) {
                    if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) continue;
                    BlockState blockState = serverWorld.getBlockState(blockPos2);
                    BlockState blockStateDown = serverWorld.getBlockState(mutable.set(blockPos2,
                            Direction.DOWN));
                    BlockState blockStateUp = serverWorld.getBlockState(mutable.set(blockPos2,
                            Direction.UP));
                    if (blockState.isOf(Blocks.WATER) || !serverWorld.getFluidState(blockPos2).isEmpty() ||
                            !blockState.getCollisionShape(serverWorld, blockPos2, shapeContext).isEmpty() ||
                            !blockStateDown.isSideSolidFullSquare(serverWorld, mutable, Direction.UP) ||
                            !blockStateUp.isAir()) continue;
                    Vec3d dest = FuzzyTargeting.findTo(this.mob, 20, 30, Vec3d.of(blockPos2));
                    cir.setReturnValue(dest);
                    cir.cancel();
                    return;
                }
            }
        }
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

    @ModifyConstant(
            method = "findVillagerPos",
            constant = @Constant(doubleValue = 32.0))
    private double replaceFindVillagerRange(double value) {
        return CONFIG.villagerPathfindingConfig.findPOIRange;
    }
}

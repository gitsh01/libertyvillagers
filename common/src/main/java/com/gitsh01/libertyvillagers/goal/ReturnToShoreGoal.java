package com.gitsh01.libertyvillagers.goal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;


import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

public class ReturnToShoreGoal extends WanderAroundGoal {

        static final private int MAX_CHANCE = 12000;
        static final private int MIN_CHANCE = 120;

        @Nullable
        private Path path = null;

        public ReturnToShoreGoal(PathAwareEntity pathAwareEntity, double speed) {
            super(pathAwareEntity, speed, MIN_CHANCE, false);
            this.ignoringChance = false;
        }

        @Override
        @Nullable
        protected Vec3d getWanderTarget() {
            if (CONFIG.golemsConfig.golemMoveToShore) {
                if (this.mob.isTouchingWater()) {
                    ServerWorld serverWorld = (ServerWorld) this.mob.getWorld();
                    BlockPos blockPos = this.mob.getBlockPos();
                    for (BlockPos blockPos2 : BlockPos.iterateOutwards(blockPos, CONFIG.golemsConfig.golemPathfindToShoreRange,
                            CONFIG.golemsConfig.golemPathfindToShoreRange, CONFIG.golemsConfig.golemPathfindToShoreRange)) {
                        if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) continue;
                        if (blockPos2.getY() < blockPos.getY()) continue;
                        BlockState blockState = serverWorld.getBlockState(blockPos2);
                        if (blockState.isOpaque()) {
                            continue;
                        }
                        if (!serverWorld.getFluidState(blockPos2).isEmpty()) {
                            continue;
                        }
                        BlockState blockStateUp = serverWorld.getBlockState(blockPos2.up());
                        if (blockStateUp.isOpaque()) {
                            continue;
                        }
                        BlockState blockStateDown = serverWorld.getBlockState(blockPos2.down());
                        if (!blockStateDown.isOpaque()) {
                            continue;
                        }
                        Path path = this.mob.getNavigation().findPathTo(blockPos2, 1);
                        if (path != null && path.getLength() > 1 && path.reachesTarget()) {
                            Vec3d dest = Vec3d.of(blockPos2);
                            this.path = path;
                            this.setChance(MIN_CHANCE);
                            return dest;
                        }
                    }
                    this.setChance(MAX_CHANCE);
                }
            }
            return null;
        }

    @Override
    public void start() {
        if (this.path == null) {
            return;
        }
        this.mob.getNavigation().startMovingAlong(this.path, this.speed);
    }

    @Override
    public boolean shouldContinue() {
            boolean shouldContinue = super.shouldContinue();
            if (!shouldContinue && !this.path.isFinished() && this.path.getCurrentNodeIndex() + 1 < this.path.getLength()) {
                // Golem stuck on the edge.
                BlockPos pos = this.path.getNodePos(this.path.getCurrentNodeIndex() + 1);
                this.mob.teleport(pos.getX(), pos.getY(), pos.getZ(), false);
                this.mob.getNavigation().startMovingAlong(path, speed);
                return true;
            }
            return shouldContinue;
    }
}

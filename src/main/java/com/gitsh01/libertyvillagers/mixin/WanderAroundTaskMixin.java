package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.ai.pathing.LandPathNodeMaker;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WanderAroundTask.class)
public abstract class WanderAroundTaskMixin {

    static private final int MAX_RUN_TIME = 20 * 60; // One minute.
    static private final long STUCK_TIME = 20 * 3; // Three seconds.

    @Nullable
    @Shadow
    private Path path;

    private WalkTarget walkTarget = null;

    @Nullable
    private BlockPos previousEntityPos = null;

    private long previousEntityPosTime;

    @Shadow
    abstract boolean hasReached(MobEntity entity, WalkTarget walkTarget);

    @Inject(method = "<init>()V",
            at = @At("TAIL"))
    public void replaceFarmerVillagerTaskRunTime(CallbackInfo ci) {
        ((TaskAccessorMixin)this).setMaxRunTime(MAX_RUN_TIME);
        ((TaskAccessorMixin)this).setMinRunTime(MAX_RUN_TIME);
    }

    @Inject(method = "hasFinishedPath(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/entity/ai/brain/WalkTarget;J)Z",
            at = @At("HEAD"))
    private void storeWalkTargetFromHasFinishedPath(MobEntity entity, WalkTarget walkTarget, long time,
                                                    CallbackInfoReturnable<Boolean> cir) {
        this.walkTarget = walkTarget;
    }

    private void checkToSeeIfVillagerHasMoved(ServerWorld serverWorld, MobEntity entity, long time) {
        BlockPos entityPos = new BlockPos(entity.getBlockX(), LandPathNodeMaker.getFeetY(serverWorld,
                entity.getBlockPos()), entity.getBlockZ());
        if (previousEntityPos == null || !previousEntityPos.isWithinDistance(entityPos, 1)) {
            previousEntityPos = entityPos;
            previousEntityPosTime = time;
            entity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        }
        if ((time - previousEntityPosTime > STUCK_TIME) && !entity.getBrain().hasMemoryModule(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)) {
            entity.getBrain().remember(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE, previousEntityPosTime);
        }
    }

    @Inject(method = "keepRunning",
            at = @At("HEAD"))
    private void keepRunningCheckToSeeIfVillagerHasMoved(ServerWorld serverWorld, MobEntity entity, long time,
                                                 CallbackInfo ci) {
        checkToSeeIfVillagerHasMoved(serverWorld,entity, time);
    }

    @Inject(method = "shouldRun",
            at = @At("HEAD"))
    protected void shouldRun(ServerWorld serverWorld, MobEntity entity, CallbackInfoReturnable<Boolean> cir) {
        long time = serverWorld.getTime();
        checkToSeeIfVillagerHasMoved(serverWorld, entity, time);
    }

    private boolean lastDitchAttemptToFindPath(MobEntity entity, long time) {
        if (CONFIG.villagersGeneralConfig.villagerWanderingFix && entity.getType() == EntityType.VILLAGER &&
                entity.getBrain().getOptionalMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE).isPresent() &&
                previousEntityPosTime > 0) {
            long cantReachWalkTargetSince = time - previousEntityPosTime;
            // Fuzzy pathing has failed, teleport.
            if (cantReachWalkTargetSince > 8 * 20) {
                Vec3d desiredPos;
                boolean shouldRun = false;
                if (this.path != null) {
                    BlockPos blockPos = this.path.getCurrentNodePos();
                    desiredPos = new Vec3d(blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f);
                    shouldRun = true;
                } else {
                    BlockPos blockPos = this.walkTarget.getLookTarget().getBlockPos();
                    desiredPos = new Vec3d(blockPos.getX() + 0.5f, blockPos.getY(), blockPos.getZ() + 0.5f);
                }
                entity.teleport(desiredPos.x, desiredPos.y, desiredPos.z, true);
                entity.getBrain().forget(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                this.previousEntityPosTime = 0;
                return shouldRun;
            } else if (cantReachWalkTargetSince > 3 * 20) {
                // Fix for really difficult pathing situations such as the armorer's house in the SkyVillage mod using
                // fuzzy pathing to wiggle out of the area.
                Vec3d vec3d = FuzzyTargeting.findTo((PathAwareEntity) entity, 5, 5,
                        Vec3d.ofBottomCenter(this.walkTarget.getLookTarget().getBlockPos()));
                if (vec3d != null) {
                    this.path = entity.getNavigation().findPathTo(vec3d.x, vec3d.y, vec3d.z, 0);
                }
                return this.path != null;
            }
        }
        return false;
    }

    @Inject(method = "keepRunning",
            at = @At("TAIL"))
    private void keepRunninglastDitchAttemptToFindPath(ServerWorld serverWorld, MobEntity entity, long time,
                                                 CallbackInfo ci) {
        if (this.walkTarget == null) {
            if (entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).isEmpty()) {
                return;
            }
            this.walkTarget = entity.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET).get();
        }
        lastDitchAttemptToFindPath(entity, time);
    }

    @Inject(method = "hasFinishedPath(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/entity/ai/brain/WalkTarget;J)Z",
            at = @At("RETURN"), cancellable = true)
    private void hasFinishedPathLastDitchAttemptToFindPath(MobEntity entity, WalkTarget walkTarget, long time,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (lastDitchAttemptToFindPath(entity, time)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

        @ModifyArg(
                method = "hasFinishedPath(Lnet/minecraft/entity/mob/MobEntity;Lnet/minecraft/entity/ai/brain/WalkTarget;J)Z",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ai/pathing/EntityNavigation;findPathTo" +
                        "(Lnet/minecraft/util/math/BlockPos;I)Lnet/minecraft/entity/ai/pathing/Path;"), index = 1)
        int replaceDistanceInFindPathToInHasFinishedPath ( int distance){
            // Fix for villagers being unable to path to POIs where that are surrounded by blocks except for one side.
            // VillagerWalkTowardsTask uses manhattan distance, FindPathTo uses crow-flies distance. Setting the
            // distance to 1 means that positions all around the POI are valid, but still within the manhattan distance
            // of 3 (assuming VillagerWalkTowardsTask uses 3).
            if (walkTarget.getLookTarget() instanceof BlockPosLookTarget) {
                return Math.max(1, distance);
            }
            return distance;
        }
    }

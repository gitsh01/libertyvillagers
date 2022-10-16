package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.WalkHomeTask;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(WalkHomeTask.class)
public abstract class WalkHomeTaskMixin extends Task<LivingEntity> {

    private static final int POI_EXPIRY = 400;
    private static final int RUN_TIME = 2000;
    private static final int MAX_DISTANCE = 3;
    private static final int MAX_TRIES = 20;

    public WalkHomeTaskMixin() {
        super(ImmutableMap.of());
    }

    @Accessor
    public abstract long getExpiryTimeLimit();

    @Accessor("expiryTimeLimit")
    public abstract void setExpiryTimeLimit(long expiryTimeLimit);

    @Accessor
    public abstract int getTries();

    @Accessor("tries")
    public abstract void setTries(int tries);

    @Accessor
    public abstract float getSpeed();

    @Accessor("speed")
    public abstract void setSpeed(float speed);

    @Accessor
    public abstract Long2LongMap getPositionToExpiry();

    @Inject(method = "shouldRun", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldRun(ServerWorld world, LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (world.getTime() - this.getExpiryTimeLimit() < RUN_TIME) {
            cir.setReturnValue(false);
            cir.cancel();
            return;
        }
        PathAwareEntity pathAwareEntity = (PathAwareEntity) entity;
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Optional<BlockPos> optional =
                pointOfInterestStorage.getNearestPosition(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME),
                        entity.getBlockPos(), CONFIG.villagersGeneralConfig.findPOIRange,
                        PointOfInterestStorage.OccupationStatus.HAS_SPACE);
        cir.setReturnValue(optional.isPresent() &&
                !(optional.get().getSquaredDistance(pathAwareEntity.getBlockPos()) <= MAX_DISTANCE));
        cir.cancel();
    }


    @Inject(method = "run", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceRun(ServerWorld world, LivingEntity entity, long time, CallbackInfo cir) {
        Predicate<BlockPos> predicate;
        this.setTries(0);
        this.setExpiryTimeLimit(world.getTime() + (long) world.getRandom().nextInt(RUN_TIME));
        PathAwareEntity pathAwareEntity = (PathAwareEntity) entity;
        PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
        Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set =
                pointOfInterestStorage.getTypesAndPositions(poiType -> poiType.matchesKey(PointOfInterestTypes.HOME),
                                predicate = pos -> {
                                    long l = pos.asLong();
                                    if (this.getPositionToExpiry().containsKey(l)) {
                                        return false;
                                    }
                                    this.setTries(this.getTries() + 1);
                                    if (this.getTries() >= MAX_TRIES) {
                                        return false;
                                    }
                                    this.getPositionToExpiry().put(l, this.getExpiryTimeLimit() + POI_EXPIRY);
                                    return true;
                                }, entity.getBlockPos(), CONFIG.villagersGeneralConfig.findPOIRange,
                                PointOfInterestStorage.OccupationStatus.HAS_SPACE)
                        .collect(Collectors.toSet());
        Path path = FindPointOfInterestTask.findPathToPoi(pathAwareEntity, set);
        if (path != null && path.reachesTarget()) {
            BlockPos blockPos = path.getTarget();
            Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(blockPos);
            if (optional.isPresent()) {
                entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(blockPos, this.getSpeed(), 1));
                DebugInfoSender.sendPointOfInterest(world, blockPos);
            }
        } else if (this.getTries() < MAX_TRIES) {
            this.getPositionToExpiry().long2LongEntrySet()
                    .removeIf(entry -> entry.getLongValue() < this.getExpiryTimeLimit());
            cir.cancel();
        }
    }
}

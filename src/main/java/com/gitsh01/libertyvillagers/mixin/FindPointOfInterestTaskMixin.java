package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FindPointOfInterestTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FindPointOfInterestTask.class)
public abstract class FindPointOfInterestTaskMixin extends Task<PathAwareEntity> {

    private static final long TICKS_PER_DAY = 24000;
    private static final long TIME_NIGHT = 13000;
    private PathAwareEntity entity;
    @Shadow
    private Predicate<RegistryEntry<PointOfInterestType>> poiTypePredicate;
    @Shadow
    private MemoryModuleType<GlobalPos> targetMemoryModuleType;
    @Shadow
    private boolean onlyRunIfChild;
    @Shadow
    private Optional<Byte> entityStatus;
    @Shadow
    private long positionExpireTimeLimit;
    @Shadow
    private Long2ObjectMap<Object> foundPositionsToExpiry;

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
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;" + "getSortedTypesAndPositions" +
                            "(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                            "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)" +
                            "Ljava/util/stream/Stream;"), index = 3)
    protected int modifyRunGetSortedTypesAndPositionsArgs(int range) {
        if (this.entity.getType() == EntityType.VILLAGER) {
            return CONFIG.villagersGeneralConfig.findPOIRange;
        }
        return range;
    }

    @Inject(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At(value = "Head"), cancellable = true)
    protected void debugPOILogicInRun(ServerWorld serverWorld, PathAwareEntity pathAwareEntity, long l,
                                      CallbackInfo ci) {
        if (!CONFIG.debugConfig.enableVillagerFindPOIDebug) {
            return;
        }
        System.out.printf("%s TargetMemoryModule is %s\n", pathAwareEntity.getName(),
                this.targetMemoryModuleType.toString());
        this.positionExpireTimeLimit = l + 20L + (long) serverWorld.getRandom().nextInt(20);
        PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
        Predicate<BlockPos> predicate = pos -> {
            return true;
        };
        Set<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> set =
                pointOfInterestStorage.getSortedTypesAndPositions(this.poiTypePredicate, predicate,
                        pathAwareEntity.getBlockPos(), CONFIG.villagersGeneralConfig.findPOIRange,
                        PointOfInterestStorage.OccupationStatus.HAS_SPACE).limit(5L).collect(Collectors.toSet());
        System.out.printf("Number of POI in the set %d - ", set.size());
        Path path = FindPointOfInterestTask.findPathToPoi(pathAwareEntity, set);
        if (path != null && path.reachesTarget()) {
            System.out.printf("Able to reach target %s\n", path.getTarget().toShortString());
        } else if (path == null) {
            System.out.printf("Path is null, starting from %s\n", pathAwareEntity.getBlockPos().toShortString());
        } else {
            System.out.printf("Unable to reach target %s from %s\n", path.getEnd().getBlockPos().toShortString(),
                    pathAwareEntity.getBlockPos().toShortString());
        }
    }
}

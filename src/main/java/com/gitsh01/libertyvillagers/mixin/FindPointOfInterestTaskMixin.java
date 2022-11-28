package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
    private ServerWorld world;
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
        this.world = world;
    }

    @Redirect(method = "run(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/mob/PathAwareEntity;J)V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/poi/PointOfInterestStorage;" + "getSortedPositions" +
                            "(Ljava/util/function/Predicate;Ljava/util/function/Predicate;Lnet/minecraft/util/math/BlockPos;" +
                            "ILnet/minecraft/world/poi/PointOfInterestStorage$OccupationStatus;)" +
    public Stream<Pair<RegistryEntry<PointOfInterestType>, BlockPos>> modifyGetSortedTypesAndPositions(
            PointOfInterestStorage pointOfInterestStorage, Predicate<RegistryEntry<PointOfInterestType>> typePredicate,
            Predicate<BlockPos> posPredicate, BlockPos pos, int radius,
            PointOfInterestStorage.OccupationStatus occupationStatus) {
        Predicate<BlockPos> newBlockPosPredicate = blockPos -> {
            if (isBedOccupiedByOthers(this.world, blockPos, this.entity)) {
                System.out.printf("%s Ignoring bed %s because it is occupied.\n", this.entity.getCustomName(),
                        blockPos.toShortString());
                return false;
            }
            return posPredicate.test(blockPos);
        };
        return pointOfInterestStorage.getSortedTypesAndPositions(typePredicate, newBlockPosPredicate, pos,
                CONFIG.villagersGeneralConfig.findPOIRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE);
    }

    private boolean isBedOccupiedByOthers(ServerWorld world, BlockPos pos, LivingEntity entity) {
        BlockState blockState = world.getBlockState(pos);
        return blockState.isIn(BlockTags.BEDS) && blockState.get(BedBlock.OCCUPIED) && !entity.isSleeping();
    }
}

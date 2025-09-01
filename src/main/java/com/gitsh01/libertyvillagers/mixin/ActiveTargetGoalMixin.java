package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Predicate;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoal {

    @Shadow
    protected TargetPredicate targetPredicate;

    public ActiveTargetGoalMixin(MobEntity mob) {
        super(mob, false);
    }

    @Inject(method = "<init>(Lnet/minecraft/entity/mob/MobEntity;Ljava/lang/Class;IZZLnet/minecraft/entity/ai/TargetPredicate$EntityPredicate;)V",
            at = @At("RETURN"))
    void changeAngerDistanceForIronGolems(MobEntity mob, Class targetClass, int reciprocalChance, boolean checkVisibility, boolean checkCanNavigate, TargetPredicate.EntityPredicate targetPredicate, CallbackInfo ci) {
        if (mob.getType() == EntityType.IRON_GOLEM) {
            this.targetPredicate.setBaseMaxDistance(CONFIG.golemsConfig.golemAggroRange);
        }
    }

    @Inject(method = "canStart", at = @At("HEAD"), cancellable = true)
    public void canStartIfNotTooFarFromBell(CallbackInfoReturnable<Boolean> cir) {
        if (mob.getType() == EntityType.IRON_GOLEM && CONFIG.golemsConfig.golemStayNearBell) {
            ServerWorld serverWorld = (ServerWorld) this.mob.getWorld();
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();

            Optional<BlockPos> nearestBell = pointOfInterestStorage.getNearestPosition(
                    poiType -> poiType.matchesKey(PointOfInterestTypes.MEETING), this.mob.getBlockPos(),
                    2 * CONFIG.golemsConfig.golemMaxBellRange, PointOfInterestStorage.OccupationStatus.ANY);

            if (nearestBell.isPresent()) {
                BlockPos nearestBellPos = nearestBell.get();
                if (!nearestBellPos.isWithinDistance(this.mob.getBlockPos(), CONFIG.golemsConfig.golemMaxBellRange)) {
                    cir.setReturnValue(false);
                    cir.cancel();
                }
            }
        }
    }

    @ModifyVariable(method = "getSearchBox(D)Lnet/minecraft/util/math/Box;", at = @At("HEAD"), ordinal = 0)
    private double replaceSearchBoxForIronGolems(double distance) {
        if (mob.getType() == EntityType.IRON_GOLEM) {
            return CONFIG.golemsConfig.golemAggroRange;
        }
        return distance;
    }
}

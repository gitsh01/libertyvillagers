package com.gitsh01.libertyvillagers.mixin;

import com.gitsh01.libertyvillagers.goal.ReturnToShoreGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(IronGolemEntity.class)
public abstract class IronGolemEntityMixin extends PathAwareEntity {

    public IronGolemEntityMixin(EntityType<? extends GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void avoidCactus(EntityType<? extends GolemEntity> entityType, World world, CallbackInfo ci) {
        if (CONFIG.golemsConfig.golemsAvoidCactus) {
            this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, -1);
        }
        if (CONFIG.golemsConfig.golemsAvoidWater) {
            this.setPathfindingPenalty(PathNodeType.WATER, 16);
            this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 8);
        }
        if (CONFIG.golemsConfig.golemsAvoidRail) {
            this.setPathfindingPenalty(PathNodeType.RAIL, -1);
        }
        if (CONFIG.golemsConfig.golemsAvoidTrapdoor) {
            this.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        }
        if (CONFIG.golemsConfig.golemsAvoidPowderedSnow) {
            this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1);
            this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, 16);
        }
    }

    @Inject(at = @At("HEAD"), method = "canTarget", cancellable = true)
    public void replaceCanTarget(EntityType<?> type, CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.golemsConfig.golemsDontAttackPlayer && type == EntityType.PLAYER) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "initGoals",
        at = @At("HEAD"),
        cancellable = true)
    protected void initGoalsAddReturnToShore(CallbackInfo ci) {
        this.goalSelector.add(1, new ReturnToShoreGoal(this, 1.0));
    }
}
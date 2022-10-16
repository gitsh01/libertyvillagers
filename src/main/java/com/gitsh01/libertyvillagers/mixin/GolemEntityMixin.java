package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(GolemEntity.class)
public abstract class GolemEntityMixin extends PathAwareEntity {

    public GolemEntityMixin(EntityType<? extends net.minecraft.entity.passive.GolemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void avoidCactus(EntityType<? extends GolemEntity> entityType, World world, CallbackInfo ci) {
        if (CONFIG.golemsConfig.golemsAvoidCactus) {
            this.setPathfindingPenalty(PathNodeType.DANGER_CACTUS, -1);
        }

        if (CONFIG.golemsConfig.golemsAvoidWater) {
            this.setPathfindingPenalty(PathNodeType.WATER, -1);
            this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16);
        }
    }
}
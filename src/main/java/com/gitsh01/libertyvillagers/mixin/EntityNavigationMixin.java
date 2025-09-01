package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityNavigation.class)
public abstract class EntityNavigationMixin {

    @Shadow
    private MobEntity entity;

    @Shadow
    private Path currentPath;

    @Inject(method = "continueFollowingPath", at = @At("HEAD"), cancellable = true)
    protected void continueFollowingPath(CallbackInfo ci) {
        if (this.entity.getType() == EntityType.VILLAGER || this.entity.getType() == EntityType.IRON_GOLEM) {
            float tempNodeReachProximity = this.entity.getWidth() > 0.75f ? this.entity.getWidth() / 2.0f :
                    0.75f - this.entity.getWidth() / 2.0f;
            BlockPos vec3i = this.currentPath.getCurrentNodePos();
            double d = Math.abs(this.entity.getX() - ((double) vec3i.getX() + 0.5));
            double f = Math.abs(this.entity.getZ() - ((double) vec3i.getZ() + 0.5));
            double g = d * d + f * f;
            // Prevent the case where the villager needs to make an u turn to get up a set of
            // steep stairs from a slab, but decides they are "close enough" and attempts to jump
            // to a stair that is too high. Using the pythagorean theorem to determine distance from
            // next node.
            if (g >= (double) (tempNodeReachProximity * tempNodeReachProximity)) {
                ci.cancel();
            }
        }
    }
}
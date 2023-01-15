package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(BeeEntity.class)
public abstract class BeeEntityMixin extends AnimalEntity {

    public BeeEntityMixin(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void BeeInit(EntityType<? extends BeeEntity> entityType, World world, CallbackInfo ci) {
        if (CONFIG.animalsConfig.beesAvoidTrapdoors) {
            this.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        }
    }
}

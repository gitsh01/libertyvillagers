package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void avoidCactus(EntityType<? extends MerchantEntity> entityType, World world, CallbackInfo ci) {
        if (CONFIG.villagersAvoidCactus) {
            this.setPathfindingPenalty(PathNodeType.DANGER_CACTUS, 16);
        }
        if (CONFIG.villagersAvoidWater) {
            this.setPathfindingPenalty(PathNodeType.WATER, -1);
            this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16);
        }
    }

    @Inject(at = @At("HEAD"), method = "wakeUp()V")
    private void healOnWakeUp(CallbackInfo info) {
        if (CONFIG.healOnWake) {
            // Heal villager upon waking up.
            this.heal(this.getMaxHealth());
        }
    }

    @Inject(at = @At("HEAD"), method = "isReadyToBreed()Z", cancellable = true)
    public void replaceIsReadyToBreed(CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.villagersDontBreed) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
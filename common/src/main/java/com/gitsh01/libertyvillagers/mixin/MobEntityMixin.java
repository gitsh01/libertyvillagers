package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends LivingEntity {

    public MobEntityMixin(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "getSafeFallDistance", at = @At(value = "HEAD"), cancellable = true)
    public void replaceGetSafeFallDistance(CallbackInfoReturnable<Integer> cir) {
        LivingEntity entity = this;
        if (entity.getType() == EntityType.VILLAGER) {
            cir.setReturnValue(CONFIG.villagerPathfindingConfig.villagerSafeFallDistance);
            cir.cancel();
        }
    }
}
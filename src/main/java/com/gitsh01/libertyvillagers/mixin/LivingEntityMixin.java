package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "isClimbing", at = @At(value = "HEAD"), cancellable = true)
    public void replaceIsClimbing(CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity == null) return;
        if (entity.getType() == EntityType.VILLAGER && CONFIG.villagerPathfindingConfig.villagersDontClimb) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (entity.getType() == EntityType.IRON_GOLEM && CONFIG.golemsConfig.golemsDontClimb) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (entity.getType() == EntityType.CAT && CONFIG.catsConfig.catsDontClimb) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", at = @At(value = "HEAD"),
            cancellable = true)
    public void replaceAttributeValueForVillagersAndGolems(EntityAttribute attribute,
                                                         CallbackInfoReturnable<Double> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity == null) return;
        if ((entity.getType() == EntityType.VILLAGER || entity.getType() == EntityType.IRON_GOLEM) &&
                attribute == EntityAttributes.GENERIC_FOLLOW_RANGE) {
            cir.setReturnValue((double) CONFIG.villagerPathfindingConfig.findPOIRange);
            cir.cancel();
        }
    }
}
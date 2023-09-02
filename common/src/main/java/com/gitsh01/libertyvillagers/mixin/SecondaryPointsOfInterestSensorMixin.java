package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.sensor.SecondaryPointsOfInterestSensor;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(SecondaryPointsOfInterestSensor.class)
public class SecondaryPointsOfInterestSensorMixin {

    private VillagerEntity villagerEntity;

    @Inject(method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)V",
            at = @At("HEAD"))
    protected void getLocalVariablesFromSense(ServerWorld serverWorld, VillagerEntity villagerEntity, CallbackInfo ci) {
        this.villagerEntity = villagerEntity;
    }

    @ModifyConstant(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)V",
            constant = @Constant(intValue = 4))
    private int replacePosXZ(int xz) {
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            return CONFIG.villagersProfessionConfig.findCropRangeHorizontal;
        }
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FISHERMAN) {
            return CONFIG.villagersProfessionConfig.fishermanFindWaterRange;
        }
        return xz;
    }

    @ModifyConstant(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)V",
            constant = @Constant(intValue = -4))
    private int replaceNegXZ(int xz) {
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            return -1 * CONFIG.villagersProfessionConfig.findCropRangeHorizontal;
        }
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FISHERMAN) {
            return -1 * CONFIG.villagersProfessionConfig.fishermanFindWaterRange;
        }
        return xz;
    }

    @ModifyConstant(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)V",
            constant = @Constant(intValue = 2))
    private int replacePosY(int y) {
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            return CONFIG.villagersProfessionConfig.findCropRangeVertical;
        }
        return y;
    }

    @ModifyConstant(
            method = "sense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)V",
            constant = @Constant(intValue = -2))
    private int replaceNegY(int y) {
        if (villagerEntity.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            return -1 * CONFIG.villagersProfessionConfig.findCropRangeVertical;
        }
        return y;
    }
}


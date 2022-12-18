package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.ai.brain.task.VillagerBreedTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerBreedTask.class)
public abstract class VillagerBreedTaskMixin {

    @Shadow
    abstract boolean canReachHome(VillagerEntity villager, BlockPos pos, RegistryEntry<PointOfInterestType> poiType);

    @Inject(method = "goHome(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/entity/passive/VillagerEntity;)V",
            at = @At("HEAD"), cancellable = true)
    private void goHome(ServerWorld world, VillagerEntity first, VillagerEntity second, CallbackInfo ci) {
        if (CONFIG.villagersGeneralConfig.villagerBabiesRequireWorkstationAndBed) {
            Optional<BlockPos> optionalWorkstation = world.getPointOfInterestStorage()
                    .getPosition(VillagerProfession.NONE.acquirableWorkstation(),
                            (poiType, pos) -> this.canReachHome(first, pos, poiType), first.getBlockPos(),
                            CONFIG.villagerPathfindingConfig.findPOIRange);
            if (optionalWorkstation.isEmpty()) {
                world.sendEntityStatus(second, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
                world.sendEntityStatus(first, EntityStatuses.ADD_VILLAGER_ANGRY_PARTICLES);
                ci.cancel();
            }
        }
    }

    @ModifyConstant(
            method = "getReachableHome(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;)Ljava/util/Optional;",
            constant = @Constant(intValue = 48))
    private int replaceGetReachableHomeDistance(int value) {
        return CONFIG.villagerPathfindingConfig.findPOIRange;
    }


    @ModifyConstant(
            method = "createChild(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/passive/VillagerEntity;Lnet/minecraft/entity/passive/VillagerEntity;)Ljava/util/Optional;",
            constant = @Constant(intValue = -24000))
    private int replaceBreedingAgeForBaby(int breedingAge) {
        return -1 * CONFIG.villagersGeneralConfig.growUpTime;
    }
}
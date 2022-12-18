package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.world.spawner.CatSpawner;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(CatSpawner.class)
public class CatSpawnerMixin {

    @ModifyConstant(
            method = "spawnInHouse(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)I",
            constant = @Constant(doubleValue = 48.0))
    private double replaceCatSpawnDistanceXZ(double value) {
        if (CONFIG.catsConfig.catsSpawnLimit) {
            return CONFIG.catsConfig.catsSpawnLimitRange;
        }
        return value;
    }

    @ModifyConstant(
            method = "spawnInHouse(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)I",
            constant = @Constant(doubleValue = 8.0))
    private double replaceCatSpawnDistanceY(double value) {
        if (CONFIG.catsConfig.catsSpawnLimit) {
            return CONFIG.catsConfig.catsSpawnLimitRange;
        }
        return value;
    }

    @ModifyConstant(
            method = "spawnInHouse(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)I",
            constant = @Constant(intValue = 5))
    private int replaceCatSpawnLimitCount(int value) {
        if (CONFIG.catsConfig.catsSpawnLimit) {
            return CONFIG.catsConfig.catsSpawnLimitCount;
        }
        return value;
    }
}

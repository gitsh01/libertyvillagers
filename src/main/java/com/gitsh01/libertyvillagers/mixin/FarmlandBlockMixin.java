package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FarmlandBlock.class)
public class FarmlandBlockMixin extends Block {

    public FarmlandBlockMixin(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Inject(method = "onLandedUpon(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;" +
            "Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/entity/Entity;F)V",
    at = @At("HEAD"),
    cancellable = true)
    public void villagerDontTrample(World world, BlockState state, BlockPos pos, Entity entity,
                                    float fallDistance, CallbackInfo ci) {
        if ((CONFIG.villagersGeneralConfig.villagersDontTrampleCrops && entity instanceof VillagerEntity) ||
            (CONFIG.golemsConfig.golemsDontTrampleCrops && entity instanceof GolemEntity)) {
            super.onLandedUpon(world, state, pos, entity, fallDistance);
            ci.cancel();
        }
    }
}

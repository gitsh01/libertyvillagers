package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(EntityNavigation.class)
public abstract class EntityNavigationMixin {

    @Accessor
    public abstract MobEntity getEntity();

    @Accessor
    public abstract World getWorld();

    @Accessor
    public abstract Path getCurrentPath();

    public EntityNavigationMixin(MobEntity entity, World world) {
    }

    @Inject(method = "adjustPath", at = @At(value = "TAIL"))
    public void checkForCactus(CallbackInfo ci) {
        if ((!CONFIG.golemsConfig.golemsAvoidCactus && !CONFIG.golemsConfig.golemsAvoidWater) ||
                this.getEntity().getType() != EntityType.IRON_GOLEM) {
            return;
        }

        for (int i = 0; i < this.getCurrentPath().getLength(); ++i) {
            PathNode pathNode = this.getCurrentPath().getNode(i);
            BlockPos pos = new BlockPos(pathNode.x, pathNode.y, pathNode.z);
            BlockState blockState1 = this.getWorld().getBlockState(pos);
            if (CONFIG.golemsConfig.golemsAvoidCactus && blockState1.isOf(Blocks.CACTUS)) {
                this.getCurrentPath().setLength(i);
                return;
            }
            if (CONFIG.golemsConfig.golemsAvoidWater && blockState1.getMaterial().isLiquid()) {
                this.getCurrentPath().setLength(i);
                return;
            }
            for (Direction direction : Direction.Type.HORIZONTAL) {
                for (int j = 0; j < 2; j++) {
                    BlockState blockState2 = this.getWorld().getBlockState(pos.offset(direction, j));
                    if (CONFIG.golemsConfig.golemsAvoidCactus && blockState2.isOf(Blocks.CACTUS)) {
                        this.getCurrentPath().setLength(i);
                        return;
                    }
                    if (CONFIG.golemsConfig.golemsAvoidWater && blockState2.getMaterial().isLiquid()) {
                        this.getCurrentPath().setLength(i);
                        return;
                    }
                }
            }
        }
    }
}
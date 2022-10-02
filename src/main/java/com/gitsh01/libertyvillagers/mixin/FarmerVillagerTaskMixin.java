package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(FarmerVillagerTask.class)
public abstract class FarmerVillagerTaskMixin extends Task<VillagerEntity> {
    @Accessor
    abstract List<BlockPos> getTargetPositions();

    @Accessor
    abstract BlockPos getCurrentTarget();

    @Accessor("currentTarget")
    public abstract void setCurrentTarget(BlockPos currentTarget);

    @Invoker("chooseRandomTarget")
    public abstract BlockPos invokeChooseRandomTarget(ServerWorld world);

    public FarmerVillagerTaskMixin() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.SECONDARY_JOB_SITE, MemoryModuleState.VALUE_PRESENT));
    }

    @Inject(method = "shouldRun", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity, CallbackInfoReturnable<Boolean> cir) {
        if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            cir.setReturnValue(false);
            cir.cancel();
        } else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            cir.setReturnValue(false);
            cir.cancel();
        } else {
            BlockPos.Mutable mutable = villagerEntity.getBlockPos().mutableCopy();
            this.getTargetPositions().clear();

            for(int i = -10; i <= 10; ++i) {
                for(int j = -1; j <= 1; ++j) {
                    for(int k = -10; k <= 10; ++k) {
                        mutable.set(villagerEntity.getX() + (double)i, villagerEntity.getY() + (double)j, villagerEntity.getZ() + (double)k);
                        if (this.replaceIsSuitableTarget(mutable, serverWorld, villagerEntity)) {
                            this.getTargetPositions().add(new BlockPos(mutable));
                        }
                    }
                }
            }

            this.setCurrentTarget(this.invokeChooseRandomTarget(serverWorld));
            cir.setReturnValue(this.getCurrentTarget() != null);
            cir.cancel();
        }
    }

    protected boolean replaceIsSuitableTarget(BlockPos pos, ServerWorld world, VillagerEntity villagerEntity) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.down()).getBlock();
        return ((block instanceof CropBlock && ((CropBlock)block).isMature(blockState)) ||
                (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()));
    }

    @Inject(method = "shouldKeepRunning", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.getCurrentTarget() != null);
        cir.cancel();
    }
}
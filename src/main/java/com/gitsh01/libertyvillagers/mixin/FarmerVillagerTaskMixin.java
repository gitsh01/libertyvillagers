package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.block.*;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.GameRules;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FarmerVillagerTask.class)
public abstract class FarmerVillagerTaskMixin extends Task<VillagerEntity> {
    public FarmerVillagerTaskMixin() {
        super(ImmutableMap.of());
    }

    @Shadow
    @Nullable
    private BlockPos currentTarget;

    @Shadow
    private long nextResponseTime;

    @Shadow
    private int ticksRan;

    @Shadow
    private List<BlockPos> targetPositions;

    @Shadow
    @Nullable
    abstract BlockPos chooseRandomTarget(ServerWorld world);

    @Inject(method = "shouldRun", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity,
                                    CallbackInfoReturnable<Boolean> cir) {
        if (!serverWorld.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            cir.setReturnValue(false);
            cir.cancel();
        } else if (villagerEntity.getVillagerData().getProfession() != VillagerProfession.FARMER) {
            cir.setReturnValue(false);
            cir.cancel();
        } else {
            BlockPos.Mutable mutable = villagerEntity.getBlockPos().mutableCopy();
            this.targetPositions.clear();

            for (int i = -1 * CONFIG.villagersProfessionConfig.findCropRangeHorizontal;
                 i <= CONFIG.villagersProfessionConfig.findCropRangeHorizontal; ++i) {
                for (int j = -1 * CONFIG.villagersProfessionConfig.findCropRangeVertical;
                     j <= CONFIG.villagersProfessionConfig.findCropRangeVertical; ++j) {
                    for (int k = -CONFIG.villagersProfessionConfig.findCropRangeHorizontal;
                         k <= CONFIG.villagersProfessionConfig.findCropRangeHorizontal; ++k) {
                        mutable.set(villagerEntity.getX() + (double) i, villagerEntity.getY() + (double) j,
                                villagerEntity.getZ() + (double) k);
                        if (this.replaceIsSuitableTarget(mutable, serverWorld, villagerEntity)) {
                            this.targetPositions.add(new BlockPos(mutable));
                        }
                    }
                }
            }

            this.currentTarget = this.chooseRandomTarget(serverWorld);
            cir.setReturnValue(this.currentTarget != null);
            cir.cancel();
        }
    }

    @Inject(method = "keepRunning", at = @At(value = "HEAD"), cancellable = true)
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l, CallbackInfo cir) {
        if (!CONFIG.villagersProfessionConfig.preferPlantSameCrop) {
            // Use default logic.
            return;
        }

        Item preferredSeeds = null;
        BlockPos currentTarget = this.currentTarget;
        if (currentTarget == null || currentTarget.isWithinDistance(villagerEntity.getPos(), 1.0)) {
            if (currentTarget != null && l > this.nextResponseTime) {
                BlockState blockState = serverWorld.getBlockState(currentTarget);
                Block block = blockState.getBlock();
                Block block2 = serverWorld.getBlockState(currentTarget.down()).getBlock();
                if (block instanceof CropBlock && ((CropBlock) block).isMature(blockState)) {
                    if (block instanceof BeetrootsBlock) {
                        preferredSeeds = Items.BEETROOT_SEEDS;
                    } else if (block instanceof PotatoesBlock) {
                        preferredSeeds = Items.POTATO;
                    } else if (block instanceof CarrotsBlock) {
                        preferredSeeds = Items.CARROT;
                    } else {
                        preferredSeeds = Items.WHEAT_SEEDS;
                    }

                    serverWorld.breakBlock(currentTarget, true, villagerEntity);
                    blockState = serverWorld.getBlockState(currentTarget);
                    block = blockState.getBlock();
                }

                if (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()) {
                    SimpleInventory simpleInventory = villagerEntity.getInventory();

                    // First look for the preferred seed.
                    boolean plantedPreferredSeeds = false;
                    if (preferredSeeds != null) {
                        for (int i = 0; i < simpleInventory.size(); ++i) {
                            ItemStack itemStack = simpleInventory.getStack(i);
                            if (!itemStack.isEmpty() && itemStack.isOf(preferredSeeds)) {
                                plantedPreferredSeeds = plantSeed(itemStack, i, serverWorld, villagerEntity);
                                if (plantedPreferredSeeds) {
                                    break;
                                }
                            }
                        }
                    }

                    if (!plantedPreferredSeeds) {
                        // Look for any seed to plant.
                        for (int i = 0; i < simpleInventory.size(); ++i) {
                            ItemStack itemStack = simpleInventory.getStack(i);
                            if (!itemStack.isEmpty()) {
                                if (plantSeed(itemStack, i, serverWorld, villagerEntity)) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (block instanceof CropBlock && !((CropBlock) block).isMature(blockState)) {
                    this.targetPositions.remove(currentTarget);
                    this.currentTarget = this.chooseRandomTarget(serverWorld);
                    if (this.currentTarget != null) {
                        this.nextResponseTime = l + 20L;
                        villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET,
                                new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, 1));
                        villagerEntity.getBrain()
                                .remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
                    }
                }
            }

            this.ticksRan++;
        }
    }

    private boolean plantSeed(ItemStack itemStack, int stackIndex, ServerWorld serverWorld,
                              VillagerEntity villagerEntity) {
        BlockState blockState2;
        BlockPos currentTarget = this.currentTarget;
        if (itemStack.isOf(Items.WHEAT_SEEDS)) {
            blockState2 = Blocks.WHEAT.getDefaultState();
        } else if (itemStack.isOf(Items.POTATO)) {
            blockState2 = Blocks.POTATOES.getDefaultState();
        } else if (itemStack.isOf(Items.CARROT)) {
            blockState2 = Blocks.CARROTS.getDefaultState();
        } else if (itemStack.isOf(Items.BEETROOT_SEEDS)) {
            blockState2 = Blocks.BEETROOTS.getDefaultState();
        } else {
            return false;
        }

        serverWorld.setBlockState(currentTarget, blockState2);
        serverWorld.emitGameEvent(GameEvent.BLOCK_PLACE, currentTarget,
                GameEvent.Emitter.of(villagerEntity, blockState2));

        serverWorld.playSound(null, currentTarget.getX(), currentTarget.getY(), currentTarget.getZ(),
                SoundEvents.ITEM_CROP_PLANT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        itemStack.decrement(1);
        if (itemStack.isEmpty()) {
            villagerEntity.getInventory().setStack(stackIndex, ItemStack.EMPTY);
        }
        return true;
    }

    protected boolean replaceIsSuitableTarget(BlockPos pos, ServerWorld world, VillagerEntity villagerEntity) {
        BlockState blockState = world.getBlockState(pos);
        Block block = blockState.getBlock();
        Block block2 = world.getBlockState(pos.down()).getBlock();
        return ((block instanceof CropBlock && ((CropBlock) block).isMature(blockState)) ||
                (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()));
    }

    @Inject(method = "shouldKeepRunning", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l,
                                            CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.currentTarget != null);
        cir.cancel();
    }
}
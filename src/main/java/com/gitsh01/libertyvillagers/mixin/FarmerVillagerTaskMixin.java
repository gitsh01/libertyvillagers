package com.gitsh01.libertyvillagers.mixin;

import com.gitsh01.libertyvillagers.ReflectionHelper;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.*;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.FarmerVillagerTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FarmerVillagerTask.class)
public abstract class FarmerVillagerTaskMixin {
    private static final int MAX_RUN_TIME = 20 * 60; // One minute.

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

    private boolean isGourd(Block block) {
        return block instanceof PumpkinBlock || isMelon(block);
    }

    private boolean isMelon(Block block) {
        return Registries.BLOCK.getId(block).equals(Registries.BLOCK.getId(Blocks.MELON));
    }

    @Inject(method = "keepRunning", at = @At(value = "HEAD"), cancellable = true)
    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l, CallbackInfo cir) {
        if (!CONFIG.villagersProfessionConfig.preferPlantSameCrop &&
                !CONFIG.villagersProfessionConfig.farmersHarvestPumpkins &&
                !CONFIG.villagersProfessionConfig.farmersHarvestMelons) {
            // Use default logic.
            return;
        }

        if (villagerEntity.getBrain().hasMemoryModule(MemoryModuleType.WALK_TARGET)) {
            // Wait for the villager to reach the walk target.
            cir.cancel();
            return;
        }

        Item preferredSeeds = null;
        BlockPos currentTarget = this.currentTarget;
        // Can't stand directly on top of the gourd bottom, so increase the distance.
        int distance = 2;
        BlockState blockState = serverWorld.getBlockState(currentTarget);
        Block block = blockState.getBlock();
        Block block2 = serverWorld.getBlockState(currentTarget.down()).getBlock();
        if (currentTarget.isWithinDistance(villagerEntity.getPos(), distance)) {
            boolean foundBlockCrop = false;
            if (CONFIG.villagersProfessionConfig.preferPlantSameCrop) {
                if (block instanceof CropBlock && ((CropBlock) block).isMature(blockState)) {
                    foundBlockCrop = true;
                    preferredSeeds = getPreferredSeedsForCropBlock(block);
                }
            }

            if (CONFIG.villagersProfessionConfig.farmersHarvestMelons && isMelon(block)) {
                foundBlockCrop = true;
            }
            if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins && block instanceof PumpkinBlock) {
                foundBlockCrop = true;
            }

            if (foundBlockCrop) {
                serverWorld.breakBlock(currentTarget, true, villagerEntity);
                blockState = serverWorld.getBlockState(currentTarget);
            }

            if (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()) {
                SimpleInventory simpleInventory = villagerEntity.getInventory();

                // If we don't know what to plant on a piece of farmland, look to nearby blocks to see if
                // we should plant the same item.
                if (CONFIG.villagersProfessionConfig.preferPlantSameCrop && preferredSeeds == null) {
                    for (BlockPos blockPos : BlockPos.iterate(currentTarget.add(-4, 0, -4),
                            currentTarget.add(4, 1, 4))) {
                        BlockState possibleCropState = serverWorld.getBlockState(blockPos);
                        Block possibleCrop = possibleCropState.getBlock();
                        if (possibleCrop instanceof CropBlock || isGourd(possibleCrop) || possibleCrop instanceof StemBlock) {
                            preferredSeeds = getPreferredSeedsForCropBlock(possibleCrop);
                            if (preferredSeeds != null) {
                                break;
                            }
                        }
                    }
                }

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

                if (!plantedPreferredSeeds && (!CONFIG.villagersProfessionConfig.preferPlantSameCrop ||
                        preferredSeeds == null)) {
                    // Look for any seed to plant.
                    for (int i = 0; i < simpleInventory.size(); ++i) {
                        ItemStack itemStack = simpleInventory.getStack(i);
                        // Plant pumpkin seeds over pumpkins if possible.
                        if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins && itemStack.isOf(Items.PUMPKIN)) {
                            if (simpleInventory.containsAny(ImmutableSet.of(Items.PUMPKIN_SEEDS))) {
                                continue;
                            }
                        }
                        // Plant melon seeds over melon slices if possible.
                        if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins && itemStack.isOf(Items.MELON_SLICE)) {
                            if (simpleInventory.containsAny(ImmutableSet.of(Items.MELON_SEEDS))) {
                                continue;
                            }
                        }
                        if (!itemStack.isEmpty()) {
                            if (plantSeed(itemStack, i, serverWorld, villagerEntity)) {
                                break;
                            }
                        }
                    }
                }
            }

            this.targetPositions.remove(currentTarget);
            this.currentTarget = null;
        }

        if (this.currentTarget == null) {
            this.currentTarget = this.chooseRandomTarget(serverWorld);
        }

        if (this.currentTarget != null) {
            this.nextResponseTime = l + 20L;
            int completionRange = 0;
            if (isGourd(serverWorld.getBlockState(this.currentTarget).getBlock())) {
                completionRange = 2;
            }
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(new BlockPosLookTarget(this.currentTarget), 0.5F, completionRange));
            villagerEntity.getBrain()
                    .remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(this.currentTarget));
        }

        this.ticksRan++;
        cir.cancel();
    }

    private Item getPreferredSeedsForCropBlock(Block block) {
        if (CONFIG.villagersProfessionConfig.farmersHarvestMelons && block instanceof AttachedStemBlock) {
            RegistryKey<Block> blockKey = (RegistryKey<Block>)ReflectionHelper.getPrivateField(block, "gourdBlock");

            if (blockKey.equals(BlockKeys.MELON)) {
                return Items.MELON_SEEDS;
            } else {
                return Items.PUMPKIN_SEEDS;
            }
        }

        if (block instanceof BeetrootsBlock) {
            return Items.BEETROOT_SEEDS;
        } else if (block instanceof PotatoesBlock) {
            return Items.POTATO;
        } else if (block instanceof CarrotsBlock) {
            return Items.CARROT;
        } else if (CONFIG.villagersProfessionConfig.farmersHarvestMelons && isMelon(block)) {
            return Items.MELON_SEEDS;
        } else if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins && block instanceof PumpkinBlock) {
            return Items.PUMPKIN_SEEDS;
        } else if (block instanceof CropBlock) {
            return Items.WHEAT_SEEDS;
        }
        return null;
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
        } else if (itemStack.isOf(Items.MELON_SEEDS) || itemStack.isOf(Items.MELON_SLICE)) {
            // Melon slices convert 1:1 to melon seeds.
            blockState2 = Blocks.MELON_STEM.getDefaultState();
        } else if (itemStack.isOf(Items.PUMPKIN_SEEDS) || itemStack.isOf(Items.PUMPKIN)) {
            if (itemStack.isOf(Items.PUMPKIN)) {
                // Give the remaining seeds to the farmer, the fourth is used for planting.
                ItemStack pumpkinSeedItemStack = new ItemStack(Items.PUMPKIN_SEEDS, 3);
                villagerEntity.getInventory().addStack(pumpkinSeedItemStack);
            }
            blockState2 = Blocks.PUMPKIN_STEM.getDefaultState();
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
        if (CONFIG.villagersProfessionConfig.farmersHarvestMelons && isMelon(block)) {
            return true;
        }
        if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins && block instanceof PumpkinBlock) {
            return true;
        }
        return ((block instanceof CropBlock && ((CropBlock) block).isMature(blockState)) ||
                (blockState.isAir() && block2 instanceof FarmlandBlock && villagerEntity.hasSeedToPlant()));
    }

    @Inject(method = "shouldKeepRunning", at = @At(value = "HEAD"), cancellable = true)
    protected void replaceShouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l,
                                            CallbackInfoReturnable<Boolean> cir) {
        if (this.currentTarget == null) {
            this.currentTarget = this.chooseRandomTarget(serverWorld);
        }
        cir.setReturnValue(this.currentTarget != null && this.ticksRan < MAX_RUN_TIME);
        cir.cancel();
    }
}
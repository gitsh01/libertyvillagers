package com.gitsh01.libertyvillagers.tasks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.VillagerWorkTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;

import java.util.Optional;

public class FisherWorkTask extends VillagerWorkTask {

    @Override
    protected void performAdditionalWork(ServerWorld world, VillagerEntity entity) {
        Optional<GlobalPos> optional = entity.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        if (optional.isEmpty()) {
            return;
        }
        GlobalPos globalPos = optional.get();
        BlockState blockState = world.getBlockState(globalPos.pos());
        if (blockState.isOf(Blocks.BARREL)) {
            this.cookAndDropFish(entity);
        }
    }

    private void cookAndDropFish(VillagerEntity entity) {
        SimpleInventory simpleInventory = entity.getInventory();
        int cod = simpleInventory.count(Items.COD);
        int salmon = simpleInventory.count(Items.SALMON);
        ServerWorld serverWorld = (ServerWorld)entity.getWorld();
        simpleInventory.removeItem(Items.COD, cod);
        simpleInventory.removeItem(Items.SALMON, salmon);
        ItemStack cookedSalmon = simpleInventory.addStack(new ItemStack(Items.COOKED_SALMON, salmon));
        if (!cookedSalmon.isEmpty()) {
            entity.dropStack(serverWorld, cookedSalmon, 0.5f);
        }
        ItemStack cookedCod = simpleInventory.addStack(new ItemStack(Items.COOKED_COD, cod));
        if (!cookedCod.isEmpty()) {
            entity.dropStack(serverWorld, cookedCod, 0.5f);
        }
    }
}


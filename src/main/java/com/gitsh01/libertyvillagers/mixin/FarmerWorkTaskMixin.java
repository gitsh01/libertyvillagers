package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.ai.brain.task.FarmerWorkTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FarmerWorkTask.class)
public class FarmerWorkTaskMixin {

    @Shadow
    private static List<Item> COMPOSTABLES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    static private void modifyStaticBlock(CallbackInfo ci) {
        if (CONFIG.villagersProfessionConfig.farmersHarvestMelons) {
            COMPOSTABLES = new ArrayList<>(COMPOSTABLES);
            COMPOSTABLES.add(Items.MELON_SEEDS);
        }
        if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins) {
            COMPOSTABLES = new ArrayList<>(COMPOSTABLES);
            COMPOSTABLES.add(Items.PUMPKIN_SEEDS);
        }
    }

    @Inject(method = "craftAndDropBread(Lnet/minecraft/entity/passive/VillagerEntity;)V",
            at = @At("HEAD"))
    private void craftAndDropPumpkinPie(VillagerEntity entity, CallbackInfo ci) {
        SimpleInventory simpleInventory = entity.getInventory();
        if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins) {
            if (simpleInventory.count(Items.PUMPKIN_PIE) > 36) {
                return;
            }
            int i = simpleInventory.count(Items.PUMPKIN);
            if (i == 0) {
                return;
            }

            // We are not going to ask where the eggs and the sugar come from.
            simpleInventory.removeItem(Items.PUMPKIN, i);
            ItemStack itemStack = simpleInventory.addStack(new ItemStack(Items.PUMPKIN_PIE, i));
            if (!itemStack.isEmpty()) {
                entity.dropStack(itemStack, 0.5f);
            }
        }
    }
}

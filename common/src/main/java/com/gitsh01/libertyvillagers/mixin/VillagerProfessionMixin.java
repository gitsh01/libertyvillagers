package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerProfession.class)
public abstract class VillagerProfessionMixin {

    @Shadow
    private String id;

    @Inject(method = "secondaryJobSites",
            at = @At("HEAD"),
            cancellable = true)
    void replaceSecondaryJobSites(CallbackInfoReturnable<ImmutableSet<Block>> cir) {
        switch (id) {
            case "librarian" -> {
                if (CONFIG.villagersProfessionConfig.librariansLookAtBooks) {
                    cir.setReturnValue(ImmutableSet.of(Blocks.BOOKSHELF));
                    cir.cancel();
                }
            }
            case "fisherman" -> {
                if (CONFIG.villagersProfessionConfig.fishermanFish) {
                    cir.setReturnValue(ImmutableSet.of(Blocks.WATER));
                    cir.cancel();
                }
            }
        }
    }

    @Inject(method = "gatherableItems",
            at = @At("RETURN"),
            cancellable = true)
    void replaceGatherableItems(CallbackInfoReturnable<ImmutableSet<Item>> cir) {
        ImmutableSet<Item> originalSet = cir.getReturnValue();
        ImmutableSet.Builder<Item> setBuilder = ImmutableSet.<Item>builder().addAll(originalSet);
        switch (id) {
            case "butcher" -> {
                if (CONFIG.villagersProfessionConfig.butchersFeedChickens) {
                    setBuilder.addAll(ImmutableSet.of(Items.PUMPKIN_SEEDS, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS));
                }
                if (CONFIG.villagersProfessionConfig.butchersFeedCows || CONFIG.villagersProfessionConfig.butchersFeedSheep) {
                    setBuilder.addAll(ImmutableSet.of(Items.WHEAT));
                }
            }
            case "farmer" -> {
                if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins) {
                    setBuilder.addAll(ImmutableSet.of(Items.PUMPKIN_SEEDS, Items.PUMPKIN));
                }
            }
            case "fisherman" -> {
                if (CONFIG.villagersProfessionConfig.fishermanFish) {
                    setBuilder.addAll(ImmutableSet.of(Items.COD, Items.SALMON));
                }
            }
            case "fletcher" -> {
                if (CONFIG.villagersProfessionConfig.fletchersFeedChickens) {
                    setBuilder.addAll(ImmutableSet.of(Items.PUMPKIN_SEEDS, Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS));
                }
            }
            case "leatherworker" -> {
                if (CONFIG.villagersProfessionConfig.leatherworkersFeedCows) {
                    setBuilder.addAll(ImmutableSet.of(Items.WHEAT));
                }
            }
            case "shepherd" -> {
                if (CONFIG.villagersProfessionConfig.shepherdsFeedSheep) {
                    setBuilder.addAll(ImmutableSet.of(Items.WHEAT));
                }
            }
        }
        cir.setReturnValue(setBuilder.build());
        cir.cancel();
    }
}

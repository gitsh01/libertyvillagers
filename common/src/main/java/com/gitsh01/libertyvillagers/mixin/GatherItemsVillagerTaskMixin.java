package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.GatherItemsVillagerTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(GatherItemsVillagerTask.class)
public abstract class GatherItemsVillagerTaskMixin {

    @Invoker("giveHalfOfStack")
    static void giveHalfOfStack(VillagerEntity villager, Set<Item> validItems, LivingEntity target) {
        throw new AssertionError();
    }

    @Inject(method = "keepRunning(Lnet/minecraft/server/world/ServerWorld;" +
            "Lnet/minecraft/entity/passive/VillagerEntity;J)V",
            at = @At("HEAD"))
    private void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l, CallbackInfo ci) {
        if (villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).isEmpty()) {
            return;
        }
        VillagerEntity villagerEntity2 = (VillagerEntity)villagerEntity.getBrain().getOptionalMemory(MemoryModuleType.INTERACTION_TARGET).get();
        if (villagerEntity.squaredDistanceTo(villagerEntity2) > 5.0) {
            return;
        }
        if (villagerEntity2.getVillagerData().getProfession() == VillagerProfession.FARMER) {
            GatherItemsVillagerTaskMixin.giveHalfOfStack(villagerEntity, ImmutableSet.of(Items.PUMPKIN),
                    villagerEntity2);
        }
        if ((CONFIG.villagersProfessionConfig.leatherworkersFeedCows &&
                villagerEntity2.getVillagerData().getProfession() == VillagerProfession.LEATHERWORKER) ||
                (CONFIG.villagersProfessionConfig.butchersFeedCows &&
                        villagerEntity2.getVillagerData().getProfession() == VillagerProfession.BUTCHER) ||
                (CONFIG.villagersProfessionConfig.butchersFeedSheep &&
                        villagerEntity2.getVillagerData().getProfession() == VillagerProfession.BUTCHER) ||
                (CONFIG.villagersProfessionConfig.shepherdsFeedSheep &&
                        villagerEntity2.getVillagerData().getProfession() == VillagerProfession.SHEPHERD)) {
            GatherItemsVillagerTaskMixin.giveHalfOfStack(villagerEntity, ImmutableSet.of(Items.WHEAT), villagerEntity2);
        }
        if (CONFIG.villagersProfessionConfig.butchersFeedPigs &&
                villagerEntity2.getVillagerData().getProfession() == VillagerProfession.BUTCHER) {
            GatherItemsVillagerTaskMixin.giveHalfOfStack(villagerEntity, ImmutableSet.of(Items.CARROT, Items.POTATO),
                    villagerEntity2);
        }
        if ((CONFIG.villagersProfessionConfig.butchersFeedChickens &&
                villagerEntity2.getVillagerData().getProfession() == VillagerProfession.BUTCHER) ||
                (CONFIG.villagersProfessionConfig.fletchersFeedChickens &&
                        villagerEntity2.getVillagerData().getProfession() == VillagerProfession.FLETCHER)) {
            GatherItemsVillagerTaskMixin.giveHalfOfStack(villagerEntity,
                    ImmutableSet.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS),
                    villagerEntity2);
        }
        if (CONFIG.villagersProfessionConfig.butchersFeedRabbits &&
                villagerEntity2.getVillagerData().getProfession() == VillagerProfession.BUTCHER) {
            GatherItemsVillagerTaskMixin.giveHalfOfStack(villagerEntity, ImmutableSet.of(Items.CARROT),
                    villagerEntity2);
        }
    }
}

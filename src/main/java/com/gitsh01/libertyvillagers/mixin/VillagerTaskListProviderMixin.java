package com.gitsh01.libertyvillagers.mixin;

import com.gitsh01.libertyvillagers.tasks.HealGolemTask;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.*;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerTaskListProvider.class)
public abstract class VillagerTaskListProviderMixin {

    private static final int SECONDARY_WORK_TASK_PRIORITY = 9; // Mojang default: 5, increasing priority for more work.
    private static final int THIRD_WORK_TASK_PRIORITY = 7;

    public VillagerTaskListProviderMixin() {
    }

    @Invoker("createBusyFollowTask")
    public static Pair<Integer, Task<LivingEntity>> invokeCreateBusyFollowTask() {
        throw new AssertionError();
    }

    @Inject(method = "createWorkTasks", at = @At("Head"), cancellable = true)
    private static void replaceCreateWorkTasks(VillagerProfession profession, float speed, CallbackInfoReturnable cir) {
        VillagerWorkTask villagerWorkTask = new VillagerWorkTask(); // Plays working sounds at the job site.
        Task secondaryWorkTask = null;
        Task thirdWorkTask = null;
        switch (profession.toString()) {
            case "armorer":
                if (CONFIG.armorerHealsGolems) {
                    secondaryWorkTask = new HealGolemTask();
                }
                break;
            case "farmer":
                villagerWorkTask = new FarmerWorkTask(); // Compost.
                secondaryWorkTask = new FarmerVillagerTask(); // Harvest / plant seeds.
                thirdWorkTask = new BoneMealTask(); // Apply bonemeal to crops.
                break;
        }

        ArrayList<Pair<Task<? super VillagerEntity>, Integer>> randomTasks = new ArrayList(
                ImmutableList.of(Pair.of(villagerWorkTask, 7),
                        Pair.of(new GoToIfNearbyTask(MemoryModuleType.JOB_SITE, 0.4f, 4), 2),
                        Pair.of(new GoToNearbyPositionTask(MemoryModuleType.JOB_SITE, 0.4f, 1, 10), 5),
                        Pair.of(new GoToSecondaryPositionTask(MemoryModuleType.SECONDARY_JOB_SITE, speed, 1, 6,
                                MemoryModuleType.JOB_SITE), 5)));

        if (secondaryWorkTask != null) {
            randomTasks.add(Pair.of(secondaryWorkTask, SECONDARY_WORK_TASK_PRIORITY));
        }

        if (thirdWorkTask != null) {
            randomTasks.add(Pair.of(thirdWorkTask, THIRD_WORK_TASK_PRIORITY));
        }

        RandomTask randomTask = new RandomTask(ImmutableList.copyOf(randomTasks));
        List<Pair<Integer, ? extends Task<? super VillagerEntity>>> tasks =
                List.of(VillagerTaskListProviderMixin.invokeCreateBusyFollowTask(), Pair.of(7, randomTask),
                        Pair.of(10, new HoldTradeOffersTask(400, 1600)),
                        Pair.of(10, new FindInteractionTargetTask(EntityType.PLAYER, 4)), Pair.of(2,
                                new VillagerWalkTowardsTask(MemoryModuleType.JOB_SITE, speed, 9,
                                        CONFIG.pathfindingMaxRange, 1200)), Pair.of(3, new GiveGiftsToHeroTask(100)),
                        Pair.of(99, new ScheduleActivityTask()));
        cir.setReturnValue(ImmutableList.copyOf(tasks));
        cir.cancel();
    }
}

package com.gitsh01.libertyvillagers.tasks;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public class FeedTargetTask extends HealTargetTask {
    private static final int COMPLETION_RANGE = 3;

    private final Class<? extends LivingEntity> entityClass;
    private final ImmutableSet<Item> foodTypes;

    private final double range;

    private final int maxEntities;

    public FeedTargetTask(Class<? extends LivingEntity> entityClass, ImmutableSet<Item> foodTypes, double range,
                          int maxEntities) {
        super(COMPLETION_RANGE);
        this.entityClass = entityClass;
        this.foodTypes = foodTypes;
        this.range = range;
        this.maxEntities = maxEntities;
    }

    @SuppressWarnings("unchecked")
    protected List<LivingEntity> getPossiblePatients(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        if (!villagerEntity.getInventory().containsAny(foodTypes)) {
            return Lists.newArrayList();
        }

        List<? extends LivingEntity> possiblePatients =
                villagerEntity.getWorld().getNonSpectatingEntities(entityClass,
                villagerEntity.getBoundingBox().expand(range));

        if (possiblePatients.size() >= maxEntities) {
            return Lists.newArrayList();
        }

        return (List<LivingEntity>) possiblePatients;
    }

    protected void healTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, LivingEntity currentPatient) {
        if (!(currentPatient instanceof AnimalEntity)) {
            return;
        }
        AnimalEntity animal = (AnimalEntity)currentPatient;
        if (!animal.canEat()) {
            return;
        }
        SimpleInventory simpleInventory = villagerEntity.getInventory();
        if (!simpleInventory.containsAny(foodTypes)) {
            return;
        }

        for (int i = 0; i < simpleInventory.size(); ++i) {
            ItemStack itemStack = simpleInventory.getStack(i);
            if (itemStack.isEmpty() || !foodTypes.contains(itemStack.getItem())) continue;
            animal.lovePlayer(null);
            itemStack.decrement(1);
            break;
        }
    }

    protected boolean isValidPatient(LivingEntity entity) {
        if (!(entity instanceof AnimalEntity)) {
            return false;
        }
        AnimalEntity animal = (AnimalEntity)entity;
        return entity != null && entity.isAlive() &&
                !entity.isInvisible() && !entity.isInvulnerable() && animal.canEat() && !animal.isBaby();
    }

}

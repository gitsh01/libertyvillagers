package com.gitsh01.libertyvillagers.tasks;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

public class HealGolemTask extends HealTargetTask {
    private static final int COMPLETION_RANGE = 3;

    public HealGolemTask() {
        super(COMPLETION_RANGE);
    }

    protected List<LivingEntity> getPossiblePatients(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        List<LivingEntity> possiblePatients = Lists.newArrayList();
        if (!CONFIG.villagersProfessionConfig.armorerHealsGolems) {
            return possiblePatients;
        }

        List<IronGolemEntity> golems = villagerEntity.getWorld().getNonSpectatingEntities(IronGolemEntity.class,
                villagerEntity.getBoundingBox().expand(CONFIG.villagersProfessionConfig.armorerHealsGolemsRange));
        possiblePatients.addAll(golems);
        return possiblePatients;
    }

    protected void healTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, LivingEntity currentPatient) {
        float g = 1.0f + (currentPatient.getRandom().nextFloat() - currentPatient.getRandom().nextFloat()) * 0.2f;
        currentPatient.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.F, g);
        currentPatient.heal(currentPatient.getMaxHealth());
    }
}

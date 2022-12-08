package com.gitsh01.libertyvillagers.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class HealTargetTask extends MultiTickTask<VillagerEntity> {
    public static final float WALK_SPEED = 0.7F;
    private static final int MAX_RUN_TIME = 1000;

    @Nullable
    private LivingEntity currentPatient;
    private long nextResponseTime;
    private int ticksRan;
    private int completionRange;

    public HealTargetTask(int completionRange) {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.JOB_SITE,
                MemoryModuleState.VALUE_PRESENT), MAX_RUN_TIME);
        this.completionRange = completionRange;
    }

    protected abstract List<LivingEntity> getPossiblePatients(ServerWorld serverWorld, VillagerEntity villagerEntity);

    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        List<LivingEntity> possiblePatients = getPossiblePatients(serverWorld, villagerEntity);
        List<LivingEntity> patients = Lists.newArrayList();
        for (LivingEntity possiblePatient : possiblePatients) {
            if (isValidPatient(possiblePatient)) {
                patients.add(possiblePatient);
            }
        }

        if (patients.size() == 0) {
            return false;
        }

        this.currentPatient = patients.get(serverWorld.getRandom().nextInt(patients.size()));
        return this.currentPatient != null;
    }

    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (l > this.nextResponseTime && this.currentPatient != null) {
            villagerEntity.getBrain()
                    .remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.currentPatient, true));
            villagerEntity.getBrain().remember(MemoryModuleType.WALK_TARGET,
                    new WalkTarget(this.currentPatient, WALK_SPEED, completionRange));
        }
    }

    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        // Check to see if the patient was healed or died before the villager reached it.
        if (!isValidPatient(currentPatient)) {
            return;
        }

        if (this.currentPatient.distanceTo(villagerEntity) <= completionRange) {
            healTarget(serverWorld, villagerEntity, currentPatient);
            currentPatient = null;
            ++this.ticksRan;
        }
    }

    protected abstract void healTarget(ServerWorld serverWorld, VillagerEntity villagerEntity,
                                       LivingEntity currentPatient);

    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        // Check to see if the patient was healed or died before the villager reached it.
        if (!isValidPatient(currentPatient)) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }

    protected boolean isValidPatient(LivingEntity entity) {
        return entity != null && !(entity.getHealth() >= entity.getMaxHealth()) && entity.isAlive() &&
                !entity.isInvisible() && !entity.isInvulnerable() &&
                !entity.hasStatusEffect(StatusEffects.REGENERATION);
    }
}

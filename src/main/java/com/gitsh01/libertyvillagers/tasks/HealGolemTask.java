package com.gitsh01.libertyvillagers.tasks;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

/**
 * Modified from FarmerVillagerTask.
 */
public class HealGolemTask extends Task<VillagerEntity> {
    public static final float WALK_SPEED = 0.7F;
    private static final int MAX_RUN_TIME = 1000;
    private static final int COMPLETION_RANGE = 3;

    @Nullable
    private IronGolemEntity currentPatient;
    private long nextResponseTime;
    private int ticksRan;

    public HealGolemTask() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.JOB_SITE,
                MemoryModuleState.VALUE_PRESENT), MAX_RUN_TIME);
    }

    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        List<IronGolemEntity> golems = villagerEntity.world.getNonSpectatingEntities(IronGolemEntity.class,
                villagerEntity.getBoundingBox().expand(CONFIG.armorerHealsGolemsRange));

        // Use a list of golem patients rather than seek out the worst, in case there are
        // multiple armorers in the village.
        List<IronGolemEntity> patients = Lists.newArrayList();

        for (IronGolemEntity golem : golems) {
            if (!golem.isAlive() || golem.getHealth() == golem.getMaxHealth() || golem.isInvisible() ||
                    golem.isInvulnerable()) continue;
            patients.add(golem);
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
                    new WalkTarget(this.currentPatient, WALK_SPEED, COMPLETION_RANGE));
        }
    }

    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        this.ticksRan = 0;
        this.nextResponseTime = l + 40L;
    }

    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        if (this.currentPatient == null || this.currentPatient.getHealth() >= this.currentPatient.getMaxHealth() ||
                !this.currentPatient.isAlive() || this.currentPatient.isInvisible() ||
                this.currentPatient.isInvulnerable()) {
            return;
        }

        if (this.currentPatient.distanceTo(villagerEntity) <= COMPLETION_RANGE) {
            float g = 1.0f + (currentPatient.getRandom().nextFloat() - currentPatient.getRandom().nextFloat()) * 0.2f;
            this.currentPatient.playSound(SoundEvents.ENTITY_IRON_GOLEM_REPAIR, 1.F, g);
            this.currentPatient.heal(this.currentPatient.getMaxHealth());
            ++this.ticksRan;
        }
    }

    protected boolean shouldKeepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        // Check to see if the patient was healed or died before the villager reached it.
        if (this.currentPatient == null || this.currentPatient.getHealth() >= this.currentPatient.getMaxHealth() ||
                !this.currentPatient.isAlive() || this.currentPatient.isInvisible() ||
                this.currentPatient.isInvulnerable()) {
            return false;
        }
        return this.ticksRan < MAX_RUN_TIME;
    }
}

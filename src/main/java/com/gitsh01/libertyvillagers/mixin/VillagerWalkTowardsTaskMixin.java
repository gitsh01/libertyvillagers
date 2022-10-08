package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.ai.brain.task.VillagerWalkTowardsTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.math.GlobalPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerWalkTowardsTask.class)
public abstract class VillagerWalkTowardsTaskMixin extends Task<VillagerEntity> {
    public VillagerWalkTowardsTaskMixin() {
        super(ImmutableMap.of());
    }

    @Accessor("maxRange")
    public abstract void setMaxRange(int maxRange);

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/ai/brain/MemoryModuleType;FIII)V")
    public void overrideMaxRange(MemoryModuleType<GlobalPos> destination, float speed, int completionRange,
                                 int maxRange, int maxRunTime, CallbackInfo ci) {
        this.setMaxRange(CONFIG.pathfindingMaxRange);
    }
}
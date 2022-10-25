package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.Memory;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(Brain.class)
public abstract class BrainMixin<E extends LivingEntity> {

    BrainMixin() {
    }

    @Shadow
    public abstract boolean hasMemoryModule(MemoryModuleType<?> type);

    @Inject(method = "setMemory(Lnet/minecraft/entity/ai/brain/MemoryModuleType;Ljava/util/Optional;)V",
            at = @At(value = "Head"))
    <U> void setMemory(MemoryModuleType<U> type, Optional<? extends Memory<?>> memory, CallbackInfo ci) {
        if (!CONFIG.debugConfig.enableVillagerBrainDebug) {
            return;
        }
        // Only look for villagers.
        if (!hasMemoryModule(MemoryModuleType.MEETING_POINT)) {
            return;
        }
        // Only look for certian memories.
        if (type != MemoryModuleType.WALK_TARGET && type != MemoryModuleType.HOME &&
                type != MemoryModuleType.POTENTIAL_JOB_SITE && type != MemoryModuleType.JOB_SITE &&
                type != MemoryModuleType.PATH) {
            return;
        }
        System.out.printf("===== MemoryType %s\n", type.toString());
        // Print out stack trace so we can see which task is controlling the villager.
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        for (int i = 1; i < elements.length; i++) {
            StackTraceElement s = elements[i];
            if (s.getClassName().contains("ai.brain")) {
                System.out.println("\tat " + s.getClassName() + "." + s.getMethodName() + "(" + s.getFileName() + ":" +
                        s.getLineNumber() + ")");
            }
        }
    }
}

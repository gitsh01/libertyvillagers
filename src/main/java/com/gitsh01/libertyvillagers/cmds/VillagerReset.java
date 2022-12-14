package com.gitsh01.libertyvillagers.cmds;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;
import static net.minecraft.server.command.CommandManager.literal;

public class VillagerReset {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("villagerreset").executes(context -> {
                    processVillagerReset(context);
                    return 1;
                })));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> dispatcher.register(literal("vr").executes(context -> {
                    processVillagerReset(context);
                    return 1;
                })));
    }

    public static void processVillagerReset(CommandContext<ServerCommandSource> command) {
        ServerCommandSource source = command.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld serverWorld = source.getWorld();

        List<VillagerEntity> villagers = serverWorld.getNonSpectatingEntities(VillagerEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        for (VillagerEntity villager : villagers) {
            villager.releaseTicketFor(MemoryModuleType.JOB_SITE);
            villager.getBrain().forget(MemoryModuleType.JOB_SITE);
            villager.releaseTicketFor(MemoryModuleType.MEETING_POINT);
            villager.getBrain().forget(MemoryModuleType.MEETING_POINT);
            villager.releaseTicketFor(MemoryModuleType.POTENTIAL_JOB_SITE);
            villager.getBrain().forget(MemoryModuleType.POTENTIAL_JOB_SITE);
        }

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerreset"));
    }
}

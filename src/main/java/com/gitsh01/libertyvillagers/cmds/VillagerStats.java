package com.gitsh01.libertyvillagers.cmds;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.village.VillagerProfession;

import java.util.HashMap;
import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;
import static net.minecraft.server.command.CommandManager.literal;

public class VillagerStats {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("villagerstats").executes(context -> {
                    processVillagerStats(context);
                    return 1;
                })));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> dispatcher.register(literal("vs").executes(context -> {
                    processVillagerStats(context);
                    return 1;
                })));
    }

    public static void processVillagerStats(CommandContext<ServerCommandSource> command) throws CommandSyntaxException {
        ServerCommandSource source = command.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld serverWorld = source.getWorld();

        List<VillagerEntity> villagers = serverWorld.getNonSpectatingEntities(VillagerEntity.class,
                player.getBoundingBox().expand(CONFIG.villagerStatRange));

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfVillagers").getString(),
                villagers.size()));

        HashMap<VillagerProfession, Integer> villagerProfessionintegerMap = new HashMap<VillagerProfession, Integer>();
        int numHomeless = 0;
        for (VillagerEntity villager : villagers) {
            villagerProfessionintegerMap.merge(villager.getVillagerData().getProfession(), 1, Integer::sum);
            if (!villager.getBrain().hasMemoryModule(MemoryModuleType.HOME)) {
                numHomeless++;
            }
        }

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfHomeless").getString(), numHomeless));

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.professions"));
        villagerProfessionintegerMap.forEach((villagerProfession, count) -> {
            player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.professionsFormat",
                    villagerProfession.toString(), count));
        });
    }
}

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
import org.apache.commons.lang3.tuple.MutablePair;

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

    public static MutablePair<Integer, Integer> sumPair(MutablePair<Integer, Integer> oldVal,
                                                        MutablePair<Integer, Integer> newVal) {
        oldVal.setLeft(oldVal.getLeft() + newVal.getLeft());
        oldVal.setRight(oldVal.getRight() + newVal.getRight());
        return oldVal;
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

        // MutablePair is countOfVillagersWithProfession, countOfVillagersWithProfessionWhoHaveWorkstations.
        HashMap<String, MutablePair<Integer, Integer>> villagerProfessionMap = new HashMap<>();
        int numHomeless = 0;
        for (VillagerEntity villager : villagers) {
            if (villager.isBaby()) {
                // TODO: getProfession() is not localized currently. When getProfession is localized, also localize
                // "baby".
                villagerProfessionMap.merge("baby", new MutablePair<>(1, 0), VillagerStats::sumPair);
            } else {

                villagerProfessionMap.merge(villager.getVillagerData().getProfession().toString(), new MutablePair<>(1,
                                villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE).isPresent() ? 1 : 0),
                        VillagerStats::sumPair);
            }
            if (!villager.getBrain().hasMemoryModule(MemoryModuleType.HOME)) {
                numHomeless++;
            }
        }

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfHomeless").getString(), numHomeless));

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.professions"));
        villagerProfessionMap.forEach((villagerProfession, pair) -> {
            player.sendMessage(
                    Text.translatable("text.LibertyVillagers.villagerStats.professionsFormat", villagerProfession,
                            pair.getLeft(), pair.getRight()));
        });
    }
}

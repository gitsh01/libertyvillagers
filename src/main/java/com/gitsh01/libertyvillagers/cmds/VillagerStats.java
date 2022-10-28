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
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import org.apache.commons.lang3.tuple.MutablePair;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;
import static net.minecraft.server.command.CommandManager.literal;

class ProfessionInfo {
    public VillagerProfession profession;
    public int countVillagersWithProfession;

    public ProfessionInfo(VillagerProfession profession, int countVillagersWithProfession) {
        this.profession = profession;
        this.countVillagersWithProfession = countVillagersWithProfession;
    }

    public static ProfessionInfo mergeProfessionInfo(ProfessionInfo oldVal, ProfessionInfo newVal) {
        return new ProfessionInfo(oldVal.profession,
                oldVal.countVillagersWithProfession + newVal.countVillagersWithProfession);
    }
}

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

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.title"));

        List<VillagerEntity> villagers = serverWorld.getNonSpectatingEntities(VillagerEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfVillagers").getString(),
                villagers.size()));

        TreeMap<String, ProfessionInfo> villagerProfessionMap = new TreeMap<>();

        for (Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> professionEntry : Registry.VILLAGER_PROFESSION.getEntrySet()) {
            VillagerProfession profession = professionEntry.getValue();
            villagerProfessionMap.put(profession.toString(), new ProfessionInfo(profession, 0));
        }

        int numHomeless = 0;
        for (VillagerEntity villager : villagers) {
            if (villager.isBaby()) {
                // TODO: getProfession() is not localized currently. When getProfession is localized, also localize
                // "baby".
                villagerProfessionMap.merge("baby", new ProfessionInfo(villager.getVillagerData().getProfession(), 1),
                        ProfessionInfo::mergeProfessionInfo);
            } else {
                villagerProfessionMap.merge(villager.getVillagerData().getProfession().toString(),
                        new ProfessionInfo(villager.getVillagerData().getProfession(), 1),
                        ProfessionInfo::mergeProfessionInfo);
            }
            if (!villager.getBrain().hasMemoryModule(MemoryModuleType.HOME)) {
                numHomeless++;
            }
        }

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfHomeless").getString(), numHomeless));

        List<PointOfInterest> availableBeds = serverWorld.getPointOfInterestStorage()
                .getInCircle(registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.HOME), player.getBlockPos(),
                        CONFIG.debugConfig.villagerStatRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE)
                .collect(Collectors.toList());

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfAvailableBeds").getString(),
                availableBeds.size()));

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.bedsAt"));
        for (PointOfInterest bed : availableBeds) {
            if (bed != null && bed.getPos() != null) {
                player.sendMessage(Text.of(bed.getPos().toShortString()));
            }
        }

        player.sendMessage(Text.translatable("text.LibertyVillagers.villagerStats.professions"));
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> {
            long numAvailableWorkstations = 0;
            long numOccupiedWorkstations = 0;
            if (villagerProfession != "baby") {
                numAvailableWorkstations = serverWorld.getPointOfInterestStorage()
                        .count(professionInfo.profession.acquirableWorkstation(), player.getBlockPos(),
                                CONFIG.debugConfig.villagerStatRange,
                                PointOfInterestStorage.OccupationStatus.HAS_SPACE);
                numOccupiedWorkstations = serverWorld.getPointOfInterestStorage()
                        .count(professionInfo.profession.heldWorkstation(), player.getBlockPos(),
                                CONFIG.debugConfig.villagerStatRange,
                                PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
            }

            player.sendMessage(
                    Text.translatable("text.LibertyVillagers.villagerStats.professionsFormat", villagerProfession,
                            professionInfo.countVillagersWithProfession, numOccupiedWorkstations,
                            numAvailableWorkstations));
        });
    }
}

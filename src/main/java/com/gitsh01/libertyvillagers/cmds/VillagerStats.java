package com.gitsh01.libertyvillagers.cmds;

import com.gitsh01.libertyvillagers.LibertyVillagersClientInitializer;
import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import com.gitsh01.libertyvillagers.LibertyVillagersServerInitializer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
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
import org.apache.logging.log4j.core.jmx.Server;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
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
    private static final int LINES_PER_PAGE = 14;

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

        ItemStack bookStack = new ItemStack(Items.WRITTEN_BOOK);
        bookStack.setSubNbt("title",
                NbtString.of(Text.translatable("text.LibertyVillagers.villagerStats.title").getString()));
        bookStack.setSubNbt("author", NbtString.of(player.getEntityName()));

        List<VillagerEntity> villagers = serverWorld.getNonSpectatingEntities(VillagerEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        NbtList pages = new NbtList();
        pages.addAll(splitToPageTags(titlePage(villagers, serverWorld)));
        pages.addAll(splitToPageTags(professionPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(heldWorkstationPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(freeWorkstationsPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(homelessPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(availableBedsPage(player, villagers, serverWorld)));
        bookStack.setSubNbt("pages", pages);

        if (LibertyVillagersMod.isClient()) {
            LibertyVillagersClientInitializer.openBookScreen(bookStack);
        } else {
            if (!FabricLoader.getInstance().getModContainer("server_translations_api").isPresent()) {
                player.sendMessage(Text.of("Server_translations_api is missing. VillagerStats does not work " +
                        "server-side without translations."));
                return;
            }
            LibertyVillagersServerInitializer.openBookScreen(bookStack, player);
        }
    }


    private static Collection<NbtString> splitToPageTags(String string) {
        final List<String> lines = LibertyVillagersMod.isClient() ? LibertyVillagersClientInitializer.wrapText(string) :
                LibertyVillagersServerInitializer.wrapText(string);

        List<NbtString> pageTags = new LinkedList<>();

        int linesRemaining = LINES_PER_PAGE;
        StringBuilder curString = new StringBuilder();
        while (!lines.isEmpty()) {
            curString.append(lines.remove(0));
            linesRemaining--;
            if (linesRemaining <= 0) {
                linesRemaining = LINES_PER_PAGE;
                pageTags.add(NbtString.of("\"" + curString + "\""));
                curString = new StringBuilder();
            }
        }

        if (curString.length() > 0) {
            pageTags.add(NbtString.of("\"" + curString + "\""));
        }

        return pageTags;
    }


    protected static String titlePage(List<VillagerEntity> villagers, ServerWorld serverWorld) {
        String pageString = Text.translatable("text.LibertyVillagers.villagerStats.title").getString() + "\n\n";

        pageString += Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfVillagers").getString(),
                villagers.size()).getString();
        return pageString;
    }

    protected static String professionPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                           ServerWorld serverWorld) {
        String pageString = Text.translatable("text.LibertyVillagers.villagerStats.professions").getString() + "\n\n";
        TreeMap<String, ProfessionInfo> villagerProfessionMap = new TreeMap<>();

        for (Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> professionEntry : Registry.VILLAGER_PROFESSION.getEntrySet()) {
            VillagerProfession profession = professionEntry.getValue();
            villagerProfessionMap.put(profession.toString(), new ProfessionInfo(profession, 0));
        }

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
        }

        AtomicReference<String> professions = new AtomicReference<>("");
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> {
            professions.set(professions.get() +
                    Text.translatable("text.LibertyVillagers.villagerStats.professionsCountFormat", villagerProfession,
                            professionInfo.countVillagersWithProfession).getString() + "\n");
        });

        pageString += professions.get() + "\n\n";

        return pageString;
    }


    protected static String heldWorkstationPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                                ServerWorld serverWorld) {
        String pageString =
                Text.translatable("text.LibertyVillagers.villagerStats.professionsHeldJobSites").getString() + "\n\n";
        TreeMap<String, ProfessionInfo> villagerProfessionMap = new TreeMap<>();

        for (Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> professionEntry : Registry.VILLAGER_PROFESSION.getEntrySet()) {
            VillagerProfession profession = professionEntry.getValue();
            villagerProfessionMap.put(profession.toString(), new ProfessionInfo(profession, 0));
        }

        AtomicReference<String> heldWorkstations = new AtomicReference<>("");
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> {
            long numOccupiedWorkstations = 0;
            if (villagerProfession != "baby") {
                numOccupiedWorkstations = serverWorld.getPointOfInterestStorage()
                        .count(professionInfo.profession.heldWorkstation(), player.getBlockPos(),
                                CONFIG.debugConfig.villagerStatRange,
                                PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
            }
            heldWorkstations.set(heldWorkstations.get() +
                    Text.translatable("text.LibertyVillagers.villagerStats.professionsCountFormat", villagerProfession,
                            numOccupiedWorkstations).getString() + "\n");
        });

        pageString += heldWorkstations.get() + "\n\n";

        return pageString;
    }


    protected static String freeWorkstationsPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                                 ServerWorld serverWorld) {
        String pageString =
                Text.translatable("text.LibertyVillagers.villagerStats.professionsAvailableJobSites").getString() +
                        "\n\n";
        TreeMap<String, ProfessionInfo> villagerProfessionMap = new TreeMap<>();

        for (Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> professionEntry : Registry.VILLAGER_PROFESSION.getEntrySet()) {
            VillagerProfession profession = professionEntry.getValue();
            villagerProfessionMap.put(profession.toString(), new ProfessionInfo(profession, 0));
        }

        AtomicReference<String> availableWorkstations = new AtomicReference<>("");
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> {
            long numAvailableWorkstations = 0;
            if (villagerProfession != "baby") {
                numAvailableWorkstations = serverWorld.getPointOfInterestStorage()
                        .count(professionInfo.profession.acquirableWorkstation(), player.getBlockPos(),
                                CONFIG.debugConfig.villagerStatRange,
                                PointOfInterestStorage.OccupationStatus.HAS_SPACE);
            }

            availableWorkstations.set(availableWorkstations.get() +
                    Text.translatable("text.LibertyVillagers.villagerStats.professionsCountFormat", villagerProfession,
                            numAvailableWorkstations).getString() + "\n");
        });

        pageString += availableWorkstations.get() + "\n\n";

        return pageString;
    }

    protected static String homelessPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                         ServerWorld serverWorld) {
        String homelessString = "";
        int numHomeless = 0;
        for (VillagerEntity villager : villagers) {
            if (!villager.getBrain().hasMemoryModule(MemoryModuleType.HOME)) {
                numHomeless++;
                homelessString +=
                        Text.translatable("text.LibertyVillagers.villagerStats.homeless", villager.getDisplayName(),
                                villager.getBlockPos().toShortString()).getString() + "\n";
            }
        }

        String pageString = Text.translatable("text.LibertyVillagers.villagerStats.format",
                        Text.translatable("text.LibertyVillagers.villagerStats.numberOfHomeless").getString(), numHomeless)
                .getString() + "\n\n";
        if (numHomeless > 0) {
            pageString += homelessString;
        }

        return pageString;
    }

    protected static String availableBedsPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                              ServerWorld serverWorld) {
        List<PointOfInterest> availableBeds = serverWorld.getPointOfInterestStorage()
                .getInCircle(registryEntry -> registryEntry.matchesKey(PointOfInterestTypes.HOME), player.getBlockPos(),
                        CONFIG.debugConfig.villagerStatRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE)
                .collect(Collectors.toList());

        String pageString = Text.translatable("text.LibertyVillagers.villagerStats.format",
                Text.translatable("text.LibertyVillagers.villagerStats.numberOfAvailableBeds").getString(),
                availableBeds.size()).getString() + "\n\n";

        if (availableBeds.size() > 0) {
            pageString += Text.translatable("text.LibertyVillagers.villagerStats.bedsAt").getString() + "\n";
            for (PointOfInterest bed : availableBeds) {
                if (bed != null && bed.getPos() != null) {
                    pageString += bed.getPos().toShortString() + "\n";
                }
            }
        }

        return pageString;
    }
}

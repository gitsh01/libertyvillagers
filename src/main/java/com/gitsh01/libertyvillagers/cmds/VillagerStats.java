package com.gitsh01.libertyvillagers.cmds;

import com.gitsh01.libertyvillagers.LibertyVillagersClientInitializer;
import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import com.gitsh01.libertyvillagers.LibertyVillagersServerInitializer;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterest;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("villagerstats").executes(context -> {
                    processVillagerStats(context);
                    return 1;
                })));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated) -> dispatcher.register(literal("vs").executes(context -> {
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
                NbtString.of(new TranslatableText("text.LibertyVillagers.villagerStats.title").getString()));
        bookStack.setSubNbt("author", NbtString.of(player.getEntityName()));

        List<VillagerEntity> villagers = serverWorld.getNonSpectatingEntities(VillagerEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        NbtList pages = new NbtList();
        pages.addAll(splitToPageTags(titlePage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(professionPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(heldWorkstationPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(freeWorkstationsPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(homelessPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(availableBedsPage(player, villagers, serverWorld)));
        pages.addAll(splitToPageTags(golems(player, serverWorld)));
        pages.addAll(splitToPageTags(cats(player, serverWorld)));
        bookStack.setSubNbt("pages", pages);

        if (LibertyVillagersMod.isClient()) {
            LibertyVillagersClientInitializer.openBookScreen(bookStack);
        } else {
            if (FabricLoader.getInstance().getModContainer("server_translations_api").isEmpty()) {
                player.sendMessage(Text.of("Server_translations_api is missing. VillagerStats does not work " +
                        "server-side without translations."), false);
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


    protected static String titlePage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                      ServerWorld serverWorld) {
        String pageString = new TranslatableText("text.LibertyVillagers.villagerStats.title").getString() + "\n\n";

        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                new TranslatableText("text.LibertyVillagers.villagerStats.numberOfVillagers").getString(),
                villagers.size()).getString() + "\n";

        int babies = 0;
        int nitwits = 0;
        int unemployed = 0;
        int homeless = 0;
        for (VillagerEntity villager : villagers) {
            if (villager.getVillagerData().getProfession() == VillagerProfession.NITWIT) {
                nitwits++;
            }
            if (villager.getVillagerData().getProfession() == VillagerProfession.NONE) {
                unemployed++;
            }
            if (villager.isBaby()) {
                babies++;
            }
            if (!villager.getBrain().hasMemoryModule(MemoryModuleType.HOME)) {
                homeless++;
            }
        }

        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfNitwits").getString(), nitwits)
                .getString() + "\n";

        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfUnemployed").getString(), unemployed)
                .getString() + "\n";

        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfBabies").getString(), babies)
                .getString() + "\n";

        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfHomeless").getString(), homeless)
                .getString() + "\n";

        List<IronGolemEntity> golems = serverWorld.getNonSpectatingEntities(IronGolemEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));
        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfGolems").getString(), golems.size())
                .getString() + "\n";

        List<CatEntity> cats = serverWorld.getNonSpectatingEntities(CatEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        pageString += new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfCats").getString(), cats.size())
                .getString() + "\n";


        return pageString;
    }

    public static String translatedProfession(VillagerProfession profession) {
        String villagerTranslationKey = EntityType.VILLAGER.getTranslationKey();
        return
                new TranslatableText(villagerTranslationKey + "." + Registry.VILLAGER_PROFESSION.getId(profession).getPath()).getString();
    }

    protected static TreeMap<String, ProfessionInfo> createProfessionTreeMap() {
        TreeMap<String, ProfessionInfo> villagerProfessionMap = new TreeMap<>();

        for (Map.Entry<RegistryKey<VillagerProfession>, VillagerProfession> professionEntry : Registry.VILLAGER_PROFESSION.getEntrySet()) {
            VillagerProfession profession = professionEntry.getValue();
            String professionText = translatedProfession(profession);
            villagerProfessionMap.put(professionText, new ProfessionInfo(profession, 0));
        }

        return villagerProfessionMap;
    }

    protected static String professionPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                           ServerWorld serverWorld) {
        String pageString = new TranslatableText("text.LibertyVillagers.villagerStats.professions").getString() + "\n\n";
        TreeMap<String, ProfessionInfo> villagerProfessionMap = createProfessionTreeMap();

        for (VillagerEntity villager : villagers) {
            if (villager.isBaby()) {
                String babyText = new TranslatableText("text.LibertyVillagers.villagerStats.baby").toString();
                villagerProfessionMap.merge(babyText, new ProfessionInfo(villager.getVillagerData().getProfession(), 1),
                        ProfessionInfo::mergeProfessionInfo);
            } else {
                villagerProfessionMap.merge(translatedProfession(villager.getVillagerData().getProfession()),
                        new ProfessionInfo(villager.getVillagerData().getProfession(), 1),
                        ProfessionInfo::mergeProfessionInfo);
            }
        }

        AtomicReference<String> professions = new AtomicReference<>("");
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> professions.set(professions.get() +
                new TranslatableText("text.LibertyVillagers.villagerStats.professionsCountFormat", villagerProfession,
                        professionInfo.countVillagersWithProfession).getString() + "\n"));

        pageString += professions.get() + "\n\n";

        return pageString;
    }


    protected static String heldWorkstationPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                                ServerWorld serverWorld) {
        String pageString =
                new TranslatableText("text.LibertyVillagers.villagerStats.professionsHeldJobSites").getString() + "\n\n";
        TreeMap<String, ProfessionInfo> villagerProfessionMap = createProfessionTreeMap();
        AtomicReference<String> heldWorkstations = new AtomicReference<>("");
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> {
            long numOccupiedWorkstations = 0;
            if (!Objects.equals(villagerProfession, "baby")) {
                numOccupiedWorkstations = serverWorld.getPointOfInterestStorage()
                        .count(professionInfo.profession.getWorkStation().getCompletionCondition(), player.getBlockPos(),
                                CONFIG.debugConfig.villagerStatRange,
                                PointOfInterestStorage.OccupationStatus.IS_OCCUPIED);
            }
            heldWorkstations.set(heldWorkstations.get() +
                    new TranslatableText("text.LibertyVillagers.villagerStats.professionsCountFormat", villagerProfession,
                            numOccupiedWorkstations).getString() + "\n");
        });

        pageString += heldWorkstations.get() + "\n\n";

        return pageString;
    }


    protected static String freeWorkstationsPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                                 ServerWorld serverWorld) {
        String pageString =
                new TranslatableText("text.LibertyVillagers.villagerStats.professionsAvailableJobSites").getString() +
                        "\n\n";
        TreeMap<String, ProfessionInfo> villagerProfessionMap = createProfessionTreeMap();

        AtomicReference<String> availableWorkstations = new AtomicReference<>("");
        villagerProfessionMap.forEach((villagerProfession, professionInfo) -> {
            long numAvailableWorkstations = 0;
            if (!Objects.equals(villagerProfession, "baby")) {
                numAvailableWorkstations = serverWorld.getPointOfInterestStorage()
                        .count(professionInfo.profession.getWorkStation().getCompletionCondition(), player.getBlockPos(),
                                CONFIG.debugConfig.villagerStatRange,
                                PointOfInterestStorage.OccupationStatus.HAS_SPACE);
            }

            availableWorkstations.set(availableWorkstations.get() +
                    new TranslatableText("text.LibertyVillagers.villagerStats.professionsCountFormat", villagerProfession,
                            numAvailableWorkstations).getString() + "\n");
        });

        pageString += availableWorkstations.get() + "\n\n";

        return pageString;
    }

    protected static String homelessPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                         ServerWorld serverWorld) {
        StringBuilder homelessString = new StringBuilder();
        int numHomeless = 0;
        for (VillagerEntity villager : villagers) {
            if (!villager.getBrain().hasMemoryModule(MemoryModuleType.HOME)) {
                numHomeless++;
                homelessString.append(
                        new TranslatableText("text.LibertyVillagers.villagerStats.homeless", villager.getDisplayName(),
                                villager.getBlockPos().toShortString()).getString()).append("\n");
            }
        }

        String pageString = new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfHomeless").getString(), numHomeless)
                .getString() + "\n\n";
        if (numHomeless > 0) {
            pageString += homelessString;
        }

        return pageString;
    }

    protected static String availableBedsPage(ServerPlayerEntity player, List<VillagerEntity> villagers,
                                              ServerWorld serverWorld) {
        List<PointOfInterest> availableBeds = serverWorld.getPointOfInterestStorage()
                .getInCircle(poiType -> poiType == PointOfInterestType.HOME, player.getBlockPos(),
                        CONFIG.debugConfig.villagerStatRange, PointOfInterestStorage.OccupationStatus.HAS_SPACE)
                .toList();

        StringBuilder pageString = new StringBuilder(new TranslatableText("text.LibertyVillagers.villagerStats.format",
                new TranslatableText("text.LibertyVillagers.villagerStats.numberOfAvailableBeds").getString(),
                availableBeds.size()).getString() + "\n\n");

        if (availableBeds.size() > 0) {
            pageString.append(new TranslatableText("text.LibertyVillagers.villagerStats.bedsAt").getString())
                    .append("\n");
            for (PointOfInterest bed : availableBeds) {
                if (bed != null && bed.getPos() != null) {
                    pageString.append(bed.getPos().toShortString()).append("\n");
                }
            }
        }

        return pageString.toString();
    }

    protected static String golems(ServerPlayerEntity player, ServerWorld serverWorld) {
        List<IronGolemEntity> golems = serverWorld.getNonSpectatingEntities(IronGolemEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        StringBuilder pageString = new StringBuilder(new TranslatableText("text.LibertyVillagers.villagerStats.format",
                new TranslatableText("text.LibertyVillagers.villagerStats.numberOfGolems").getString(),
                golems.size()).getString() + "\n\n");

        if (golems.size() > 0) {
            pageString.append(new TranslatableText("text.LibertyVillagers.villagerStats.golemsAt").getString())
                    .append("\n");
            for (IronGolemEntity golem : golems) {
                if (golem != null && golem.getBlockPos() != null) {
                    pageString.append(
                            new TranslatableText("text.LibertyVillagers.villagerStats.homeless", golem.getDisplayName(),
                                    golem.getBlockPos().toShortString()).getString()).append("\n");
                }
            }
        }

        return pageString.toString();
    }

    protected static String translatedCatVariant(String variant) {
        return new TranslatableText("text.LibertyVillagers.villagerStats." + variant).getString();
    }

    protected static String catType(Identifier identifier) {
        return identifier.getPath().replaceAll("^(.*)/(.*)(\\..*)$", "$2");
    }

    protected static String cats(ServerPlayerEntity player, ServerWorld serverWorld) {
        List<CatEntity> cats = serverWorld.getNonSpectatingEntities(CatEntity.class,
                player.getBoundingBox().expand(CONFIG.debugConfig.villagerStatRange));

        String pageString = new TranslatableText("text.LibertyVillagers.villagerStats.format",
                        new TranslatableText("text.LibertyVillagers.villagerStats.numberOfCats").getString(), cats.size())
                .getString() + "\n\n";

        TreeMap<String, Integer> catVariantMap = new TreeMap<>();

        for (int i = 0; i < CatEntity.TEXTURES.size(); i++) {
            catVariantMap.put(translatedCatVariant(catType(CatEntity.TEXTURES.get(i))), 0);
        }

        if (cats.size() > 0) {
            for (CatEntity cat : cats) {
                String variant = translatedCatVariant(catType(cat.getTexture()));
                catVariantMap.merge(variant, 1, Integer::sum);
            }

            pageString += new TranslatableText("text.LibertyVillagers.villagerStats.catTypes").getString() + "\n";

            AtomicReference<String> catVariants = new AtomicReference<>("");
            catVariantMap.forEach((catVariant, sum) -> catVariants.set(catVariants.get() +
                    new TranslatableText("text.LibertyVillagers.villagerStats.professionsCountFormat",
                            catVariant, sum).getString() + "\n"));

            pageString += catVariants;
        }

        return pageString;
    }
}

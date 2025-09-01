package com.gitsh01.libertyvillagers.cmds;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import net.minecraft.world.poi.PointOfInterestTypes;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public class VillagerSetPOI {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
                literal("villagersetpoi").executes(context -> {
                    processVillagerSetPOI(context);
                    return 1;
                })));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess, environment) -> dispatcher.register(literal("vsp").executes(context -> {
                    processVillagerSetPOI(context);
                    return 1;
                })));
    }

    public static void processVillagerSetPOI(CommandContext<ServerCommandSource> command) {
        ServerCommandSource source = command.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld serverWorld = source.getWorld();

        float maxDistance = 50;
        float tickDelta = 0;
        HitResult hit = player.raycast(maxDistance, tickDelta, false);

        switch (hit.getType()) {
            case MISS -> player.sendMessage(Text.translatable("text.LibertyVillagers.villagerSetPOI.miss"));
            case BLOCK -> {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                BlockState blockState = serverWorld.getBlockState(blockPos);
                handleBlockHit(player, serverWorld, blockPos, blockState);
            }
            case ENTITY -> player.sendMessage(Text.translatable("text.LibertyVillagers.villagerSetPOI.entity"));
        }
    }

    protected static void handleBlockHit(ServerPlayerEntity player, ServerWorld serverWorld, BlockPos blockPos,
                                         BlockState blockState) {
        Block block = blockState.getBlock();
        Text name = block.getName();

        Optional<RegistryEntry<PointOfInterestType>> optionalRegistryEntry =
                PointOfInterestTypes.getTypeForState(blockState);
        if (optionalRegistryEntry.isEmpty()) {
            player.sendMessage(Text.translatable("text.LibertyVillagers.villagerSetPOI.notPOIType", name));
            return;
        }

        Optional<RegistryKey<PointOfInterestType>> optionalRegistryKey = optionalRegistryEntry.get().getKey();
        if (optionalRegistryKey.isEmpty()) {
            player.sendMessage(Text.translatable("text.LibertyVillagers.villagerSetPOI.notPOIType", name));
            return;
        }

        String poiTypeName = optionalRegistryKey.get().getValue().toString();
        PointOfInterestStorage storage = serverWorld.getPointOfInterestStorage();

        if (!storage.hasTypeAt(optionalRegistryKey.get(), blockPos)) {
            storage.add(blockPos, optionalRegistryEntry.get());
            DebugInfoSender.sendPoiAddition(serverWorld, blockPos);
            player.sendMessage(Text.translatable("text.LibertyVillagers.villagerSetPOI.enable", name, poiTypeName));
        } else {
            storage.remove(blockPos);
            DebugInfoSender.sendPoiRemoval(serverWorld, blockPos);
            player.sendMessage(Text.translatable("text.LibertyVillagers.villagerSetPOI.disable", name, poiTypeName));
        }
    }
}

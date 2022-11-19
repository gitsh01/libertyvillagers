package com.gitsh01.libertyvillagers.cmds;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Optional;

import static net.minecraft.server.command.CommandManager.literal;

public class VillagerSetPOI {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("villagersetpoi").executes(context -> {
                    processVillagerSetPOI(context);
                    return 1;
                })));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated) -> dispatcher.register(literal("vsp").executes(context -> {
                    processVillagerSetPOI(context);
                    return 1;
                })));
    }

    public static void processVillagerSetPOI(CommandContext<ServerCommandSource> command)
            throws CommandSyntaxException {
        ServerCommandSource source = command.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld serverWorld = source.getWorld();

        float maxDistance = 50;
        float tickDelta = 0;
        HitResult hit = player.raycast(maxDistance, tickDelta, false);

        switch (hit.getType()) {
            case MISS -> player.sendMessage(new TranslatableText("text.LibertyVillagers.villagerSetPOI.miss"), false);
            case BLOCK -> {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                BlockState blockState = serverWorld.getBlockState(blockPos);
                handleBlockHit(player, serverWorld, blockPos, blockState);
            }
            case ENTITY -> {
                EntityHitResult entityHit = (EntityHitResult) hit;
                Entity entity = entityHit.getEntity();
                player.sendMessage(new TranslatableText("text.LibertyVillagers.villagerSetPOI.entity"), false);
            }
        }
    }

    protected static void handleBlockHit(ServerPlayerEntity player, ServerWorld serverWorld, BlockPos blockPos,
                                         BlockState blockState) {
        Block block = blockState.getBlock();
        Text name = block.getName();

        Optional<PointOfInterestType> optionalRegistryEntry =
                PointOfInterestType.from(blockState);
        if (optionalRegistryEntry.isEmpty()) {
            player.sendMessage(new TranslatableText("text.LibertyVillagers.villagerSetPOI.notPOIType", name), false);
            return;
        }

        PointOfInterestType poiType = optionalRegistryEntry.get();
        String poiTypeName = poiType.getId();
        PointOfInterestStorage storage = serverWorld.getPointOfInterestStorage();

        if (!storage.hasTypeAt(poiType, blockPos)) {
            storage.add(blockPos, poiType);
            DebugInfoSender.sendPoiAddition(serverWorld, blockPos);
            player.sendMessage(new TranslatableText("text.LibertyVillagers.villagerSetPOI.enable", name, poiTypeName)
                    , false);
        } else {
            storage.remove(blockPos);
            DebugInfoSender.sendPoiRemoval(serverWorld, blockPos);
            player.sendMessage(new TranslatableText("text.LibertyVillagers.villagerSetPOI.disable", name,
                    poiTypeName), false);
        }
    }
}

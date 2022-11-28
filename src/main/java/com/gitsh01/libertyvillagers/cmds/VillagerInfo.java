package com.gitsh01.libertyvillagers.cmds;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.dynamic.GlobalPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;
import static net.minecraft.server.command.CommandManager.literal;

public class VillagerInfo {

    final static String BLANK_COORDS = "                 ";

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("villagerinfo").executes(context -> {
                    processVillagerInfo(context);
                    return 1;
                })));
        CommandRegistrationCallback.EVENT.register(
                (dispatcher, dedicated) -> dispatcher.register(literal("vi").executes(context -> {
                    processVillagerInfo(context);
                    return 1;
                })));
    }

    public static void processVillagerInfo(CommandContext<ServerCommandSource> command) throws CommandSyntaxException {
        ServerCommandSource source = command.getSource();
        ServerPlayerEntity player = source.getPlayer();
        ServerWorld serverWorld = source.getWorld();

        float maxDistance = 50;
        float tickDelta = 0;
        Vec3d vec3d = player.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = player.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        HitResult hit = serverWorld.raycast(
                new RaycastContext(vec3d, vec3d3, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE,
                        player));

        // Found a block between us and the max distance, update the max distance for the entity check.
        if (hit.getType() != HitResult.Type.MISS) {
            vec3d3 = hit.getPos();
        }

        HitResult hitResult2;
        // Look for an entity between us and the block.
        if ((hitResult2 = ProjectileUtil.getEntityCollision(serverWorld, player, vec3d2, vec3d3,
                player.getBoundingBox().stretch(player.getVelocity()).expand(maxDistance), Entity::isAlive)) != null) {
            hit = hitResult2;
        }

        List<Text> lines = null;
        switch (hit.getType()) {
            case MISS:
                break;
            case BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                BlockState blockState = serverWorld.getBlockState(blockPos);
                if (blockState != null) {
                    lines = getBlockInfo(serverWorld, blockPos, blockState);
                }
                break;
            case ENTITY:
                EntityHitResult entityHit = (EntityHitResult) hit;
                Entity entity = entityHit.getEntity();
                if (entity != null) {
                    lines = getEntityInfo(serverWorld, entity);
                }
                break;
        }

        if (lines != null) {
            for (Text line : lines) {
                player.sendMessage(line, false);
            }
        }
    }


    public static List<Text> getEntityInfo(ServerWorld serverWorld, Entity entity) {
        List<Text> lines = new ArrayList<>();
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.title"));
        Text name = entity.getDisplayName();
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.name", name));

        if (!(entity instanceof VillagerEntity)) {
            return lines;
        }

        VillagerEntity villager = (VillagerEntity)entity;
        String occupation =
                VillagerStats.translatedProfession(villager.getVillagerData().getProfession());
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.occupation", occupation));

        // Client-side villagers don't have memories.
        if (serverWorld == null) {
            lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.needsServer"));
            return lines;
        }

        Optional<GlobalPos> home = villager.getBrain().getOptionalMemory(MemoryModuleType.HOME);
        String homeCoords = home.isPresent() ? home.get().getPos().toShortString() : BLANK_COORDS;
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.home", homeCoords));

        Optional<GlobalPos> jobSite = villager.getBrain().getOptionalMemory(MemoryModuleType.JOB_SITE);
        String jobSiteCoords = jobSite.isPresent() ? jobSite.get().getPos().toShortString() : BLANK_COORDS;
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.jobSite", jobSiteCoords));

        Optional<GlobalPos> potentialJobSite =
                villager.getBrain().getOptionalMemory(MemoryModuleType.POTENTIAL_JOB_SITE);
        String potentialJobSiteCoords =
                potentialJobSite.isPresent() ? potentialJobSite.get().getPos().toShortString() : BLANK_COORDS;
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.potentialJobSite", potentialJobSiteCoords));

        Optional<GlobalPos> meetingPoint = villager.getBrain().getOptionalMemory(MemoryModuleType.MEETING_POINT);
        String meetingPointCoords =
                meetingPoint.isPresent() ? meetingPoint.get().getPos().toShortString() : BLANK_COORDS;
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.meetingPoint", meetingPointCoords));

        Optional<WalkTarget> walkTarget = villager.getBrain().getOptionalMemory(MemoryModuleType.WALK_TARGET);
        String walkTargetCoords =
                walkTarget.isPresent() ? walkTarget.get().getLookTarget().getBlockPos().toShortString() : BLANK_COORDS;
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.walkTarget", walkTargetCoords,
                walkTarget.map(WalkTarget::getCompletionRange).orElse(0)));

        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.inventory"));

        if (villager.getInventory().isEmpty()) {
            lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.empty"));
        } else {
            for(int i = 0; i < villager.getInventory().size(); i++) {
                ItemStack stack = villager.getInventory().getStack(i);
                if (!stack.isEmpty()) {
                    lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.inventoryLine", stack.getCount(),
                            stack.getName()));
                }
            }
        }

        if (villager.getNavigation().getCurrentPath() != null && CONFIG.debugConfig.villagerInfoShowsPath) {
            lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.path"));
            Path path = villager.getNavigation().getCurrentPath();
            for (int i = path.getCurrentNodeIndex(); i < path.getLength(); i++) {
                lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.pathnode", i,
                        path.getNode(i).getBlockPos().toShortString()));
            }
        }

        return lines;
    }

    public static List<Text> getBlockInfo(ServerWorld serverWorld, BlockPos blockPos, BlockState blockState) {
        List<Text> lines = new ArrayList<>();
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.title"));
        Block block = blockState.getBlock();
        Text name = block.getName();
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.name", name));

        if (serverWorld == null) {
            lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.needsServer"));
            return lines;
        }

        Optional<PointOfInterestType> optionalRegistryEntry =
                PointOfInterestType.from(blockState);
        if (optionalRegistryEntry.isEmpty()) {
            lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.poiType",
                    new TranslatableText("text.LibertyVillagers.villagerInfo.none")));
            return lines;
        }

        PointOfInterestType poiType = optionalRegistryEntry.get();
        String poiTypeName =  poiType.getId();

        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.poiType", poiTypeName));

        PointOfInterestStorage storage = serverWorld.getPointOfInterestStorage();
        if (!storage.hasTypeAt(poiType, blockPos)) {
            lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.notAPOI"));
            return lines;
        }

        int freeTickets = storage.getFreeTickets(blockPos);
        Text isOccupied =
                freeTickets < poiType.getTicketCount() ? new TranslatableText("text.LibertyVillagers.villagerInfo" +
                        ".true") :
                        new TranslatableText("text" + ".LibertyVillagers.villagerInfo.false");
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.isOccupied", isOccupied));
        lines.add(new TranslatableText("text.LibertyVillagers.villagerInfo.freeTickets", freeTickets,
                poiType.getTicketCount()));

        return lines;

    }
}
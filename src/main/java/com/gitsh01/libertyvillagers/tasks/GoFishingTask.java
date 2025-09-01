package com.gitsh01.libertyvillagers.tasks;

import com.gitsh01.libertyvillagers.mixin.FishingBobberEntityAccessorMixin;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.BlockPosLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.MultiTickTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;


import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

public class GoFishingTask extends MultiTickTask<VillagerEntity> {

    private static final int MAX_RUN_TIME = 40 * 20;
    private static final int TURN_TIME = 3 * 20;

    // Give the villager time to look at the water, so they aren't throwing the bobber behind their heads.
    private long bobberCountdown;

    private boolean hasThrownBobber = false;

    @Nullable
    private BlockPos targetBlockPos;

    @Nullable
    private FishingBobberEntity bobber = null;

    public GoFishingTask() {
        super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.VALUE_ABSENT,
                MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT, MemoryModuleType.JOB_SITE,
                MemoryModuleState.VALUE_PRESENT), MAX_RUN_TIME);
    }

    protected boolean shouldRun(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        // This just looks wrong.
        if (villagerEntity.isTouchingWater()) {
            return false;
        }

        // Look for water nearby.
        BlockPos villagerPos = villagerEntity.getBlockPos();
        for (BlockPos blockPos : BlockPos.iterateOutwards(villagerPos, CONFIG.villagersProfessionConfig.fishermanFishingWaterRange,
                CONFIG.villagersProfessionConfig.fishermanFishingWaterRange, CONFIG.villagersProfessionConfig.fishermanFishingWaterRange)) {
            // Don't fish "up".
            if (blockPos.getY() > villagerPos.getY()) continue;
            // Don't fish on ourselves (it looks odd).
            if (blockPos.isWithinDistance(villagerPos, 1)) continue;
            if (serverWorld.getBlockState(blockPos).getFluidState().isStill() &&
                    serverWorld.getBlockState(blockPos.up()).isOf(Blocks.AIR)) {
                Vec3d bobberStartPosition = getBobberStartPosition(villagerEntity, blockPos);

                // Make sure the bobber won't be starting in a solid wall of a boat.
                if (serverWorld.getBlockState(BlockPos.ofFloored(bobberStartPosition)).isOpaque()) {
                    continue;
                }

                Vec3d centerBlockPos = Vec3d.ofCenter(blockPos);
                // Ray trace to see if the villager can actually fish on that spot.
                // Use the lower edge of the bobber since it seems to get caught on the floor first.
                Box box = EntityType.FISHING_BOBBER.getDimensions().getBoxAt(bobberStartPosition);
                Vec3d lowerEdge = new Vec3d(0, -1 * box.getLengthY() / 2, 0);
                if (doesNotHitValidWater(bobberStartPosition, lowerEdge, centerBlockPos, villagerEntity, serverWorld)) {
                    continue;
                }

                // Next, look for an entity between us and the block that the bobber might hit to avoid fishing
                // through buddies.
                if (ProjectileUtil.getEntityCollision(serverWorld, villagerEntity, bobberStartPosition, centerBlockPos,
                        box, Entity::isAlive) != null) {
                    // We're going to hit someone.
                    continue;
                }

                // Now check if the lower right or lower left are going to hit something (like that fence)....
                Vec3d lowerLeftEdge = new Vec3d(-1 * box.getLengthY() / 2, -1 * box.getLengthY() / 2, 0);
                if (doesNotHitValidWater(bobberStartPosition, lowerLeftEdge, centerBlockPos, villagerEntity,
                        serverWorld)) {
                    continue;
                }
                Vec3d lowerRightEdge = new Vec3d(1 * box.getLengthX() / 2, -1 * box.getLengthX() / 2, 0);
                if (doesNotHitValidWater(bobberStartPosition, lowerRightEdge, centerBlockPos, villagerEntity,
                        serverWorld)) {
                    continue;
                }

                // This check is expensive, so stop on the first one we find that works, instead of looking
                // for more.
                targetBlockPos = blockPos;
                return true;
            }
        }

        return false;
    }

    protected void run(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.FISHING_ROD));
        villagerEntity.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0f);
        villagerEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new BlockPosLookTarget(targetBlockPos.up()));
        bobberCountdown = TURN_TIME + time;
    }

    protected boolean shouldKeepRunning(ServerWorld world, VillagerEntity entity, long time) {
        // Villager dropped the fishing rod for some reason.
        if (!entity.getMainHandStack().isOf(Items.FISHING_ROD)) {
            return false;
        }

        if (!this.hasThrownBobber) {
            return true;
        }

        if (bobber == null || bobber.isRemoved() ||
                (bobber.isOnGround() && !bobber.getBlockStateAtPos().isOf(Blocks.WATER))) {
            return false;
        }

        // Still initilizing...
        if (bobber.getDataTracker() == null) {
            return true;
        }

        boolean caughtFish = bobber.getDataTracker().get(FishingBobberEntityAccessorMixin.getCaughtFish());
        return !caughtFish;
    }

    protected void keepRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long time) {
        if (!hasThrownBobber && time > bobberCountdown) {
            throwBobber(villagerEntity, serverWorld);
        }
    }

    protected void finishRunning(ServerWorld serverWorld, VillagerEntity villagerEntity, long l) {
        villagerEntity.getBrain().forget(MemoryModuleType.LOOK_TARGET);
        villagerEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
        if (bobber != null) {
            bobber.use(ItemStack.EMPTY);
            bobber = null;
        }
        // Remove fishing pole.
        villagerEntity.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        this.hasThrownBobber = false;
        this.bobberCountdown = 0;
    }

    Vec3d getBobberStartPosition(VillagerEntity thrower, BlockPos targetBlockPos) {
        Vec3d targetPosition = Vec3d.ofCenter(targetBlockPos);
        double d = targetPosition.x - thrower.getX();
        double f = targetPosition.z - thrower.getZ();

        double x = thrower.getX() + (d * 0.3);
        double y = thrower.getEyeY();
        double z = thrower.getZ() + (f * 0.3);
        return new Vec3d(x, y, z);
    }

    void throwBobber(VillagerEntity thrower, ServerWorld serverWorld) {
        bobber = new FishingBobberEntity(EntityType.FISHING_BOBBER, serverWorld);
        bobber.setOwner(thrower);

        Vec3d bobberStartPosition = getBobberStartPosition(thrower, targetBlockPos);

        bobber.refreshPositionAndAngles(bobberStartPosition.x, bobberStartPosition.y, bobberStartPosition.z,
                thrower.getYaw(),
                thrower.getPitch());

        Vec3d targetPosition = Vec3d.ofCenter(targetBlockPos);
        double d = targetPosition.x - bobberStartPosition.x;
        double e = targetPosition.y - bobberStartPosition.y;
        double f = targetPosition.z - bobberStartPosition.z;
        double g = 0.1;

        Vec3d vec3d = new Vec3d(d * g, e * g, f * g);

        bobber.setVelocity(vec3d);
        bobber.setYaw((float) (MathHelper.atan2(vec3d.x, vec3d.z) * 57.2957763671875));
        bobber.setPitch((float) (MathHelper.atan2(vec3d.y, vec3d.horizontalLength()) * 57.2957763671875));
        bobber.prevYaw = bobber.getYaw();
        bobber.prevPitch = bobber.getPitch();

        serverWorld.spawnEntity(bobber);
        serverWorld.playSound(null, thrower.getX(), thrower.getY(), thrower.getZ(),
                SoundEvents.ENTITY_FISHING_BOBBER_THROW, SoundCategory.NEUTRAL, 0.5f,
                0.4f / (serverWorld.getRandom().nextFloat() * 0.4f + 0.8f));
        hasThrownBobber = true;
    }

    boolean doesNotHitValidWater(Vec3d bobberStartPosition, Vec3d bobberEdge, Vec3d centerBlockPos,
                                 VillagerEntity villagerEntity, ServerWorld serverWorld) {
        BlockHitResult hitResult = serverWorld.raycast(
                new RaycastContext(bobberStartPosition.add(bobberEdge),
                        centerBlockPos,
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.ANY,
                        villagerEntity));

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockState blockState = serverWorld.getBlockState(hitResult.getBlockPos());
            return !blockState.isOf(Blocks.WATER) || !blockState.getFluidState().isStill();
        }
        return true;
    }
}
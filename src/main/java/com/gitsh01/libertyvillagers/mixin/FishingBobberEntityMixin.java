package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(FishingBobberEntity.class)
public abstract class FishingBobberEntityMixin extends ProjectileEntity {

    // Why do I get mixin errors when I use my own enum? Why can't I use FishingBobberEntity's enum? :(
    boolean isFlying = true;
    boolean isBobbing = false;

    @Shadow
    private int removalTimer;
    @Shadow
    private Random velocityRandom;
    @Shadow
    private boolean caughtFish;
    @Shadow
    private int outOfOpenWaterTicks;
    @Shadow
    private int hookCountdown;
    @Shadow
    private int fishTravelCountdown;
    @Shadow
    private boolean inOpenWater = true;

    public FishingBobberEntityMixin(World world) {
        super(EntityType.FISHING_BOBBER, world);
    }

    @Shadow
    abstract void checkForCollision();

    @Shadow
    abstract void tickFishingLogic(BlockPos pos);

    @Shadow
    abstract boolean isOpenOrWaterAround(BlockPos pos);

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    void tickForVillagerOwnedBobber(CallbackInfo ci) {
        if (this.getOwner() == null || this.getOwner().getType() != EntityType.VILLAGER) {
            return;
        }

        if (!this.getWorld().isClient && this.removeIfInvalidOwner()) {
            ci.cancel();
            return;
        }

        if (this.isOnGround()) {
            ++this.removalTimer;
            if (this.removalTimer >= 1200) {
                this.discard();
                ci.cancel();
                return;
            }
        } else {
            this.removalTimer = 0;
        }
        float f = 0.0f;
        BlockPos blockPos = this.getBlockPos();
        FluidState fluidState = this.getWorld().getFluidState(blockPos);
        if (fluidState.isIn(FluidTags.WATER)) {
            f = fluidState.getHeight(this.getWorld(), blockPos);
        }
        boolean bl = f > 0.0f;
        if (isFlying) {
            if (bl) {
                this.setVelocity(this.getVelocity().multiply(0.3, 0.3, 0.3));
                isFlying = false;
                isBobbing = true;
                ci.cancel();
                return;
            }
            this.checkForCollision();
        } else {
            if (isBobbing) {
                Vec3d vec3d = this.getVelocity();
                double d = this.getY() + vec3d.y - (double) blockPos.getY() - (double) f;
                if (Math.abs(d) < 0.01) {
                    d += Math.signum(d) * 0.1;
                }
                this.setVelocity(vec3d.x * 0.9, vec3d.y - d * (double) this.random.nextFloat() * 0.2, vec3d.z * 0.9);
                this.inOpenWater = this.hookCountdown <= 0 && this.fishTravelCountdown <= 0 ||
                        this.inOpenWater && this.outOfOpenWaterTicks < 10 && this.isOpenOrWaterAround(blockPos);
                if (bl) {
                    this.outOfOpenWaterTicks = Math.max(0, this.outOfOpenWaterTicks - 1);
                    if (this.caughtFish) {
                        this.setVelocity(this.getVelocity().add(0.0, -0.1 * (double) this.velocityRandom.nextFloat() *
                                (double) this.velocityRandom.nextFloat(), 0.0));
                    }
                    if (!this.getWorld().isClient) {
                        this.tickFishingLogic(blockPos);
                    }
                } else {
                    this.outOfOpenWaterTicks = Math.min(10, this.outOfOpenWaterTicks + 1);
                }
            }
        }
        this.move(MovementType.SELF, this.getVelocity());
        this.updateRotation();
        if (isFlying && (this.isOnGround() || this.horizontalCollision)) {
            this.setVelocity(Vec3d.ZERO);
        }
        this.setVelocity(this.getVelocity().multiply(0.92));
        this.refreshPosition();
        ci.cancel();
    }

    private boolean removeIfInvalidOwner() {
        Entity owner = this.getOwner();
        if (owner == null || !owner.isAlive()) {
            this.discard();
            return true;
        }
        VillagerEntity villager = (VillagerEntity) owner;
        ItemStack itemStack = villager.getMainHandStack();
        boolean bl = itemStack.isOf(Items.FISHING_ROD);
        if (!bl || this.squaredDistanceTo(villager) > (CONFIG.villagersProfessionConfig.fishermanFishingWaterRange *
                CONFIG.villagersProfessionConfig.fishermanFishingWaterRange)) {
            this.discard();
            return true;
        }
        return false;
    }

    @Inject(method = "onSpawnPacket(Lnet/minecraft/network/packet/s2c/play/EntitySpawnS2CPacket;)V", at = @At("HEAD"),
            cancellable = true)
    public void onSpawnPacket(EntitySpawnS2CPacket packet, CallbackInfo ci) {
        super.onSpawnPacket(packet);
        ci.cancel();
    }

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(ItemStack usedItem, CallbackInfoReturnable<Integer> cir) {
        if (this.getWorld().isClient || this.getOwner() == null || this.getOwner().getType() != EntityType.VILLAGER) {
            return;
        }
        VillagerEntity villager = (VillagerEntity) this.getOwner();
        int i = 0;
        if (this.hookCountdown > 0) {
            Item fish = this.getWorld().getRandom().nextInt(2) == 0 ? Items.COD : Items.SALMON;
            ItemStack itemStack = new ItemStack(fish);
            ItemEntity itemEntity = new ItemEntity(this.getWorld(), this.getX(), this.getY(), this.getZ(), itemStack);
            double d = villager.getX() - this.getX();
            double e = villager.getY() - this.getY();
            double f = villager.getZ() - this.getZ();
            itemEntity.setVelocity(d * 0.1, e * 0.1 + Math.sqrt(Math.sqrt(d * d + e * e + f * f)) * 0.08, f * 0.1);
            this.getWorld().spawnEntity(itemEntity);
            i = 1;
        }
        if (this.isOnGround()) {
            i = 2;
        }
        this.discard();
        cir.setReturnValue(i);
        cir.cancel();
    }

    @Inject(method = "onEntityHit",
            at = @At("HEAD"),
            cancellable = true)
    protected void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        if (this.getOwner() != null && this.getOwner().getType() == EntityType.VILLAGER) {
            // Don't "hook" entities.
            super.onEntityHit(entityHitResult);
            ci.cancel();
        }
    }
}

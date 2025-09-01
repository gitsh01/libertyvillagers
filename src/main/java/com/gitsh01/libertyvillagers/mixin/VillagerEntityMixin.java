package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.DebugInfoSender;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerDataContainer;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;
import java.util.function.BiPredicate;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;
import static net.minecraft.entity.passive.VillagerEntity.POINTS_OF_INTEREST;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity implements InteractionObserver, VillagerDataContainer {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract VillagerData getVillagerData();

    @Shadow
    public abstract void setVillagerData(VillagerData villagerData);

    @Shadow
    public static Map<Item, Integer> ITEM_FOOD_VALUES;

    @Unique
    private static Set<Item> GATHERABLE_ITEMS = Sets.newHashSet();

    @Inject(method = "<clinit>", at = @At("TAIL"))
    static private void modifyStaticBlock(CallbackInfo ci) {
        // Only specific professions should have seeds and wheat.
        GATHERABLE_ITEMS =  ImmutableSet.copyOf(Sets.difference(GATHERABLE_ITEMS,
                ImmutableSet.of(Items.WHEAT_SEEDS, Items.BEETROOT_SEEDS, Items.WHEAT)));
        if (CONFIG.villagersGeneralConfig.villagersEatMelons) {
            GATHERABLE_ITEMS = new HashSet<>(GATHERABLE_ITEMS);
            GATHERABLE_ITEMS.add(Items.MELON_SLICE);
            ITEM_FOOD_VALUES = new HashMap<>(ITEM_FOOD_VALUES);
            ITEM_FOOD_VALUES.put(Items.MELON_SLICE, 1);
        }
        if (CONFIG.villagersProfessionConfig.farmersHarvestMelons) {
            GATHERABLE_ITEMS = new HashSet<>(GATHERABLE_ITEMS);
            GATHERABLE_ITEMS.add(Items.MELON_SLICE);
        }
        if (CONFIG.villagersGeneralConfig.villagersEatPumpkinPie) {
            ITEM_FOOD_VALUES = new HashMap<>(ITEM_FOOD_VALUES);
            ITEM_FOOD_VALUES.put(Items.PUMPKIN_PIE, 1);
            GATHERABLE_ITEMS = new HashSet<>(GATHERABLE_ITEMS);
            GATHERABLE_ITEMS.add(Items.PUMPKIN_PIE);
        }
        if (CONFIG.villagersGeneralConfig.villagersEatCookedFish) {
            ITEM_FOOD_VALUES = new HashMap<>(ITEM_FOOD_VALUES);
            ITEM_FOOD_VALUES.put(Items.COOKED_COD, 1);
            ITEM_FOOD_VALUES.put(Items.COOKED_SALMON, 1);
            GATHERABLE_ITEMS = new HashSet<>(GATHERABLE_ITEMS);
            GATHERABLE_ITEMS.add(Items.COOKED_COD);
            GATHERABLE_ITEMS.add(Items.COOKED_SALMON);
        }
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    public void villagerInit(EntityType<? extends MerchantEntity> entityType, World world, CallbackInfo ci) {
        if (CONFIG.villagerPathfindingConfig.villagersAvoidCactus) {
            this.setPathfindingPenalty(PathNodeType.DANGER_OTHER, 16);
        }
        if (CONFIG.villagerPathfindingConfig.villagersAvoidWater) {
            this.setPathfindingPenalty(PathNodeType.WATER, -1);
            this.setPathfindingPenalty(PathNodeType.WATER_BORDER, 16);
        }
        if (CONFIG.villagerPathfindingConfig.villagersAvoidRail) {
            this.setPathfindingPenalty(PathNodeType.RAIL, -1);
        }
        if (CONFIG.villagerPathfindingConfig.villagersAvoidTrapdoor) {
            this.setPathfindingPenalty(PathNodeType.TRAPDOOR, -1);
        }
        if (CONFIG.villagerPathfindingConfig.villagersAvoidPowderedSnow) {
            this.setPathfindingPenalty(PathNodeType.POWDER_SNOW, -1);
            this.setPathfindingPenalty(PathNodeType.DANGER_POWDER_SNOW, 16);
        }
        if (CONFIG.villagersGeneralConfig.allBabyVillagers) {
            this.setBaby(true);
        }
    }

    @Inject(at = @At("HEAD"), method = "initBrain(Lnet/minecraft/entity/ai/brain/Brain;)V")
    private void changeVillagerProfession(Brain<VillagerEntity> brain, CallbackInfo ci) {
        if (!(this.getWorld() instanceof ServerWorld)) {
            return;
        }
        ServerWorld world = (ServerWorld) this.getWorld();

        VillagerProfession profession = this.getVillagerData().getProfession();
        if (CONFIG.villagersGeneralConfig.noNitwitVillagers && profession == VillagerProfession.NITWIT) {
            this.setVillagerData(getVillagerData().withProfession(VillagerProfession.NONE));
            brain.stopAllTasks(world, (VillagerEntity) ((Object) this));
        }
        if (CONFIG.villagersGeneralConfig.allNitwitVillagers && profession != VillagerProfession.NITWIT) {
            this.setVillagerData(getVillagerData().withProfession(VillagerProfession.NITWIT));
            this.releaseTicketFor(brain, world, MemoryModuleType.JOB_SITE);
            this.releaseTicketFor(brain, world, MemoryModuleType.POTENTIAL_JOB_SITE);
            brain.stopAllTasks(world, (VillagerEntity) ((Object) this));
        }
    }

    // The brain is not yet assigned when initBrain is called, so it must be specified.
    public void releaseTicketFor(Brain<VillagerEntity> brain, ServerWorld world, MemoryModuleType<GlobalPos> memoryModuleType) {
        MinecraftServer minecraftServer = world.getServer();
        brain.getOptionalMemory(memoryModuleType).ifPresent(pos -> {
            ServerWorld serverWorld = minecraftServer.getWorld(pos.dimension());
            if (serverWorld == null) {
                return;
            }
            PointOfInterestStorage pointOfInterestStorage = serverWorld.getPointOfInterestStorage();
            Optional<RegistryEntry<PointOfInterestType>> optional = pointOfInterestStorage.getType(pos.pos());
            BiPredicate<VillagerEntity, RegistryEntry<PointOfInterestType>> biPredicate = POINTS_OF_INTEREST.get(memoryModuleType);
            if (optional.isPresent() && biPredicate.test((VillagerEntity) ((Object) this), optional.get())) {
                pointOfInterestStorage.releaseTicket(pos.pos());
                DebugInfoSender.sendPointOfInterest(serverWorld, pos.pos());
            }
        });
    }

    @Inject(method = "hasSeedToPlant()Z",
            at = @At("HEAD"),
            cancellable = true)
    public void hasExtraSeedToPlant(CallbackInfoReturnable<Boolean> cir) {
        Set<Item> extraSeeds = new HashSet<>();
        if (CONFIG.villagersProfessionConfig.farmersHarvestMelons) {
            extraSeeds.add(Items.MELON_SLICE);
            extraSeeds.add(Items.MELON_SEEDS);
        }
        if (CONFIG.villagersProfessionConfig.farmersHarvestPumpkins) {
            extraSeeds.add(Items.PUMPKIN);
            extraSeeds.add(Items.PUMPKIN_SEEDS);
        }
        if (!extraSeeds.isEmpty() && this.getInventory().containsAny(extraSeeds)) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "onGrowUp()V", at = @At("HEAD"), cancellable = true)
    private void babiesNeverGrowUp(CallbackInfo ci) {
        if (CONFIG.villagersGeneralConfig.allBabyVillagers || CONFIG.villagersGeneralConfig.foreverYoung) {
            this.setBaby(true);
            ci.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "wakeUp()V")
    private void healOnWakeUp(CallbackInfo info) {
        if (CONFIG.villagersGeneralConfig.healOnWake) {
            // Heal villager upon waking up.
            this.heal(this.getMaxHealth());
        }
    }

    @Inject(at = @At("HEAD"), method = "isReadyToBreed()Z", cancellable = true)
    public void replaceIsReadyToBreed(CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.villagersGeneralConfig.villagersDontBreed) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(at = @At("HEAD"), method = "canSummonGolem(J)Z", cancellable = true)
    public void replaceCanSummonGolem(long time, CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.golemsConfig.villagersDontSummonGolems) {
            cir.setReturnValue(false);
            cir.cancel();
        }
        if (CONFIG.golemsConfig.golemSpawnLimit) {
            List<IronGolemEntity> golems = this.getWorld().getNonSpectatingEntities(IronGolemEntity.class,
                    this.getBoundingBox().expand(CONFIG.golemsConfig.golemSpawnLimitRange));
            if (golems.size() >= CONFIG.golemsConfig.golemSpawnLimitCount) {
                cir.setReturnValue(false);
                cir.cancel();
            }
        }
    }

    @Inject(method = "readCustomDataFromNbt",
            at = @At("TAIL"))
   public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        // If initialized with a rod, get rid of it.
        if (this.getMainHandStack().isOf(Items.FISHING_ROD)) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
        // Get rid of items the villager can't gather.
        for (int i = this.getInventory().size(); i >= 0; i-- ) {
            ItemStack stack = this.getInventory().getStack(i);
            if (stack.isEmpty()) continue;
            if (GATHERABLE_ITEMS.contains(stack.getItem())) continue;
            if (this.getVillagerData().getProfession().gatherableItems().contains(stack.getItem())) continue;
            this.getInventory().removeStack(i);
        }
    }

    @Inject(method = "setAttacker",
            at = @At("TAIL"))
    public void setAttacker(@Nullable LivingEntity attacker, CallbackInfo ci) {
        // Drop the rod if attacked.
        if (this.getMainHandStack().isOf(Items.FISHING_ROD)) {
            this.equipStack(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        }
    }
}
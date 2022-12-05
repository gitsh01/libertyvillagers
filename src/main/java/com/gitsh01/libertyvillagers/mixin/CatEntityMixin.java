package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(CatEntity.class)
public abstract class CatEntityMixin extends TameableEntity {

    @Shadow
    public abstract void setVariant(CatVariant variant);

    public CatEntityMixin(EntityType<? extends CatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;" +
            "Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/EntityData;",
            at = @At("RETURN"))
    void addPersistantToInitialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason,
                                   @Nullable EntityData entityData, @Nullable NbtCompound entityNbt,
                                   CallbackInfoReturnable<EntityData> cir) {
        if (CONFIG.catsConfig.villageCatsDontDespawn) {
            this.setPersistent();
        }

        if (CONFIG.catsConfig.allBlackCats) {
            this.setVariant(Registries.CAT_VARIANT.get(CatVariant.ALL_BLACK));
        }
    }

    @Redirect(method = "initialize(Lnet/minecraft/world/ServerWorldAccess;Lnet/minecraft/world/LocalDifficulty;" +
                    "Lnet/minecraft/entity/SpawnReason;Lnet/minecraft/entity/EntityData;Lnet/minecraft/nbt/NbtCompound;)Lnet/minecraft/entity/EntityData;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/ServerWorldAccess;getMoonSize()F"))
    private float replaceMoonSize(ServerWorldAccess world) {
        if (CONFIG.catsConfig.blackCatsAtAnyTime) {
            return 1.0f;
        }

        return world.getMoonSize();
    }
}
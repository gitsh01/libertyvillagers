package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
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
    public abstract void setVariant(RegistryEntry<CatVariant> registryEntry);

    public CatEntityMixin(EntityType<? extends CatEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "initialize",
            at = @At("RETURN"))
    void addPersistantToInitialize(ServerWorldAccess world, LocalDifficulty difficulty,
                                   SpawnReason spawnReason, EntityData entityData,
                                   CallbackInfoReturnable<EntityData> cir) {
        if (CONFIG.catsConfig.villageCatsDontDespawn) {
            this.setPersistent();
        }

        if (CONFIG.catsConfig.allBlackCats) {
            Registries.CAT_VARIANT
                    .getEntry(CatVariant.ALL_BLACK.getValue())
                    .ifPresent(this::setVariant);
        }
    }

    @Redirect(method = "initialize",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/ServerWorldAccess;getMoonSize()F"))
    private float replaceMoonSize(ServerWorldAccess world) {
        if (CONFIG.catsConfig.blackCatsAtAnyTime) {
            return 1.0f;
        }

        return world.getMoonSize();
    }
}
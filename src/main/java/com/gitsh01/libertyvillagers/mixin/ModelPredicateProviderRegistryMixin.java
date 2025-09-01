package com.gitsh01.libertyvillagers.mixin;

/*
 * This mixin modifies the ModelPredicateProviderRegistry class to add custom model predicate behavior.
 * However, the ModelPredicateProviderRegistry class has been deprecated and removed from Fabric.
 *
 * This mixin is now obsolete and will not be executed as the target class no longer exists in the Fabric API.
 * The code below has been commented out, but it can be restored if the target class or a similar functionality
 * becomes available in future versions.
 *
 * Please remove this class completely once it's confirmed that there is no more need for it.
 */

// Commented out due to deprecation and removal of ModelPredicateProviderRegistry class
/*
import com.google.common.collect.Maps;
import net.minecraft.client.item.ClampedModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProvider;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(ModelPredicateProviderRegistry.class)
public class ModelPredicateProviderRegistryMixin {

    @Shadow
    @Mutable
    static Map<Item, Map<Identifier, ModelPredicateProvider>> ITEM_SPECIFIC;

    @Inject(method = "register(Lnet/minecraft/item/Item;Lnet/minecraft/util/Identifier;" +
            "Lnet/minecraft/client/item/ClampedModelPredicateProvider;)V",
            at = @At("HEAD"),
            cancellable = true)
    private static void register(Item item, Identifier id, ClampedModelPredicateProvider provider, CallbackInfo ci) {
        if (item != Items.FISHING_ROD) {
            return;
        }
        // Villagers should show the used graphic for the fishing rod.
        ClampedModelPredicateProvider newProvider = (stack, world, entity, seed) -> {
            if (entity != null && entity.getType() == EntityType.VILLAGER) {
                return 1.0f;
            }
            return provider.unclampedCall(stack, world, entity, seed);
        };
        ITEM_SPECIFIC.computeIfAbsent(item, key -> Maps.newHashMap()).put(id, newProvider);
        ci.cancel();
    }
}
*/


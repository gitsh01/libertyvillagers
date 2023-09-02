package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.inventory.Inventory;
import net.minecraft.screen.LecternScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LecternScreenHandler.class)
public interface LecternScreenHandlerAccessorMixin {
    @Accessor("inventory")
    Inventory getInventory();
}


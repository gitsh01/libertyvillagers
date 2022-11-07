package com.gitsh01.libertyvillagers;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.LecternScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import com.gitsh01.libertyvillagers.mixin.LecternScreenHandlerAccessorMixin;
import org.jetbrains.annotations.Nullable;

public class LecternScreenHandlerFactory implements NamedScreenHandlerFactory {
    private final ItemStack bookStack;

    public LecternScreenHandlerFactory(ItemStack bookStack) {
        this.bookStack = bookStack;
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("text.LibertyVillagers.villagerStats.title");
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        final LecternScreenHandler lecternScreenHandler = new LecternScreenHandler(i);
        ((LecternScreenHandlerAccessorMixin) lecternScreenHandler).getInventory().setStack(0, bookStack);
        return lecternScreenHandler;
    }
}


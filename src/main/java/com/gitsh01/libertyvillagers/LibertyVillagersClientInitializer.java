package com.gitsh01.libertyvillagers;

import com.gitsh01.libertyvillagers.overlay.LibertyVillagersOverlay;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.screen.ingame.BookScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Style;

import java.util.LinkedList;
import java.util.List;

public class LibertyVillagersClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LibertyVillagersOverlay.register();
        LibertyVillagersMod.setIsClient(true);
    }

    public static void openBookScreen(ItemStack bookStack) {
        BookScreen screen = new BookScreen(BookScreen.Contents.create(bookStack));

        RenderSystem.recordRenderCall(() -> MinecraftClient.getInstance().setScreen(screen));
    }

    public static List<String> wrapText(String string) {
        TextHandler textHandler = MinecraftClient.getInstance().textRenderer.getTextHandler();
        List<String> lines = new LinkedList<>();
        textHandler.wrapLines(string, 114, Style.EMPTY, true, (style, ix, jx) -> {
            String substring = string.substring(ix, jx);
            lines.add(substring);
        });
        return lines;
    }

}

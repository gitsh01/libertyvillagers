package com.gitsh01.libertyvillagers;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.LinkedList;
import java.util.List;

public class LibertyVillagersServerInitializer implements DedicatedServerModInitializer {
    private static final int LINE_LEN = 22;

    @Override
    public void onInitializeServer() {
    }

    public static void openBookScreen(ItemStack bookStack, PlayerEntity user) {
        user.openHandledScreen(new LecternScreenHandlerFactory(bookStack));
    }

    public static List<String> wrapText(String string) {
        List<String> lines = new LinkedList<>();
        StringBuilder lineBuilder = new StringBuilder();
        boolean skipNextCount = false;
        int curLen = 0;
        for (char c : string.toCharArray()) {
            lineBuilder.append(c);
            if (skipNextCount) skipNextCount = false;
            else {
                if (c == '§') skipNextCount = true;
                else {
                    if (c == '\n' || curLen == LINE_LEN) {
                        lines.add(lineBuilder.toString());
                        lineBuilder = new StringBuilder();
                        curLen = 0;
                    } else {
                        curLen++;
                    }
                }
            }
        }
        if (!lineBuilder.isEmpty()) {
            lines.add(lineBuilder.toString());
        }
        return lines;
    }
}

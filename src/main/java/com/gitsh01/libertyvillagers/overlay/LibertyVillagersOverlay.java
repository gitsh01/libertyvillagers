package com.gitsh01.libertyvillagers.overlay;

import com.gitsh01.libertyvillagers.cmds.VillagerInfo;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

public class LibertyVillagersOverlay {

    static float WIDTH_FROM_RIGHT_EDGE = 150;
    static int WHITE = 0xffffff;

    public static void HudRenderCallback(MatrixStack matrices, float tickDelta) {
        if (!CONFIG.debugConfig.enableVillagerInfoOverlay) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        float width = client.getWindow().getScaledWidth();
        float x = width = WIDTH_FROM_RIGHT_EDGE;
        TextRenderer renderer = client.textRenderer;
        float textHeight = renderer.getWrappedLinesHeight("Ij", 40);
        float y = 0;
        renderer.draw(matrices, Text.translatable("text.LibertyVillagers.libertyVillagersOverlay.title"), x, y, WHITE);

        HitResult hit = client.crosshairTarget;
        List<Text> lines = null;

        switch (hit.getType()) {
            case MISS:
                break;
            case BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                BlockState blockState = client.world.getBlockState(blockPos);
                lines = VillagerInfo.getBlockInfo(blockPos, blockState);
                break;
            case ENTITY:
                EntityHitResult entityHit = (EntityHitResult) hit;
                Entity entity = entityHit.getEntity();
                lines = VillagerInfo.getEntityInfo(entity);
                break;
        }

        if (lines != null) {
            y += textHeight;
            MultilineText multilineText = MultilineText.createFromTexts(renderer, lines);
            multilineText.draw(matrices, (int) x, (int) y, (int) textHeight, WHITE);
        }
    }
}

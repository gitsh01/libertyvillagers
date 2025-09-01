package com.gitsh01.libertyvillagers.overlay;

import com.gitsh01.libertyvillagers.cmds.VillagerInfo;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

public class LibertyVillagersOverlay implements HudRenderCallback {

    static int WHITE = 0xffffff;
    static int TEXT_PADDING = 2;
    static int BACKGROUND_PADDING = 2;
    static int BACKGROUND_COLOR = 0x55200000;

    public static void register() {
        HudRenderCallback.EVENT.register(new LibertyVillagersOverlay());
    }

    public void onHudRender(DrawContext context, RenderTickCounter tickDelta) {
        if (!CONFIG.debugConfig.enableVillagerInfoOverlay) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        HitResult hit = client.crosshairTarget;
        List<Text> lines = null;
        ServerWorld world = null;
        if (client.isIntegratedServerRunning()) {
            world = client.getServer().getWorld(client.world.getRegistryKey());
        }

        switch (hit.getType()) {
            case MISS:
                break;
            case BLOCK:
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                BlockState blockState = client.world.getBlockState(blockPos);
                lines = VillagerInfo.getBlockInfo(world, blockPos, blockState);
                break;
            case ENTITY:
                EntityHitResult entityHit = (EntityHitResult) hit;
                Entity entity = entityHit.getEntity();
                if (client.isIntegratedServerRunning()) {
                    entity = world.getEntity(entity.getUuid());
                }
                lines = VillagerInfo.getEntityInfo(world, entity);
                break;
        }

        if (lines != null) {
            TextRenderer renderer = client.textRenderer;
            MultilineText multilineText = MultilineText.create(renderer, lines.toArray(new Text[0]));

            int windowScaledWidth = client.getWindow().getScaledWidth();
            int multilineWidth = multilineText.getMaxWidth() + TEXT_PADDING;
            int x = windowScaledWidth - multilineWidth;
            int width = x + (multilineWidth / 2) - (BACKGROUND_PADDING / 2);

            int i = TEXT_PADDING;
            for (Text line : lines) {
                context.drawTextWithBackground(renderer, line, x, i, width, WHITE);

                i += renderer.fontHeight;
            }
        }
    }
}

package com.gitsh01.libertyvillagers.forge;

import com.gitsh01.libertyvillagers.overlay.LibertyVillagersOverlay;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.client.gui.OverlayRegistry;

public class LibertyVillagersClientInitializerForge {
    static IIngameOverlay HUD_DEBUG_TEXT_ELEMENT;

    static void register() {
        HUD_DEBUG_TEXT_ELEMENT =
                OverlayRegistry.registerOverlayTop("Debug Villagers Info", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
                    LibertyVillagersOverlay.HudRenderCallback(poseStack, partialTick);
                });
    }

}

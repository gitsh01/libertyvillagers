package com.gitsh01.libertyvillagers.forge;

import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import com.gitsh01.libertyvillagers.overlay.LibertyVillagersOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= LibertyVillagersMod.MOD_ID, value= Dist.CLIENT)
public class LibertyVillagersClientInitializerForge {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent event) {
        if (!(event instanceof RenderGuiOverlayEvent.Post)) {
            return;
        }
        LibertyVillagersOverlay.HudRenderCallback(event.getGuiGraphics(), event.getPartialTick());
    }

    public static void onInitializeClient() {
    }

}

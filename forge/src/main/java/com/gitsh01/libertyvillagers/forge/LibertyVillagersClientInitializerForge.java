package com.gitsh01.libertyvillagers.forge;

import com.gitsh01.libertyvillagers.LibertyVillagersMod;
import com.gitsh01.libertyvillagers.overlay.LibertyVillagersOverlay;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid= LibertyVillagersMod.MOD_ID, value= Dist.CLIENT)
public class LibertyVillagersClientInitializerForge {

    @SubscribeEvent
    public static void onRegisterOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("Debug Villagers Info", (gui, poseStack, partialTick, screenWidth, screenHeight) -> {
            LibertyVillagersOverlay.HudRenderCallback(poseStack, partialTick);
        });
    }

    public static void onInitializeClient() {
    }

}

package com.gitsh01.libertyvillagers.fabric;

import com.gitsh01.libertyvillagers.overlay.LibertyVillagersOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class LibertyVillagersClientInitializerFabric implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        HudRenderCallback.EVENT.register(LibertyVillagersOverlay::HudRenderCallback);
    }
}

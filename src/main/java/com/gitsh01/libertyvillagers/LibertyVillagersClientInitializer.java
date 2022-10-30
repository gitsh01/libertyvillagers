package com.gitsh01.libertyvillagers;

import com.gitsh01.libertyvillagers.overlay.LibertyVillagersOverlay;
import net.fabricmc.api.ClientModInitializer;

public class LibertyVillagersClientInitializer implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        System.out.println("OnInitializeClient");
        LibertyVillagersOverlay.register();
    }
}

package com.gitsh01.libertyvillagers.mixin;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerProfession.class)
public abstract class VillagerProfessionMixin {

    @Shadow
    private String id;

    @Inject(method = "secondaryJobSites",
            at = @At("HEAD"),
            cancellable = true)
    void replaceSecondaryJobSites(CallbackInfoReturnable<ImmutableSet<Block>> cir) {
        switch (id) {
            case "librarian" -> {
                if (CONFIG.villagersProfessionConfig.librariansLookAtBooks) {
                    cir.setReturnValue(ImmutableSet.of(Blocks.BOOKSHELF));
                    cir.cancel();
                }
            }
        }
    }
}

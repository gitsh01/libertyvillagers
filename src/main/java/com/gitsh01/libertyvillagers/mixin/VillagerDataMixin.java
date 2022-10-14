package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

@Mixin(VillagerData.class)
public abstract class VillagerDataMixin {

    @Accessor
    public abstract VillagerProfession getProfession();

    @Accessor("profession")
    public abstract void setProfession(VillagerProfession profession);

    @Inject(at = @At("RETURN"), method = "<init>()V")
    public void replaceNitwit(VillagerType type, VillagerProfession profession, int level, CallbackInfo ci) {
        if (CONFIG.noNitwitVillagers && this.getProfession() == VillagerProfession.NITWIT) {
            this.setProfession(VillagerProfession.NONE);
        }
    }
}

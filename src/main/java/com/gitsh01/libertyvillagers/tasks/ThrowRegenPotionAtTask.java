package com.gitsh01.libertyvillagers.tasks;

import com.google.common.collect.Lists;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.PotionEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

import java.util.List;

import static com.gitsh01.libertyvillagers.LibertyVillagersMod.CONFIG;

public class ThrowRegenPotionAtTask extends HealTargetTask {

    private static final int COMPLETION_RANGE = 5;

    public ThrowRegenPotionAtTask() {
        super(COMPLETION_RANGE);
    }

    protected List<LivingEntity> getPossiblePatients(ServerWorld serverWorld, VillagerEntity villagerEntity) {
        List<LivingEntity> possiblePatients = Lists.newArrayList();
        if (!CONFIG.villagersProfessionConfig.clericThrowsPotionsAtVillagers &&
                !CONFIG.villagersProfessionConfig.clericThrowsPotionsAtPlayers) {
            return possiblePatients;
        }

        if (CONFIG.villagersProfessionConfig.clericThrowsPotionsAtVillagers) {
            List<VillagerEntity> villagers = villagerEntity.getWorld().getNonSpectatingEntities(VillagerEntity.class,
                    villagerEntity.getBoundingBox()
                            .expand(CONFIG.villagersProfessionConfig.clericThrowsPotionsAtRange));
            possiblePatients.addAll(villagers);
        }

        if (CONFIG.villagersProfessionConfig.clericThrowsPotionsAtPlayers) {
            List<PlayerEntity> players = villagerEntity.getWorld().getNonSpectatingEntities(PlayerEntity.class,
                    villagerEntity.getBoundingBox()
                            .expand(CONFIG.villagersProfessionConfig.clericThrowsPotionsAtRange));
            possiblePatients.addAll(players);
        }

        return possiblePatients;
    }

    protected void healTarget(ServerWorld serverWorld, VillagerEntity villagerEntity, LivingEntity currentPatient) {
        Vec3d vec3d = currentPatient.getVelocity();
        double d = currentPatient.getX() + vec3d.x - villagerEntity.getX();
        double e = currentPatient.getEyeY() - (double) 1.1f - villagerEntity.getY();
        double f = currentPatient.getZ() + vec3d.z - villagerEntity.getZ();
        double g = Math.sqrt(d * d + f * f);

        Potion potion = Potions.REGENERATION;
        PotionEntity potionEntity = new PotionEntity(serverWorld, villagerEntity);
        potionEntity.setItem(PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), potion));
        potionEntity.setPitch(potionEntity.getPitch() + 20.0f);
        potionEntity.setVelocity(d, e + g * 0.2, f, 0.75f, 8.0f);
        serverWorld.playSound(null, villagerEntity.getX(), villagerEntity.getY(), villagerEntity.getZ(),
                SoundEvents.ENTITY_LINGERING_POTION_THROW, villagerEntity.getSoundCategory(), 1.0f,
                0.8f + serverWorld.getRandom().nextFloat() * 0.4f);
        serverWorld.spawnEntity(potionEntity);
    }
}

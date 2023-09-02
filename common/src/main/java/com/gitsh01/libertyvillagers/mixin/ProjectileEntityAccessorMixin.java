package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ProjectileEntity.class)
public interface ProjectileEntityAccessorMixin {

    @Invoker("getOwner")
    public Entity invokeGetOwner();

    @Invoker("updateRotation")
    public void invokeUpdateRotation();
}

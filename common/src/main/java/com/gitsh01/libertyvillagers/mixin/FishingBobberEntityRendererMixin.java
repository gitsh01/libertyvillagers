package com.gitsh01.libertyvillagers.mixin;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.FishingBobberEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public abstract class FishingBobberEntityRendererMixin extends EntityRenderer<FishingBobberEntity> {

    @Shadow
    private static RenderLayer LAYER;

    @Shadow
    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x,
                               int y, int u, int v) {}

    @Shadow
    private static float percentage(int value, int max) { return 0; }

    @Shadow
    private static void renderFishingLine(float x, float y, float z, VertexConsumer buffer,
                                          MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {}

    public FishingBobberEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "render",
            at = @At("HEAD"),
            cancellable = true)
    public void render(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        double s;
        float r;
        double q;
        double p;
        double o;
        if (fishingBobberEntity.getOwner() == null) {
            return;
        }
        if (fishingBobberEntity.getOwner().getType() != EntityType.VILLAGER) {
            return;
        }
        VillagerEntity villager = (VillagerEntity) fishingBobberEntity.getOwner();
        matrixStack.push();
        matrixStack.push();
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.multiply(this.dispatcher.getRotation());
        matrixStack.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        matrixStack.pop();
        float l = MathHelper.lerp(g, villager.prevBodyYaw, villager.bodyYaw) * ((float)Math.PI / 180);
        double d = MathHelper.sin(l);
        double e = MathHelper.cos(l);
        double n = 0.4;
        o = MathHelper.lerp(g, villager.prevX, villager.getX()) - d * n;
        p = villager.prevY + (double)villager.getStandingEyeHeight() + (villager.getY() - villager.prevY) * (double)g - 0.45;
        q = MathHelper.lerp(g, villager.prevZ, villager.getZ()) + e * n;

        s = MathHelper.lerp(g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
        double t = MathHelper.lerp(g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
        double u = MathHelper.lerp(g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
        float v = (float)(o - s);
        float w = (float)(p - t);
        float x = (float)(q - u);
        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLineStrip());
        MatrixStack.Entry entry2 = matrixStack.peek();
        int y = 16;
        for (int z = 0; z <= 16; ++z) {
            FishingBobberEntityRendererMixin.renderFishingLine(v, w, x, vertexConsumer2, entry2,
                    FishingBobberEntityRendererMixin.percentage(z, 16),
                    FishingBobberEntityRendererMixin.percentage(z + 1, 16));
        }
        matrixStack.pop();
        super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
        ci.cancel();
    }
}

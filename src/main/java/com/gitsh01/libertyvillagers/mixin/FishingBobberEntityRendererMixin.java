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
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FishingBobberEntityRenderer.class)
public abstract class FishingBobberEntityRendererMixin extends EntityRenderer<FishingBobberEntity> {

    @Shadow
    private static RenderLayer LAYER;

    public FishingBobberEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Shadow
    private static void vertex(VertexConsumer buffer, Matrix4f matrix, Matrix3f normalMatrix, int light, float x, int y,
                               int u, int v) {
    }

    @Shadow
    private static float percentage(int value, int max) {
        return 0;
    }

    private static void renderFishingLineAsLine(float x, float y, float z, VertexConsumer buffer,
                                                MatrixStack.Entry matrices, float segmentStart, float segmentEnd) {
        float f = x * segmentStart;
        float g = y * (segmentStart * segmentStart + segmentStart) * 0.5f + 0.25f;
        float h = z * segmentStart;
        float i = x * segmentEnd - f;
        float j = y * (segmentEnd * segmentEnd + segmentEnd) * 0.5f + 0.25f - g;
        float k = z * segmentEnd - h;
        float l = MathHelper.sqrt(i * i + j * j + k * k);
        buffer.vertex(matrices.getPositionMatrix(), f, g, h).color(0, 0, 0, 255)
                .normal(matrices.getNormalMatrix(), i /= l, j /= l, k /= l).next();
        // Switching from line strip to line, so add doubles of all the intermediate points. 0->1, 1->2, 2->3.
        if ((segmentStart != 0) && (segmentStart != 1.0f)) {
            buffer.vertex(matrices.getPositionMatrix(), f, g, h).color(0, 0, 0, 255)
                    .normal(matrices.getNormalMatrix(), i, j, k).next();
        }
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(FishingBobberEntity fishingBobberEntity, float f, float g, MatrixStack matrixStack,
                       VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
        double s;
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
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        MatrixStack.Entry entry = matrixStack.peek();
        Matrix4f matrix4f = entry.getPositionMatrix();
        Matrix3f matrix3f = entry.getNormalMatrix();
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(LAYER);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 0, 0, 1);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 0, 1, 1);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 1.0f, 1, 1, 0);
        FishingBobberEntityRendererMixin.vertex(vertexConsumer, matrix4f, matrix3f, i, 0.0f, 1, 0, 0);
        matrixStack.pop();
        float l = MathHelper.lerp(g, villager.prevBodyYaw, villager.bodyYaw) * ((float) Math.PI / 180);
        double d = MathHelper.sin(l);
        double e = MathHelper.cos(l);
        double n = 0.4;
        o = MathHelper.lerp(g, villager.prevX, villager.getX()) - d * n;
        p = villager.prevY + (double) villager.getStandingEyeHeight() +
                (villager.getY() - villager.prevY) * (double) g - 0.45;
        q = MathHelper.lerp(g, villager.prevZ, villager.getZ()) + e * n;

        s = MathHelper.lerp(g, fishingBobberEntity.prevX, fishingBobberEntity.getX());
        double t = MathHelper.lerp(g, fishingBobberEntity.prevY, fishingBobberEntity.getY()) + 0.25;
        double u = MathHelper.lerp(g, fishingBobberEntity.prevZ, fishingBobberEntity.getZ());
        float v = (float) (o - s);
        float w = (float) (p - t);
        float x = (float) (q - u);
        VertexConsumer vertexConsumer2 = vertexConsumerProvider.getBuffer(RenderLayer.getLines());
        MatrixStack.Entry entry2 = matrixStack.peek();
        for (int z = 0; z <= 16; ++z) {
            // There's a bug in 1.19.3 which causes the line strips to not properly end when the layer is
            // switched, which means a line is drawn from the end of one fishing line to the next fishing line.
            FishingBobberEntityRendererMixin.renderFishingLineAsLine(v, w, x, vertexConsumer2, entry2,
                    FishingBobberEntityRendererMixin.percentage(z, 16),
                    FishingBobberEntityRendererMixin.percentage(z + 1, 16));
        }
        matrixStack.pop();
        super.render(fishingBobberEntity, f, g, matrixStack, vertexConsumerProvider, i);
        ci.cancel();
    }
}

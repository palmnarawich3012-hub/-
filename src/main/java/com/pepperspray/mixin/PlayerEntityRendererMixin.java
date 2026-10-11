package com.pepperspray.mixin;

import com.pepperspray.client.HeldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** After the first-person arm is drawn, attach the item to the hand exactly like the third-person layer does. */
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

    @Inject(method = "renderArm", at = @At("TAIL"))
    private void pepperspray$attachCan(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                       AbstractClientPlayerEntity player, ModelPart arm, ModelPart sleeve,
                                       CallbackInfo ci) {
        ItemStack stack = HeldRenderContext.stack;
        if (stack.isEmpty()) return;

        PlayerEntityModel<AbstractClientPlayerEntity> model = ((PlayerEntityRenderer) (Object) this).getModel();
        boolean left = arm == model.leftArm;

        matrices.push();
        model.setArmAngle(left ? Arm.LEFT : Arm.RIGHT, matrices);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0f));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f));
        matrices.translate((left ? -1 : 1) / 16.0f, 0.125f, -0.625f);
        MinecraftClient client = MinecraftClient.getInstance();
        client.getItemRenderer().renderItem(player, stack,
                left ? ModelTransformationMode.FIRST_PERSON_LEFT_HAND : ModelTransformationMode.FIRST_PERSON_RIGHT_HAND,
                left, matrices, vertexConsumers, client.world, light, OverlayTexture.DEFAULT_UV, player.getId());
        matrices.pop();
    }
}

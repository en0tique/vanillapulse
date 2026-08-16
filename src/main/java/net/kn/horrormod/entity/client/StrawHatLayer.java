package net.kn.horrormod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.kn.horrormod.item.StrawHatItem;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class StrawHatLayer extends RenderLayer<Player, PlayerModel<Player>> {

    private static final ResourceLocation HAT_TEXTURE =
            new ResourceLocation(
                    "horrormod",
                    "textures/entity/straw_hat.png"
            );

    private final StrawHatModel hatModel;

    public StrawHatLayer(
            RenderLayerParent<Player, PlayerModel<Player>> parent,
            StrawHatModel hatModel
    ) {
        super(parent);
        this.hatModel = hatModel;
    }

    @Override
    public void render(
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight,
            Player player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {

        ItemStack stack = player.getItemBySlot(EquipmentSlot.HEAD);

        if (stack.isEmpty()
                || !(stack.getItem() instanceof StrawHatItem)) {
            return;
        }
        poseStack.pushPose();

        this.getParentModel().head.translateAndRotate(poseStack);

        poseStack.translate(0.0F, -0.25F, 0.0F);
        poseStack.scale(1.2F, 1.2F, 1.2F);

        hatModel.setupAnim(
                player,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        VertexConsumer vertexConsumer =
                buffer.getBuffer(
                        RenderType.entityCutoutNoCull(HAT_TEXTURE)
                );

        hatModel.renderToBuffer(
                poseStack,
                vertexConsumer,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );

        poseStack.popPose();
    }
}
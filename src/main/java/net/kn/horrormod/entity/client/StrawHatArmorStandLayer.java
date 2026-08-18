package net.kn.horrormod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.kn.horrormod.item.StrawHatItem;

import net.minecraft.client.model.ArmorStandModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;

public class StrawHatArmorStandLayer
        extends RenderLayer<ArmorStand, ArmorStandModel> {

    private static final ResourceLocation HAT_TEXTURE =
            new ResourceLocation(
                    "horrormod",
                    "textures/entity/straw_hat.png"
            );

    private final StrawHatModel hatModel;

    public StrawHatArmorStandLayer(
            RenderLayerParent<ArmorStand, ArmorStandModel> parent,
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
            ArmorStand armorStand,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {

        ItemStack stack =
                armorStand.getItemBySlot(EquipmentSlot.HEAD);

        if (stack.isEmpty()
                || !(stack.getItem() instanceof StrawHatItem)) {
            return;
        }

        poseStack.pushPose();

        /*
         * Голова Armor Stand
         */
        this.getParentModel()
                .head
                .translateAndRotate(poseStack);

        /*
         * Ті самі налаштування,
         * що використовуються на гравцеві.
         */
        poseStack.translate(
                0.0F,
                -0.25F,
                0.0F
        );

        poseStack.scale(
                1.2F,
                1.2F,
                1.2F
        );

        /*
         * setupAnim НЕ викликаємо.
         *
         * У StrawHatModel він порожній,
         * тому це взагалі не потрібно.
         */

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
package net.kn.horrormod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
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

public class StrawHatLayer
        extends RenderLayer<Player, PlayerModel<Player>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(
                    "horrormod",
                    "textures/entity/straw_hat.png"
            );

    private final StrawHatModel<Player> hatModel;

    public StrawHatLayer(
            RenderLayerParent<Player, PlayerModel<Player>> parent,
            StrawHatModel<Player> hatModel
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

        ItemStack stack =
                player.getItemBySlot(EquipmentSlot.HEAD);

        if (!(stack.getItem() instanceof StrawHatItem)) {
            return;
        }

        poseStack.pushPose();

        /*
         * Прикріплюємо весь капелюх до голови гравця.
         */
        this.getParentModel().head.translateAndRotate(poseStack);

        /*
         * Player head має стандартну Minecraft-модель,
         * а наша модель побудована в координатах Blockbench
         * навколо [8,8,8].
         *
         * Перший тест — без додаткового зміщення.
         */
        hatModel.renderToBuffer(
                poseStack,
                buffer.getBuffer(
                        RenderType.entityCutoutNoCull(TEXTURE)
                ),
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
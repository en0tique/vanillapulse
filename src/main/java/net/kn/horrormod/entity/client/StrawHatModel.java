package net.kn.horrormod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.player.Player;

public class StrawHatModel extends EntityModel<Player> {

    private final ModelPart root;
    private final ModelPart head;

    public StrawHatModel(ModelPart root) {
        this.root = root.getChild("root");
        this.head = this.root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {

        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        /*
         * ROOT
         *
         * Позиція взята безпосередньо з Blockbench.
         * Ти поставив модель на Y = 24.
         */
        PartDefinition root =
                partdefinition.addOrReplaceChild(
                        "root",
                        CubeListBuilder.create(),
                        PartPose.offset(
                                0.0F,
                                -1.0F,
                                0.0F
                        )
                );

        /*
         * HEAD
         */
        PartDefinition head =
                root.addOrReplaceChild(
                        "head",
                        CubeListBuilder.create(),
                        PartPose.offset(
                                0.0F,
                                0.0F,
                                0.0F
                        )
                );

        /*
         * BAND
         */
        head.addOrReplaceChild(
                "band_r1",
                CubeListBuilder.create()
                        .texOffs(28, 26)
                        .addBox(
                                -12.0F,
                                -9.0F,
                                3.0F,
                                9.0F,
                                1.0F,
                                9.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        7.375F,
                        7.9F,
                        -7.3F,
                        0.0F,
                        0.0436F,
                        0.0F
                )
        );

        /*
         * CROWN
         */
        head.addOrReplaceChild(
                "crown_r1",
                CubeListBuilder.create()
                        .texOffs(32, 37)
                        .addBox(
                                -12.0F,
                                -13.0F,
                                4.0F,
                                8.0F,
                                5.0F,
                                8.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        8.0F,
                        8.0F,
                        -8.0F,
                        -0.0175F,
                        0.0349F,
                        0.0F
                )
        );

        /*
         * BRIM
         */
        head.addOrReplaceChild(
                "brim_r1",
                CubeListBuilder.create()
                        .texOffs(12, 50)
                        .addBox(
                                -14.5F,
                                -5.0F,
                                1.5F,
                                13.0F,
                                1.0F,
                                13.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        8.0F,
                        5.0F,
                        -8.0F,
                        0.0F,
                        0.0F,
                        0.0524F
                )
        );

        /*
         * UV у Blockbench = 64×64.
         *
         * PNG може фізично бути 128×128.
         * Це не міняємо.
         */
        return LayerDefinition.create(
                meshdefinition,
                64,
                64
        );
    }

    @Override
    public void setupAnim(
            Player player,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        /*
         * Нічого тут не крутимо.
         *
         * Поворот голови вже передається через
         * PlayerModel.head у StrawHatLayer.
         */
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {

        root.render(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,
                red,
                green,
                blue,
                alpha
        );
    }
}
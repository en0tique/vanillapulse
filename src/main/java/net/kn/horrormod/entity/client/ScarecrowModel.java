package net.kn.horrormod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kn.horrormod.entity.ScarecrowEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class ScarecrowModel<T extends Entity> extends HierarchicalModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation("horrormod", "scarecrow"), "main");

    private final ModelPart root;
    private final ModelPart post;
    private final ModelPart tatters_1;
    private final ModelPart tatters_2;
    private final ModelPart tatters_3;
    private final ModelPart body;
    private final ModelPart crossbeam;
    private final ModelPart head;
    private final ModelPart hat_brim;
    private final ModelPart hat_top;

    public ScarecrowModel(ModelPart root) {
        this.root = root;
        this.post = root.getChild("post");
        this.tatters_1 = root.getChild("tatters_1");
        this.tatters_2 = root.getChild("tatters_2");
        this.tatters_3 = root.getChild("tatters_3");
        this.body = root.getChild("body");
        this.crossbeam = root.getChild("crossbeam");
        this.head = root.getChild("head");
        this.hat_brim = root.getChild("hat_brim");
        this.hat_top = root.getChild("hat_top");
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("post", CubeListBuilder.create().texOffs(36, 37)
                        .addBox(-1.0F, -8.0F, -1.0F, 2.0F, 8.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        partdefinition.addOrReplaceChild("tatters_1", CubeListBuilder.create().texOffs(0, 27)
                        .addBox(1.0F, 0.0F, -2.0F, 2.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 4.0F, 0.0F));

        partdefinition.addOrReplaceChild("tatters_2", CubeListBuilder.create().texOffs(12, 27)
                        .addBox(-1.0F, 0.0F, -2.0F, 2.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 4.0F, 0.0F));

        partdefinition.addOrReplaceChild("tatters_3", CubeListBuilder.create().texOffs(24, 37)
                        .addBox(-3.0F, 1.0F, -2.0F, 2.0F, 11.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 4.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(24, 25)
                        .addBox(-3.0F, -28.0F, -2.0F, 6.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));

        partdefinition.addOrReplaceChild("crossbeam", CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-14.0F, -1.0F, -1.0F, 28.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(),
                PartPose.offset(0.0F, -4.0F, 0.0F));
        head.addOrReplaceChild("head_r1", CubeListBuilder.create().texOffs(0, 15)
                        .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition hat_brim = partdefinition.addOrReplaceChild("hat_brim", CubeListBuilder.create(),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        hat_brim.addOrReplaceChild("hat_brim_r1", CubeListBuilder.create().texOffs(0, 4)
                        .addBox(-5.0F, -7.0F, -5.0F, 10.0F, 1.0F, 10.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, -28.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        PartDefinition hat_top = partdefinition.addOrReplaceChild("hat_top", CubeListBuilder.create(),
                PartPose.offset(0.0F, -5.0F, 0.0F));
        hat_top.addOrReplaceChild("hat_top_r1", CubeListBuilder.create().texOffs(24, 15)
                        .addBox(-3.0F, -11.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float yawRad = netHeadYaw * ((float) Math.PI / 180F);
        float pitchRad = headPitch * ((float) Math.PI / 180F);

        this.head.yRot = yawRad;
        this.head.xRot = pitchRad;
        this.hat_brim.yRot = yawRad;
        this.hat_top.yRot = yawRad;

        if (entity instanceof ScarecrowEntity scarecrow) {
            this.animate(scarecrow.idleAnimationState, ScarecrowAnimation.IDLE, ageInTicks);
            this.animate(scarecrow.watchTriggerAnimationState, ScarecrowAnimation.WATCH_TRIGGER, ageInTicks);
        }
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        post.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tatters_1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tatters_2.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        tatters_3.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        crossbeam.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        hat_brim.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        hat_top.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}
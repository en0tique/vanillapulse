package net.kn.horrormod.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.EntityModel;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class StrawHatModel<T extends LivingEntity> extends EntityModel<T> {

    private static final float TEX_WIDTH = 64.0F;
    private static final float TEX_HEIGHT = 64.0F;

    @Override
    public void setupAnim(
            T entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
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

        poseStack.pushPose();

        /*
         * ---------------------------------------------------------
         * HEAD
         * ---------------------------------------------------------
         *
         * Blockbench group:
         *
         * origin = [8, 8, 8]
         *
         * Тому всі координати моделі переводимо відносно [8,8,8].
         */

        poseStack.translate(
                0.0F,
                0.0F,
                0.0F
        );

        /*
         * ---------------------------------------------------------
         * BRIM
         * ---------------------------------------------------------
         *
         * from [1.5, 7, 1.5]
         * to   [14.5, 8, 14.5]
         *
         * rotation Z = 3°
         * origin = [0, 3, 0]
         */

        poseStack.pushPose();

        // Blockbench rotation origin [0,3,0]
        poseStack.translate(
                -8.0F / 16.0F,
                -5.0F / 16.0F,
                -8.0F / 16.0F
        );

        poseStack.translate(
                0.0F,
                3.0F / 16.0F,
                0.0F
        );

        poseStack.mulPose(
                Axis.ZP.rotationDegrees(3.0F)
        );

        poseStack.translate(
                0.0F,
                -3.0F / 16.0F,
                0.0F
        );

        renderCube(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,
                1.5F,
                7.0F,
                1.5F,
                14.5F,
                8.0F,
                14.5F,

                // north
                new float[]{5.5F, 4.25F, 8.75F, 4.5F},

                // east
                new float[]{2.0F, 7.0F, 5.25F, 7.25F},

                // south
                new float[]{5.25F, 7.0F, 8.5F, 7.25F},

                // west
                new float[]{2.0F, 7.25F, 5.25F, 7.5F},

                // up
                new float[]{3.25F, 3.25F, 0.0F, 0.0F},

                // down
                new float[]{3.25F, 3.25F, 0.0F, 6.5F}
        );

        poseStack.popPose();

        /*
         * ---------------------------------------------------------
         * CROWN
         * ---------------------------------------------------------
         *
         * from [4,8,4]
         * to   [12,13,12]
         *
         * X = 1°
         * Y = -2°
         * origin = [0,0,0]
         */

        poseStack.pushPose();

        poseStack.translate(
                -8.0F / 16.0F,
                -8.0F / 16.0F,
                -8.0F / 16.0F
        );

        poseStack.mulPose(
                Axis.XP.rotationDegrees(1.0F)
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(-2.0F)
        );

        renderCube(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,
                4.0F,
                8.0F,
                4.0F,
                12.0F,
                13.0F,
                12.0F,

                // north
                new float[]{5.5F, 0.0F, 7.5F, 1.25F},

                // east
                new float[]{5.5F, 1.25F, 7.5F, 2.5F},

                // south
                new float[]{5.5F, 2.5F, 7.5F, 3.75F},

                // west
                new float[]{0.0F, 6.5F, 2.0F, 7.75F},

                // up
                new float[]{5.25F, 6.5F, 3.25F, 4.5F},

                // down
                new float[]{7.25F, 4.5F, 5.25F, 6.5F}
        );

        poseStack.popPose();

        /*
         * ---------------------------------------------------------
         * BAND
         * ---------------------------------------------------------
         *
         * from [3.625,8,3.7]
         * to   [12.375,9.25,12.45]
         *
         * Y = -2.5°
         * origin = [0.625,0,0.7]
         */

        poseStack.pushPose();

        poseStack.translate(
                -8.0F / 16.0F,
                -8.0F / 16.0F,
                -8.0F / 16.0F
        );

        poseStack.translate(
                0.625F / 16.0F,
                0.0F,
                0.7F / 16.0F
        );

        poseStack.mulPose(
                Axis.YP.rotationDegrees(-2.5F)
        );

        poseStack.translate(
                -0.625F / 16.0F,
                0.0F,
                -0.7F / 16.0F
        );

        renderCube(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,
                3.625F,
                8.0F,
                3.7F,
                12.375F,
                9.25F,
                12.45F,

                // north
                new float[]{5.5F, 3.75F, 7.75F, 4.125F},

                // east
                new float[]{2.0F, 6.5F, 4.25F, 6.875F},

                // south
                new float[]{4.25F, 6.5F, 6.5F, 6.875F},

                // west
                new float[]{6.5F, 6.5F, 8.75F, 6.875F},

                // up
                new float[]{5.5F, 2.25F, 3.25F, 0.0F},

                // down
                new float[]{5.5F, 2.25F, 3.25F, 4.5F}
        );

        poseStack.popPose();

        poseStack.popPose();
    }

    private void renderCube(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,

            float x1,
            float y1,
            float z1,

            float x2,
            float y2,
            float z2,

            float[] northUV,
            float[] eastUV,
            float[] southUV,
            float[] westUV,
            float[] upUV,
            float[] downUV
    ) {

        /*
         * Minecraft model coordinates:
         *
         * Blockbench 0..16
         * ->
         * Minecraft model -0.5..0.5
         */

        float minX = (x1 - 8.0F) / 16.0F;
        float minY = (y1 - 8.0F) / 16.0F;
        float minZ = (z1 - 8.0F) / 16.0F;

        float maxX = (x2 - 8.0F) / 16.0F;
        float maxY = (y2 - 8.0F) / 16.0F;
        float maxZ = (z2 - 8.0F) / 16.0F;

        /*
         * NORTH
         */
        renderFace(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,

                minX, minY, minZ,
                maxX, minY, minZ,
                maxX, maxY, minZ,
                minX, maxY, minZ,

                northUV,
                0.0F,
                0.0F,
                -1.0F
        );

        /*
         * EAST
         */
        renderFace(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,

                maxX, minY, minZ,
                maxX, minY, maxZ,
                maxX, maxY, maxZ,
                maxX, maxY, minZ,

                eastUV,
                1.0F,
                0.0F,
                0.0F
        );

        /*
         * SOUTH
         */
        renderFace(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,

                maxX, minY, maxZ,
                minX, minY, maxZ,
                minX, maxY, maxZ,
                maxX, maxY, maxZ,

                southUV,
                0.0F,
                0.0F,
                1.0F
        );

        /*
         * WEST
         */
        renderFace(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,

                minX, minY, maxZ,
                minX, minY, minZ,
                minX, maxY, minZ,
                minX, maxY, maxZ,

                westUV,
                -1.0F,
                0.0F,
                0.0F
        );

        /*
         * UP
         */
        renderFace(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,

                minX, maxY, minZ,
                maxX, maxY, minZ,
                maxX, maxY, maxZ,
                minX, maxY, maxZ,

                upUV,
                0.0F,
                1.0F,
                0.0F
        );

        /*
         * DOWN
         */
        renderFace(
                poseStack,
                vertexConsumer,
                packedLight,
                packedOverlay,

                minX, minY, maxZ,
                maxX, minY, maxZ,
                maxX, minY, minZ,
                minX, minY, minZ,

                downUV,
                0.0F,
                -1.0F,
                0.0F
        );
    }

    private void renderFace(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,

            float x1,
            float y1,
            float z1,

            float x2,
            float y2,
            float z2,

            float x3,
            float y3,
            float z3,

            float x4,
            float y4,
            float z4,

            float[] uv,

            float nx,
            float ny,
            float nz
    ) {

        PoseStack.Pose pose = poseStack.last();

        Matrix4f matrix = pose.pose();
        Matrix3f normalMatrix = pose.normal();

        float u1 = uv[0] / TEX_WIDTH;
        float v1 = uv[1] / TEX_HEIGHT;
        float u2 = uv[2] / TEX_WIDTH;
        float v2 = uv[3] / TEX_HEIGHT;

        vertex(
                vertexConsumer,
                matrix,
                normalMatrix,
                x1, y1, z1,
                u1, v1,
                nx, ny, nz,
                packedLight,
                packedOverlay
        );

        vertex(
                vertexConsumer,
                matrix,
                normalMatrix,
                x2, y2, z2,
                u2, v1,
                nx, ny, nz,
                packedLight,
                packedOverlay
        );

        vertex(
                vertexConsumer,
                matrix,
                normalMatrix,
                x3, y3, z3,
                u2, v2,
                nx, ny, nz,
                packedLight,
                packedOverlay
        );

        vertex(
                vertexConsumer,
                matrix,
                normalMatrix,
                x4, y4, z4,
                u1, v2,
                nx, ny, nz,
                packedLight,
                packedOverlay
        );
    }

    private void vertex(
            VertexConsumer consumer,
            Matrix4f matrix,
            Matrix3f normalMatrix,

            float x,
            float y,
            float z,

            float u,
            float v,

            float nx,
            float ny,
            float nz,

            int packedLight,
            int packedOverlay
    ) {

        consumer.vertex(matrix, x, y, z)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(u, v)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(normalMatrix, nx, ny, nz)
                .endVertex();
    }
}
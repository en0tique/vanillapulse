package net.kn.horrormod.entity.client;

import net.kn.horrormod.entity.ScarecrowEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ScarecrowRenderer extends MobRenderer<ScarecrowEntity, ScarecrowModel<ScarecrowEntity>> {

    private static final ResourceLocation TEXTURE_NORMAL =
            new ResourceLocation("horrormod", "textures/entity/scarecrow_normal.png");
    private static final ResourceLocation TEXTURE_ANGRY =
            new ResourceLocation("horrormod", "textures/entity/scarecrow_angry.png");

    public ScarecrowRenderer(EntityRendererProvider.Context context) {
        super(context, new ScarecrowModel<>(context.bakeLayer(ScarecrowModel.LAYER_LOCATION)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(ScarecrowEntity entity) {
        long days = entity.level().getDayTime() / 24000L;
        return days >= 20 ? TEXTURE_ANGRY : TEXTURE_NORMAL;
    }
}
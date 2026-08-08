package net.kn.horrormod.entity.client;

import net.kn.horrormod.entity.StalkerEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class StalkerRenderer extends MobRenderer<StalkerEntity, StalkerModel<StalkerEntity>> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("horrormod", "textures/entity/stalker.png");
    public StalkerRenderer(EntityRendererProvider.Context context){
        super(context, new StalkerModel<>(context.bakeLayer(StalkerModel.LAYER_LOCATION)), 0.5f);
    }
    @Override
    public ResourceLocation getTextureLocation(StalkerEntity entity){
        return TEXTURE;
    }

}

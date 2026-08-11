 package net.kn.horrormod.entity.client;

import net.kn.horrormod.entity.DeadHorseEntity;
import net.minecraft.client.model.HorseModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DeadHorseRenderer
        extends MobRenderer<DeadHorseEntity, HorseModel<DeadHorseEntity>> {

    public DeadHorseRenderer(EntityRendererProvider.Context context) {

        super(
                context,
                new HorseModel<>(
                        context.bakeLayer(ModelLayers.HORSE)
                ),
                0.75F
        );
    }

    @Override
    public ResourceLocation getTextureLocation(DeadHorseEntity entity) {

        return new ResourceLocation(
                "horrormod",
                "textures/entity/horse_zombie2.png"
        );
    }
}


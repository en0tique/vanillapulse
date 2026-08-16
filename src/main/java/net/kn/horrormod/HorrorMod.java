package net.kn.horrormod;

import com.mojang.logging.LogUtils;

import net.kn.horrormod.block.ModBlocks;
import net.kn.horrormod.entity.DeadHorseEntity;
import net.kn.horrormod.entity.ModEntity;
import net.kn.horrormod.entity.ScarecrowEntity;
import net.kn.horrormod.entity.StalkerEntity;

import net.kn.horrormod.entity.client.DeadHorseRenderer;
import net.kn.horrormod.entity.client.StalkerModel;
import net.kn.horrormod.entity.client.StalkerRenderer;
import net.kn.horrormod.entity.client.StrawHatLayer;
import net.kn.horrormod.entity.client.StrawHatModel;

import net.kn.horrormod.item.ModItems;
import net.kn.horrormod.network.HorrorNetwork;
import net.kn.horrormod.sound.ModSounds;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(HorrorMod.MOD_ID)
public class HorrorMod {

    public static final String MOD_ID = "horrormod";

    private static final Logger LOGGER =
            LogUtils.getLogger();

    /*
     * ModelLayer для капелюха.
     */
    public static final ModelLayerLocation STRAW_HAT_LAYER =
            new ModelLayerLocation(
                    new ResourceLocation(
                            MOD_ID,
                            "straw_hat"
                    ),
                    "main"
            );

    public HorrorMod(
            FMLJavaModLoadingContext context
    ) {

        IEventBus modEventBus =
                context.getModEventBus();

        ModItems.ITEMS.register(
                modEventBus
        );

        ModBlocks.BLOCKS.register(
                modEventBus
        );

        ModSounds.SOUNDS.register(
                modEventBus
        );

        ModEntity.ENTITY_TYPES.register(
                modEventBus
        );

        modEventBus.addListener(
                this::registerAttributes
        );

        modEventBus.addListener(
                this::commonSetup
        );

        MinecraftForge.EVENT_BUS.register(
                this
        );
    }

    private void registerAttributes(
            final EntityAttributeCreationEvent event
    ) {

        event.put(
                ModEntity.STALKER.get(),
                StalkerEntity
                        .createAttributes()
                        .build()
        );

        event.put(
                ModEntity.DEAD_HORSE.get(),
                DeadHorseEntity
                        .createAttributes()
                        .build()
        );

        event.put(
                ModEntity.SCARECROW.get(),
                ScarecrowEntity
                        .createAttributes()
                        .build()
        );
    }

    private void commonSetup(
            final FMLCommonSetupEvent event
    ) {

        HorrorNetwork.register();
    }

    @Mod.EventBusSubscriber(
            modid = MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientModEvents {

        /*
         * ENTITY RENDERERS
         */
        @SubscribeEvent
        public static void onRegisterEntityRenderers(
                EntityRenderersEvent.RegisterRenderers event
        ) {

            event.registerEntityRenderer(
                    ModEntity.STALKER.get(),
                    StalkerRenderer::new
            );

            event.registerEntityRenderer(
                    ModEntity.DEAD_HORSE.get(),
                    DeadHorseRenderer::new
            );

            event.registerEntityRenderer(
                    ModEntity.SCARECROW.get(),
                    context ->
                            new HumanoidMobRenderer<>(
                                    context,
                                    new PlayerModel<>(
                                            context.bakeLayer(
                                                    ModelLayers.PLAYER
                                            ),
                                            false
                                    ),
                                    0.5F
                            ) {

                                @Override
                                public ResourceLocation getTextureLocation(
                                        ScarecrowEntity entity
                                ) {

                                    return new ResourceLocation(
                                            "minecraft",
                                            "textures/entity/zombie/zombie.png"
                                    );
                                }
                            }
            );
        }

        /*
         * MODEL LAYERS
         */
        @SubscribeEvent
        public static void onRegisterLayers(
                EntityRenderersEvent.RegisterLayerDefinitions event
        ) {

            /*
             * Stalker
             */
            event.registerLayerDefinition(
                    StalkerModel.LAYER_LOCATION,
                    StalkerModel::createBodyLayer
            );

            /*
             * Straw Hat
             */
            event.registerLayerDefinition(
                    STRAW_HAT_LAYER,
                    StrawHatModel::createBodyLayer
            );
        }

        /*
         * PLAYER LAYERS
         */
        @SubscribeEvent
        public static void onAddLayers(
                EntityRenderersEvent.AddLayers event
        ) {

            addHatLayer(
                    event,
                    "default"
            );

            addHatLayer(
                    event,
                    "slim"
            );
        }

        private static void addHatLayer(
                EntityRenderersEvent.AddLayers event,
                String skin
        ) {

            var renderer =
                    event.getSkin(skin);

            if (renderer == null) {
                return;
            }

            /*
             * getSkin() має wildcard generic.
             *
             * Контрольований cast.
             */
            @SuppressWarnings("unchecked")
            LivingEntityRenderer<
                    Player,
                    PlayerModel<Player>
                    > playerRenderer =
                    (LivingEntityRenderer<
                            Player,
                            PlayerModel<Player>
                            >) (Object) renderer;

            /*
             * Створюємо StrawHatModel
             * із ModelLayer.
             */
            StrawHatModel hatModel =
                    new StrawHatModel(
                            Minecraft
                                    .getInstance()
                                    .getEntityModels()
                                    .bakeLayer(
                                            STRAW_HAT_LAYER
                                    )
                    );

            /*
             * Додаємо капелюх.
             */
            playerRenderer.addLayer(
                    new StrawHatLayer(
                            playerRenderer,
                            hatModel
                    )
            );
        }

        /*
         * CLIENT SETUP
         */
        @SubscribeEvent
        public static void onClientSetup(
                FMLClientSetupEvent event
        ) {
        }
    }
}
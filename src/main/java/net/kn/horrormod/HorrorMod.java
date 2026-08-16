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

import net.minecraft.client.model.PlayerModel;
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

    private static final Logger LOGGER = LogUtils.getLogger();

    public HorrorMod(FMLJavaModLoadingContext context) {

        IEventBus modEventBus = context.getModEventBus();

        // Предмети
        ModItems.ITEMS.register(modEventBus);

        // Блоки
        ModBlocks.BLOCKS.register(modEventBus);

        // Звуки
        ModSounds.SOUNDS.register(modEventBus);

        // Entity
        ModEntity.ENTITY_TYPES.register(modEventBus);

        // Атрибути Entity
        modEventBus.addListener(this::registerAttributes);

        // Common setup
        modEventBus.addListener(this::commonSetup);

        // Forge event bus
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void registerAttributes(
            final EntityAttributeCreationEvent event
    ) {

        event.put(
                ModEntity.STALKER.get(),
                StalkerEntity.createAttributes().build()
        );

        event.put(
                ModEntity.DEAD_HORSE.get(),
                DeadHorseEntity.createAttributes().build()
        );

        event.put(
                ModEntity.SCARECROW.get(),
                ScarecrowEntity.createAttributes().build()
        );
    }

    private void commonSetup(
            final FMLCommonSetupEvent event
    ) {
        HorrorNetwork.register();
    }

    // =========================================================
    // CLIENT
    // =========================================================

    @Mod.EventBusSubscriber(
            modid = MOD_ID,
            bus = Mod.EventBusSubscriber.Bus.MOD,
            value = Dist.CLIENT
    )
    public static class ClientModEvents {

        // =====================================================
        // ENTITY RENDERERS
        // =====================================================

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
                                            context.bakeLayer(ModelLayers.PLAYER),
                                            false
                                    ),
                                    0.5f
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

        // =====================================================
        // MODEL LAYERS
        // =====================================================

        @SubscribeEvent
        public static void onRegisterLayers(
                EntityRenderersEvent.RegisterLayerDefinitions event
        ) {

            /*
             * Тут реєструємо ТІЛЬКИ StalkerModel.
             *
             * StrawHatModel сюди НЕ додаємо.
             *
             * Він більше не використовує ModelLayerDefinition /
             * ModelPart / bakeLayer().
             */

            event.registerLayerDefinition(
                    StalkerModel.LAYER_LOCATION,
                    StalkerModel::createBodyLayer
            );
        }

        // =====================================================
        // PLAYER LAYERS
        // =====================================================

        @SubscribeEvent
        public static void onAddLayers(
                EntityRenderersEvent.AddLayers event
        ) {

            // Steve
            addHatLayer(event, "default");

            // Alex
            addHatLayer(event, "slim");
        }

        private static void addHatLayer(
                EntityRenderersEvent.AddLayers event,
                String skin
        ) {

            var renderer = event.getSkin(skin);

            if (renderer == null) {
                return;
            }

            /*
             * getSkin() має wildcard generic.
             *
             * Тут робимо контрольований cast до renderer
             * гравця.
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
             * Наша StrawHatModel більше НЕ використовує
             * ModelPart.
             *
             * Тому ніякого bakeLayer() тут немає.
             */
            StrawHatModel<Player> hatModel =
                    new StrawHatModel<>();

            /*
             * Додаємо капелюх до renderer гравця.
             */
            playerRenderer.addLayer(
                    new StrawHatLayer(
                            playerRenderer,
                            hatModel
                    )
            );
        }

        // =====================================================
        // CLIENT SETUP
        // =====================================================

        @SubscribeEvent
        public static void onClientSetup(
                FMLClientSetupEvent event
        ) {

        }
    }
}
package net.kn.horrormod;

import com.mojang.logging.LogUtils;
import net.kn.horrormod.block.ModBlocks;
import net.kn.horrormod.entity.ModEntity;
import net.kn.horrormod.entity.StalkerEntity;
import net.kn.horrormod.item.ModItems;
import net.minecraft.client.renderer.entity.ZombieRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;

@Mod(HorrorMod.MOD_ID)
public class HorrorMod
{

    public static final String MOD_ID = "horrormod";

    private static final Logger LOGGER = LogUtils.getLogger();

    public HorrorMod(FMLJavaModLoadingContext context)
    {
        IEventBus modEventBus = context.getModEventBus();
        //реєстрація папки (class) ITEM, до основного файлу. modEventBus - підгружає ці предмети під час завантаження лаунчера
        ModItems.ITEMS.register(modEventBus);

        ModBlocks.BLOCKS.register(modEventBus);

        ModEntity.ENTITY_TYPES.register(modEventBus);
        modEventBus.addListener(this::registerAttributes);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }
    private void registerAttributes(final EntityAttributeCreationEvent event) {
        event.put(ModEntity.STALKER.get(), StalkerEntity.createAttributes().build());
    }
    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }




    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntity.STALKER.get(), ZombieRenderer::new);
        }
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}

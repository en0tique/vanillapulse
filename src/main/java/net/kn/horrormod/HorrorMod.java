package net.kn.horrormod;

import com.mojang.logging.LogUtils;
import net.kn.horrormod.block.ModBlocks;
import net.kn.horrormod.item.ModItems;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;


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
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {

    }




    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}

package net.kn.horrormod.event;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "horrormod")
public class SleepEventManager {

    private static final Random RANDOM = new Random();
    private static final long AGGRESSION_START_DAY = 5;

    @SubscribeEvent
    public static void OnPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase != TickEvent.Phase.END) return;
        if(!(event.player instanceof ServerPlayer player)) return;
        if(!player.isSleeping()) return;

        long days = player.level().getDayTime() / 24000L;
        if (days < AGGRESSION_START_DAY) return;

        if (RANDOM.nextInt(18000) == 0){
            breakBedAndWake(player);
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTime(SleepFinishedTimeEvent event) {
        ServerLevel level = (ServerLevel) event.getLevel();
        long days = level.getDayTime() / 24000L;

        if (days >= AGGRESSION_START_DAY && RANDOM.nextInt(3) == 0) {
            long extraDays = 2 + RANDOM.nextInt(3);
            long randomTimeOfDay = RANDOM.nextInt(24000);
            long newTime = level.getDayTime() - (level.getDayTime() % 24000L) + (extraDays * 24000L) + randomTimeOfDay;
            event.setTimeAddition(newTime);
        }
    }

    private static void breakBedAndWake(ServerPlayer player) {
        player.getSleepingPos().ifPresent(bedPos -> {
            player.level().destroyBlock(bedPos, false);
            player.level().playSound(null, bedPos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 1.0f, 0.7f);
        });
        player.stopSleepInBed(false, false);
    }

    @SubscribeEvent
    public static void onSleepTimeCheck(SleepingTimeCheckEvent event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel level)) return;

        long days = level.getDayTime() / 24000L;
        if (days < AGGRESSION_START_DAY) return;

        if (RANDOM.nextInt(20) == 0) {
            event.setResult(net.minecraftforge.eventbus.api.Event.Result.DENY);
        }
    }
}
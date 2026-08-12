package net.kn.horrormod.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.SleepingTimeCheckEvent;
import net.minecraftforge.event.level.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "horrormod")
public class SleepEventManager {

    private static final Random RANDOM = new Random();

    private static final long AGGRESSION_START_DAY = 5L;

    private static final double DANGEROUS_SLEEP_CHANCE = 0.15D;
    private static final double BED_ATTACK_CHANCE = 0.15D;
    private static final double TIME_SKIP_CHANCE = 0.30D;

    private static final int MIN_SLEEP_TICKS = 20;
    private static final int MAX_SLEEP_TICKS = 80;

    private static final int MIN_EXTRA_DAYS = 2;
    private static final int MAX_EXTRA_DAYS = 4;

    private static final int EVENT_COOLDOWN_TICKS = 100;

    private static final Map<UUID, SleepAttempt> SLEEP_ATTEMPTS = new HashMap<>();
    private static final Map<UUID, SleepData> SLEEPING_PLAYERS = new HashMap<>();
    private static final Map<UUID, Integer> COOLDOWNS = new HashMap<>();

    @SubscribeEvent
    public static void onSleepTimeCheck(SleepingTimeCheckEvent event) {
        Player player = event.getEntity();

        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }

        if (!(serverPlayer.level() instanceof ServerLevel level)) {
            return;
        }

        long days = level.getDayTime() / 24000L;

        if (days < AGGRESSION_START_DAY) {
            return;
        }

        UUID uuid = serverPlayer.getUUID();

        if (serverPlayer.isSleeping()) {
            return;
        }

        if (isOnCooldown(uuid)) {
            return;
        }

        SleepAttempt attempt = SLEEP_ATTEMPTS.get(uuid);

        if (attempt == null) {
            attempt = new SleepAttempt();
            attempt.dangerous =
                    RANDOM.nextDouble() < DANGEROUS_SLEEP_CHANCE;

            SLEEP_ATTEMPTS.put(uuid, attempt);
        }

        if (!attempt.dangerous) {
            return;
        }

        event.setResult(Event.Result.DENY);

        serverPlayer.displayClientMessage(
                Component.literal(
                        "§4§lIt is dangerous to sleep..."
                ),
                true
        );

        setCooldown(
                uuid,
                EVENT_COOLDOWN_TICKS
        );

        SLEEP_ATTEMPTS.remove(uuid);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        UUID uuid = player.getUUID();

        updateCooldown(uuid);

        if (!player.isSleeping()) {
            SLEEPING_PLAYERS.remove(uuid);
            return;
        }

        long days =
                player.level().getDayTime() / 24000L;

        if (days < AGGRESSION_START_DAY) {
            return;
        }

        SleepData sleepData =
                SLEEPING_PLAYERS.get(uuid);

        if (sleepData == null) {
            sleepData = new SleepData();

            sleepData.ticksSleeping = 0;

            sleepData.attackCheckTick =
                    MIN_SLEEP_TICKS
                            + RANDOM.nextInt(
                            MAX_SLEEP_TICKS
                                    - MIN_SLEEP_TICKS
                                    + 1
                    );

            sleepData.bedAttack =
                    RANDOM.nextDouble()
                            < BED_ATTACK_CHANCE;

            SLEEPING_PLAYERS.put(
                    uuid,
                    sleepData
            );

            SLEEP_ATTEMPTS.remove(uuid);
        }

        sleepData.ticksSleeping++;

        if (sleepData.attackTriggered) {
            return;
        }

        if (sleepData.ticksSleeping
                < sleepData.attackCheckTick) {
            return;
        }

        sleepData.attackTriggered = true;

        if (sleepData.bedAttack) {
            breakBedAndWake(player);
        }
    }

    @SubscribeEvent
    public static void onSleepFinishedTime(
            SleepFinishedTimeEvent event
    ) {
        if (!(event.getLevel()
                instanceof ServerLevel level)) {
            return;
        }

        long days =
                level.getDayTime() / 24000L;

        if (days < AGGRESSION_START_DAY) {
            return;
        }

        if (RANDOM.nextDouble()
                >= TIME_SKIP_CHANCE) {
            return;
        }

        int extraDays =
                MIN_EXTRA_DAYS
                        + RANDOM.nextInt(
                        MAX_EXTRA_DAYS
                                - MIN_EXTRA_DAYS
                                + 1
                );

        long extraTime =
                extraDays * 24000L;

        long newTime =
                event.getNewTime()
                        + extraTime;

        event.setTimeAddition(newTime);
    }

    private static void breakBedAndWake(
            ServerPlayer player
    ) {
        UUID uuid =
                player.getUUID();

        setCooldown(
                uuid,
                EVENT_COOLDOWN_TICKS
        );

        player.getSleepingPos().ifPresent(
                bedPos -> {

                    player.level().destroyBlock(
                            bedPos,
                            false
                    );

                    player.level().playSound(
                            null,
                            bedPos,
                            SoundEvents.WOOD_BREAK,
                            SoundSource.BLOCKS,
                            1.0F,
                            0.7F
                    );
                }
        );

        player.stopSleepInBed(
                false,
                false
        );

        player.displayClientMessage(
                Component.literal(
                        "§4§lSomething was near your bed..."
                ),
                true
        );

        SLEEPING_PLAYERS.remove(uuid);
        SLEEP_ATTEMPTS.remove(uuid);
    }

    private static void updateCooldown(
            UUID uuid
    ) {
        Integer cooldown =
                COOLDOWNS.get(uuid);

        if (cooldown == null) {
            return;
        }

        cooldown--;

        if (cooldown <= 0) {
            COOLDOWNS.remove(uuid);
        } else {
            COOLDOWNS.put(
                    uuid,
                    cooldown
            );
        }
    }

    private static boolean isOnCooldown(
            UUID uuid
    ) {
        Integer cooldown =
                COOLDOWNS.get(uuid);

        return cooldown != null
                && cooldown > 0;
    }

    private static void setCooldown(
            UUID uuid,
            int ticks
    ) {
        COOLDOWNS.put(
                uuid,
                ticks
        );
    }

    private static class SleepAttempt {
        boolean dangerous;
    }

    private static class SleepData {
        int ticksSleeping;
        int attackCheckTick;
        boolean attackTriggered;
        boolean bedAttack;
    }
}
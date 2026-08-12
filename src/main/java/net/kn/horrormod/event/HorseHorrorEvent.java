package net.kn.horrormod.event;

import net.kn.horrormod.entity.DeadHorseEntity;
import net.kn.horrormod.entity.ModEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import net.minecraft.server.level.ServerPlayer;
import net.kn.horrormod.network.HorrorNetwork;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "horrormod")
public class HorseHorrorEvent {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onHorseHit(LivingAttackEvent event) {

        // Не реагуємо на Dead Horse
        if (event.getEntity() instanceof DeadHorseEntity) {
            return;
        }

        // Працюємо тільки зі звичайним конем
        if (!(event.getEntity() instanceof Horse horse)) {
            return;
        }

        // Хто вдарив?
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof Player player)) {
            return;
        }

        // Тільки сервер
        Level level = horse.level();

        if (level.isClientSide()) {
            return;
        }

        // 10% шанс
        if (RANDOM.nextFloat() > 0.1F) {
            return;
        }

        // Випадковий сценарій
        boolean spawnDeadHorse = RANDOM.nextBoolean();

        if (spawnDeadHorse) {

            spawnDeadHorse(horse);

        } else {

            jumpscare(horse, player);
        }
    }

    private static void spawnDeadHorse(Horse horse) {

        Level level = horse.level();

        DeadHorseEntity deadHorse =
                ModEntity.DEAD_HORSE.get().create(level);

        if (deadHorse == null) {
            return;
        }

        deadHorse.moveTo(
                horse.getX(),
                horse.getY(),
                horse.getZ(),
                horse.getYRot(),
                horse.getXRot()
        );

        level.addFreshEntity(deadHorse);

        horse.discard();

        System.out.println(
                "HORRORMOD: Dead Horse spawned!"
        );
    }

    private static void jumpscare(
            Horse horse,
            Player player
    ) {

        if (player instanceof ServerPlayer serverPlayer) {

            HorrorNetwork.sendJumpscare(serverPlayer);
        }

        horse.discard();

        System.out.println(
                "HORRORMOD: Jumpscare!"
        );
    }
}
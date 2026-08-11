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

import java.util.Random;

@Mod.EventBusSubscriber(modid = "horrormod")
public class HorseHorrorEvent {

    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void onHorseHit(LivingAttackEvent event) {

        // Це взагалі кінь?
        if (!(event.getEntity() instanceof Horse horse)) {
            return;
        }

        // Саме гравець має вдарити коня
        Entity attacker = event.getSource().getEntity();

        if (!(attacker instanceof Player player)) {
            return;
        }

        // Працюємо тільки на сервері
        Level level = horse.level();

        if (level.isClientSide()) {
            return;
        }

        // 5% шанс
        if (RANDOM.nextFloat() > 0.05F) {
            return;
        }

        // Повідомлення гравцю
        player.sendSystemMessage(
                Component.literal(
                        "§4THE MOON HAUNTS YOU."
                )
        );

        // Створюємо Dead Horse
        DeadHorseEntity deadHorse =
                ModEntity.DEAD_HORSE.get().create(level);

        if (deadHorse == null) {
            return;
        }

        // Переносимо позицію звичайного коня
        deadHorse.moveTo(
                horse.getX(),
                horse.getY(),
                horse.getZ(),
                horse.getYRot(),
                horse.getXRot()
        );

        // Додаємо Dead Horse у світ
        level.addFreshEntity(deadHorse);

        // Видаляємо звичайного коня
        horse.discard();

        System.out.println(
                "HORRORMOD: Dead Horse spawned!"
        );
    }
}


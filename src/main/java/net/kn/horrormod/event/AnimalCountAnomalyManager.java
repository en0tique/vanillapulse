package net.kn.horrormod.event;

import net.kn.horrormod.HorrorMod;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = HorrorMod.MOD_ID)
public class AnimalCountAnomalyManager {

    private static final Random RANDOM = new Random();
    private static final int CHECK_INTERVAL = 20;
    private static final double SEARCH_RADIUS = 24.0;
    private static final double BASE_CHANCE = 0.50;
    private static final int MIN_ANIMALS = 0;
    private static final int MAX_ANIMALS = 50;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerLevel level = server.overworld();

        for (ServerPlayer player : level.players()) {
            tryAnimalAnomaly(level, player);
        }
    }

    private static void tryAnimalAnomaly(ServerLevel level, ServerPlayer player) {
        long days = level.getDayTime() / 24000L;
        double dayFactor = Math.min(1.0, days / 20.0);
        double chance = BASE_CHANCE * (0.15 + dayFactor);

        if (RANDOM.nextDouble() > chance) return;

        AABB area = new AABB(player.blockPosition()).inflate(SEARCH_RADIUS);
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, area);
        if (animals.isEmpty()) return;

        Animal target = animals.get(RANDOM.nextInt(animals.size()));

        boolean canAdd = animals.size() < MAX_ANIMALS;
        boolean canRemove = animals.size() > MIN_ANIMALS;

        if (!canAdd && !canRemove) return;

        boolean shouldAdd;
        if (!canAdd) shouldAdd = false;
        else if (!canRemove) shouldAdd = true;
        else shouldAdd = RANDOM.nextBoolean();

        if (shouldAdd) {
            duplicateAnimal(level, target);
        } else {
            target.discard();
        }
    }


    private static void duplicateAnimal(ServerLevel level, Animal original) {
        EntityType<?> type = original.getType();
        Entity clone = type.create(level);
        if (clone == null) return;

        clone.moveTo(
                original.getX() + (RANDOM.nextDouble() - 0.5) * 2,
                original.getY(),
                original.getZ() + (RANDOM.nextDouble() - 0.5) * 2,
                original.getYRot(), 0
        );

        level.addFreshEntity(clone);
    }
}
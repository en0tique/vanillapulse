package net.kn.horrormod.event;

import net.kn.horrormod.HorrorMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = HorrorMod.MOD_ID)
public class AnimalStareEventManager {

    private static final Random RANDOM = new Random();
    private static final int CHECK_INTERVAL = 2400;
    private static final double SEARCH_RADIUS = 20.0;
    private static final double BASE_CHANCE = 0.08;
    private static final int STARE_DURATION_TICKS = 100;

    private static int tickCounter = 0;
    private static Vec3 activeStarePoint = null;
    private static int stareTicksLeft = 0;
    private static List<Animal> staringAnimals = null;
    private static boolean switchToPlayerAfter = false;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerLevel level = server.overworld();

        if (stareTicksLeft > 0) {
            tickActiveStare(level);
            return;
        }

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerPlayer player : level.players()) {
            tryStartStare(level, player);
            if (stareTicksLeft > 0) break;
        }
    }

    private static void tryStartStare(ServerLevel level, ServerPlayer player) {
        long days = level.getDayTime() / 24000L;
        double dayFactor = Math.min(1.0, days / 20.0);
        double chance = BASE_CHANCE * (0.1 + dayFactor);

        if (RANDOM.nextDouble() > chance) return;

        AABB area = new AABB(player.blockPosition()).inflate(SEARCH_RADIUS);
        List<Animal> animals = level.getEntitiesOfClass(Animal.class, area);
        if (animals.size() < 2) return;

        Vec3 center = Vec3.atCenterOf(player.blockPosition())
                .add((RANDOM.nextDouble() - 0.5) * SEARCH_RADIUS, 0, (RANDOM.nextDouble() - 0.5) * SEARCH_RADIUS);

        activeStarePoint = center;
        staringAnimals = animals;
        stareTicksLeft = STARE_DURATION_TICKS;
        switchToPlayerAfter = RANDOM.nextDouble() < 0.25;
    }

    private static void tickActiveStare(ServerLevel level) {
        if (staringAnimals == null) {
            stareTicksLeft = 0;
            return;
        }

        Vec3 target = activeStarePoint;

        if (switchToPlayerAfter && stareTicksLeft < STARE_DURATION_TICKS / 2) {
            Player nearest = level.getNearestPlayer(target.x, target.y, target.z, 40.0, false);
            if (nearest != null) {
                target = nearest.position();
            }
        }

        for (Animal animal : staringAnimals) {
            if (!animal.isAlive()) continue;

            double dx = target.x - animal.getX();
            double dz = target.z - animal.getZ();
            double dy = (target.y + 1.0) - animal.getEyeY();
            double distXZ = Math.sqrt(dx * dx + dz * dz);

            float yaw = (float) (Mth.atan2(dz, dx) * (180F / Math.PI)) - 90F;
            float pitch = (float) -(Mth.atan2(dy, distXZ) * (180F / Math.PI));

            animal.setYRot(yaw);
            animal.setYHeadRot(yaw);
            animal.setYBodyRot(yaw);
            animal.setXRot(pitch);
            animal.yHeadRotO = yaw;
            animal.yBodyRotO = yaw;
        }

        stareTicksLeft--;
        if (stareTicksLeft <= 0) {
            staringAnimals = null;
            activeStarePoint = null;
        }
    }
}
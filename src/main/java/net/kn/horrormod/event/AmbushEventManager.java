package net.kn.horrormod.event;

import net.kn.horrormod.entity.ModEntity;
import net.kn.horrormod.entity.StalkerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber
public class AmbushEventManager {

    private static final Random RANDOM = new Random();
    private static int cooldownTicks = randomCooldown();
    private static int activeEventTicks = 0;
    private static final int EVENT_DURATION = 3600;

    private static int randomCooldown() {
        return (24000 * RANDOM.nextInt(5)) + (RANDOM.nextInt(24000) / 4);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;

        ServerLevel level = server.overworld();
        List<ServerPlayer> players = level.players();
        if (players.isEmpty()) return;

        if (activeEventTicks > 0) {
            applyEventEffects(players);
            activeEventTicks--;

            if (activeEventTicks == 0) {
                endEvent(level, players);
            }
            return;
        }

        cooldownTicks--;
        if (cooldownTicks <= 0) {
            startEvent(level);
        }
    }

    private static void startEvent(ServerLevel level) {
        activeEventTicks = EVENT_DURATION;
        cooldownTicks = randomCooldown();
        level.setWeatherParameters(0, EVENT_DURATION + 200, true, false);
        List<ServerPlayer> players = level.players();
        if(!players.isEmpty()){
            ServerPlayer target = players.get(RANDOM.nextInt(players.size()));
            spawnStalkerNear(level, target);
        }
    }

    private static void applyEventEffects(List<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0, false, false));
            player.addEffect(new MobEffectInstance(MobEffects.DARKNESS, EVENT_DURATION, 4, false, false));
        }
    }

    private static void endEvent(ServerLevel level, List<ServerPlayer> players) {

    }

    private static void spawnStalkerNear(ServerLevel level, ServerPlayer player) {
        BlockPos spawnPos = findSpawnPosNear(level, player.blockPosition());
        if (spawnPos == null) return;

        StalkerEntity stalker = ModEntity.STALKER.get().create(level);
        if (stalker == null) return;

        stalker.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        stalker.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.EVENT, null, null);
        level.addFreshEntity(stalker);
    }

    private static BlockPos findSpawnPosNear(ServerLevel level, BlockPos center) {
        for (int attempt = 0; attempt < 10; attempt++) {
            int dx = RANDOM.nextInt(21) - 10;
            int dz = RANDOM.nextInt(21) - 10;
            BlockPos candidate = center.offset(dx, 0, dz);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate);

            if (level.getBlockState(surface).isAir() || !level.getBlockState(surface.below()).isAir()) {
                return surface;
            }
        }
        return null;
    }
}
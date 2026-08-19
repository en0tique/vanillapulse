package net.kn.horrormod.event;

import net.kn.horrormod.HorrorMod;
import net.kn.horrormod.entity.ModEntity;
import net.kn.horrormod.entity.ScarecrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = HorrorMod.MOD_ID)
public class FarmlandScarecrowManager {

    private static final Random RANDOM = new Random();
    private static final int CHECK_INTERVAL = 600;
    private static final int SEARCH_RADIUS = 32;
    private static final double SPAWN_CHANCE = 0.15;
    private static final double NEARBY_SEARCH_RADIUS = 16.0;

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
            trySpawnOnRandomFarmland(level, player);
        }
    }

    private static void trySpawnOnRandomFarmland(ServerLevel level, ServerPlayer player) {
        if (RANDOM.nextDouble() > SPAWN_CHANCE) return;

        BlockPos playerPos = player.blockPosition();
        List<BlockPos> farmlandSpots = new ArrayList<>();

        for (BlockPos pos : BlockPos.betweenClosed(
                playerPos.offset(-SEARCH_RADIUS, -5, -SEARCH_RADIUS),
                playerPos.offset(SEARCH_RADIUS, 5, SEARCH_RADIUS))) {

            if (level.getBlockState(pos).is(Blocks.FARMLAND)) {
                farmlandSpots.add(pos.immutable());
            }
        }

        if (farmlandSpots.isEmpty()) return;

        BlockPos chosen = farmlandSpots.get(RANDOM.nextInt(farmlandSpots.size()));

        List<ScarecrowEntity> nearby = level.getEntitiesOfClass(
                ScarecrowEntity.class,
                new AABB(chosen).inflate(NEARBY_SEARCH_RADIUS)
        );
        if (!nearby.isEmpty()) return;

        BlockPos spawnPos = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, chosen);

        ScarecrowEntity scarecrow = ModEntity.SCARECROW.get().create(level);
        if (scarecrow == null) return;

        scarecrow.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, 0, 0);
        scarecrow.finalizeSpawn(level, level.getCurrentDifficultyAt(spawnPos), MobSpawnType.EVENT, null, null);
        level.addFreshEntity(scarecrow);


    }
}
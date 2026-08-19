package net.kn.horrormod.event;

import net.kn.horrormod.HorrorMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Mod.EventBusSubscriber(modid = HorrorMod.MOD_ID)
public class FarmHorrorManager {

    private static final Random RANDOM = new Random();
    private static final ResourceLocation FARM_STRUCTURE = new ResourceLocation(HorrorMod.MOD_ID, "farm_house");
    private static final int CHECK_INTERVAL = 100;
    private static final int GHOST_LIFETIME = 1200;
    private static final int SEARCH_RADIUS = 80;

    private static int tickCounter = 0;
    private static final List<GhostFarm> activeFarms = new ArrayList<>();

    private record GhostFarm(BlockPos origin, BlockPos size, StructureTemplate revertSnapshot, int expiresAtTick) {
        boolean contains(BlockPos pos) {
            return pos.getX() >= origin.getX() && pos.getX() < origin.getX() + size.getX()
                    && pos.getY() >= origin.getY() && pos.getY() < origin.getY() + size.getY()
                    && pos.getZ() >= origin.getZ() && pos.getZ() < origin.getZ() + size.getZ();
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        ServerLevel level = server.overworld();

        removeExpiredFarms(level);

        tickCounter++;
        if (tickCounter < CHECK_INTERVAL) return;
        tickCounter = 0;

        for (ServerPlayer player : level.players()) {
            trySpawnGhostFarm(level, player);
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != ServerLevel.OVERWORLD) return;

        BlockPos pos = event.getPos();
        for (GhostFarm farm : activeFarms) {
            if (farm.contains(pos)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void trySpawnGhostFarm(ServerLevel level, ServerPlayer player) {
        long days = level.getDayTime() / 24000L;
        double distFromSpawn = Math.sqrt(player.blockPosition().distSqr(level.getSharedSpawnPos()));

        double dayFactor = Math.min(1.0, days / 30.0);
        double distFactor = Math.min(1.0, distFromSpawn / 2000.0);
        double chance = 0.05 * dayFactor * (0.3 + distFactor);

        if (RANDOM.nextDouble() > chance) return;

        BlockPos center = player.blockPosition().offset(
                RANDOM.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS, 0,
                RANDOM.nextInt(SEARCH_RADIUS * 2) - SEARCH_RADIUS);

        ChunkPos chunkPos = new ChunkPos(center);
        level.getChunk(chunkPos.x, chunkPos.z);

        BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, center).below(4);

        StructureTemplateManager manager = level.getStructureManager();
        StructureTemplate template = manager.get(FARM_STRUCTURE).orElse(null);
        if (template == null) return;

        StructurePlaceSettings settings = new StructurePlaceSettings();
        Vec3i sizeVec = template.getSize();
        BlockPos sizePos = new BlockPos(sizeVec.getX(), sizeVec.getY(), sizeVec.getZ());
        RandomSource randomSource = RandomSource.create();

        StructureTemplate revertSnapshot = new StructureTemplate();
        revertSnapshot.fillFromWorld(level, surface, sizeVec, true, null);

        template.placeInWorld(level, surface, surface, settings, randomSource, 2);

        activeFarms.add(new GhostFarm(surface, sizePos, revertSnapshot,
                level.getServer().getTickCount() + GHOST_LIFETIME));
    }

    private static void removeExpiredFarms(ServerLevel level) {
        int now = level.getServer().getTickCount();
        RandomSource randomSource = RandomSource.create();

        activeFarms.removeIf(farm -> {
            if (now >= farm.expiresAtTick()) {
                StructurePlaceSettings settings = new StructurePlaceSettings();
                farm.revertSnapshot().placeInWorld(level, farm.origin(), farm.origin(), settings, randomSource, 2);
                return true;
            }
            return false;
        });
    }
}
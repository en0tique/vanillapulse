package net.kn.horrormod.event;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Holder;
import net.kn.horrormod.HorrorMod;
import net.kn.horrormod.entity.ModEntity;
import net.kn.horrormod.entity.ScarecrowEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = HorrorMod.MOD_ID)
public class GuaranteedFarmManager {

    private static final ResourceLocation FARM_STRUCTURE = new ResourceLocation(HorrorMod.MOD_ID, "farm_house");
    private static final Random RANDOM = new Random();


    private static boolean isValidBiome(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        return !biome.is(BiomeTags.IS_OCEAN)
                && !biome.is(BiomeTags.IS_BEACH)
                && !biome.is(BiomeTags.IS_RIVER);
    }
    private static class FarmData extends SavedData {
        boolean placed = false;

        static FarmData create() { return new FarmData(); }

        static FarmData load(CompoundTag tag) {
            FarmData data = new FarmData();
            data.placed = tag.getBoolean("placed");
            return data;
        }

        @Override
        public CompoundTag save(CompoundTag tag) {
            tag.putBoolean("placed", placed);
            return tag;
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (level.dimension() != ServerLevel.OVERWORLD) return;

        FarmData data = level.getDataStorage().computeIfAbsent(
                FarmData::load, FarmData::create, "horrormod_guaranteed_farm"
        );

        if (data.placed) return;

        level.getServer().tell(new TickTask(100, () -> {
            BlockPos placedAt = placeGuaranteedFarm(level);
            if (placedAt != null) {
                data.placed = true;
                data.setDirty();


                level.getServer().tell(new TickTask(20, () -> trySpawnScarecrowOnFarm(level, placedAt)));
            } else {
                HorrorMod.LOGGER.warn("[HorrorMod] Не вдалось розмістити гарантовану ферму");
            }
        }));
    }

    private static BlockPos placeGuaranteedFarm(ServerLevel level) {
        BlockPos spawn = level.getSharedSpawnPos();

        for (int attempt = 0; attempt < 30; attempt++) {
            int distance = 64 + RANDOM.nextInt(33);
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            int dx = (int) (Math.cos(angle) * distance);
            int dz = (int) (Math.sin(angle) * distance);

            BlockPos candidate = spawn.offset(dx, 0, dz);

            ChunkPos chunkPos = new ChunkPos(candidate);
            level.getChunk(chunkPos.x, chunkPos.z);
            if (!isValidBiome(level, candidate)) {
                continue;
            }
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate).below(4);

            StructureTemplateManager manager = level.getStructureManager();
            StructureTemplate template = manager.get(FARM_STRUCTURE).orElse(null);
            if (template == null) {
                HorrorMod.LOGGER.warn("[HorrorMod] Шаблон 'farm_house' не знайдено");
                return null;
            }

            StructurePlaceSettings settings = new StructurePlaceSettings();
            RandomSource randomSource = RandomSource.create();

            template.placeInWorld(level, surface, surface, settings, randomSource, 2);
            HorrorMod.LOGGER.info("[HorrorMod] Гарантована ферма розміщена на {}", surface);
            return surface;
        }
        return null;
    }

    private static void trySpawnScarecrowOnFarm(ServerLevel level, BlockPos farmOrigin) {
        BlockPos farmlandSpot = findFarmlandNear(level, farmOrigin, 24);
        if (farmlandSpot == null) {
            HorrorMod.LOGGER.warn("[HorrorMod] Не знайдено вскопаної землі біля ферми для спавну пугала");
            return;
        }

        ScarecrowEntity scarecrow = ModEntity.SCARECROW.get().create(level);
        if (scarecrow == null) return;

        scarecrow.moveTo(farmlandSpot.getX() + 0.5, farmlandSpot.getY() + 1, farmlandSpot.getZ() + 0.5, 0, 0);
        scarecrow.finalizeSpawn(level, level.getCurrentDifficultyAt(farmlandSpot), MobSpawnType.EVENT, null, null);
        level.addFreshEntity(scarecrow);

        HorrorMod.LOGGER.info("[HorrorMod] Пугало заспавнено на {}", farmlandSpot);
    }

    private static BlockPos findFarmlandNear(ServerLevel level, BlockPos center, int radius) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -2, -radius),
                center.offset(radius, 6, radius))) {

            if (level.getBlockState(pos).is(Blocks.FARMLAND)) {
                double dist = center.distSqr(pos);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = pos.immutable();
                }
            }
        }
        return best;
    }
}
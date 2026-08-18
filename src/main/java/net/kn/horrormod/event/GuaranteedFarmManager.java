package net.kn.horrormod.event;

import net.kn.horrormod.HorrorMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
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

    private static class FarmData extends SavedData {
        boolean placed = false;

        static FarmData create() {
            return new FarmData();
        }

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
                FarmData::load,
                FarmData::create,
                "horrormod_guaranteed_farm"
        );

        if (data.placed) return;

        placeGuaranteedFarm(level);
        data.placed = true;
        data.setDirty();
    }

    private static void placeGuaranteedFarm(ServerLevel level) {
        BlockPos spawn = level.getSharedSpawnPos();

        for (int attempt = 0; attempt < 30; attempt++) {
            int distance = 64 + RANDOM.nextInt(33); // 64-96 блоків = 4-6 чанків
            double angle = RANDOM.nextDouble() * Math.PI * 2;
            int dx = (int) (Math.cos(angle) * distance);
            int dz = (int) (Math.sin(angle) * distance);

            BlockPos candidate = spawn.offset(dx, 0, dz);
            BlockPos surface = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, candidate);

            StructureTemplateManager manager = level.getStructureManager();
            StructureTemplate template = manager.get(FARM_STRUCTURE).orElse(null);
            if (template == null) return;

            StructurePlaceSettings settings = new StructurePlaceSettings();
            RandomSource randomSource = RandomSource.create();

            template.placeInWorld(level, surface, surface, settings, randomSource, 2);
            return;
        }
    }
}
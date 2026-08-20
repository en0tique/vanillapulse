package net.kn.horrormod.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class AnomalousCropBlock extends CropBlock {

    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            box(0, 0, 0, 16, 4, 16),
            box(0, 0, 0, 16, 8, 16),
            box(0, 0, 0, 16, 14, 16),
    };

    public AnomalousCropBlock(Properties properties) {
        super(properties);
    }

    @Override
    public int getMaxAge() {
        return 2;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        int growthSpeed = 3;
        if (random.nextInt(growthSpeed) == 0) {
            int age = getAge(state);
            if (age < getMaxAge()) {
                level.setBlock(pos, getStateForAge(age + 1), 2);
            }
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        int age = getAge(state);
        return SHAPE_BY_AGE[Math.min(age, SHAPE_BY_AGE.length - 1)];
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(Blocks.FARMLAND);
    }
}
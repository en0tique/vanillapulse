package net.kn.horrormod.entity;

import net.kn.horrormod.entity.util.VisionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

public class ScarecrowEntity extends Monster {

    private int teleportCooldown = 0;
    private float fixedYaw = 0;
    private boolean directionInitialized = false;
    public final AnimationState idleAnimationState = new AnimationState();
    public final AnimationState watchTriggerAnimationState = new AnimationState();
    private boolean wasWatching = false;

    public ScarecrowEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 1000.0)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.FOLLOW_RANGE, 40.0);
    }

    @Override
    protected void registerGoals() {
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        return true;
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide()) {
            idleAnimationState.startIfStopped(this.tickCount);
            return;
        }

        if (!directionInitialized) {
            this.fixedYaw = level().random.nextFloat() * 360.0f;
            this.setYRot(fixedYaw);
            this.setYHeadRot(fixedYaw);
            directionInitialized = true;
        }

        long days = level().getDayTime() / 24000L;
        boolean isWatching = days >= 10;

        if (isWatching && !wasWatching) {
            watchTriggerAnimationState.start(this.tickCount);
        }
        wasWatching = isWatching;

        if (!isWatching) {
            handleDormantPhase();
            return;
        }

        handleWatchingPhase();
    }

    private void handleDormantPhase() {
        this.setYRot(fixedYaw);
        this.setYHeadRot(fixedYaw);

        if (teleportCooldown > 0) {
            teleportCooldown--;
            return;
        }

        if (level().random.nextInt(2000) == 0) {
            fleeAndReappear();
            teleportCooldown = 400;
        }
    }

    private void handleWatchingPhase() {
        Player player = level().getNearestPlayer(this, 48.0);
        if (player == null) return;

        this.getLookControl().setLookAt(player.getX(), player.getEyeY(), player.getZ(), 180.0f, 180.0f);

        if (teleportCooldown > 0) {
            teleportCooldown--;
            return;
        }

        if (!VisionUtils.isPlayerLookingAt(player, this)) {
            tryTeleportCloser(player);
        }
    }

    private void tryTeleportCloser(Player player) {
        BlockPos current = this.blockPosition();
        BlockPos playerPos = player.blockPosition();

        double distSq = current.distSqr(playerPos);
        if (distSq < 16) return;

        int dx = (int) Math.signum(playerPos.getX() - current.getX()) * (3 + level().random.nextInt(4));
        int dz = (int) Math.signum(playerPos.getZ() - current.getZ()) * (3 + level().random.nextInt(4));

        BlockPos target = playerPos.offset(-dx, 0, -dz);
        BlockPos surface = level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, target);

        if (level().getBlockState(surface).isAir()) {
            this.teleportTo(surface.getX() + 0.5, surface.getY(), surface.getZ() + 0.5);
            teleportCooldown = 100;
        }
    }

    private BlockPos findNearestFarmland(BlockPos center, int radius) {
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        for (BlockPos pos : BlockPos.betweenClosed(
                center.offset(-radius, -3, -radius),
                center.offset(radius, 3, radius))) {

            if (level().getBlockState(pos).is(Blocks.FARMLAND)) {
                double dist = center.distSqr(pos);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = pos.immutable();
                }
            }
        }
        return best;
    }

    private void fleeAndReappear() {
        BlockPos newSpot = findNearestFarmland(this.blockPosition(), 20);
        if (newSpot == null) return;

        this.remove(RemovalReason.DISCARDED);

        level().getServer().tell(new TickTask(40, () -> {
            ScarecrowEntity newScarecrow = net.kn.horrormod.entity.ModEntity.SCARECROW.get().create(level());
            if (newScarecrow == null) return;

            newScarecrow.moveTo(newSpot.getX() + 0.5, newSpot.getY() + 1, newSpot.getZ() + 0.5, 0, 0);
            level().addFreshEntity(newScarecrow);
        }));
    }
}
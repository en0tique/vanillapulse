package net.kn.horrormod.entity.ai;

import net.kn.horrormod.entity.StalkerEntity;
import net.kn.horrormod.entity.util.VisionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class HideFromPlayerGoal extends Goal {

    private final PathfinderMob mob;
    private final double speed;
    private static final int SEARCH_RADIUS = 10;

    private long lastSearchTick = -1000;
    private static final int SEARCH_COOLDOWN = 20;


    private BlockPos hideSpot;

    private int hideCount = 0;

    private boolean tryDisappear() {
        hideCount++;

        double chance;

        switch (hideCount) {
            case 1 -> chance = 0.10;
            case 2 -> chance = 0.30;
            case 3 -> chance = 0.60;
            case 4 -> chance = 0.85;
            default -> chance = 1.0;
        }

        if (mob.getRandom().nextDouble() < chance) {
            mob.discard();
            return true;
        }

        return false;
    }

    private boolean isHiddenFromPlayer(Player player) {
        return isFullyConcealed(mob.blockPosition(), player);
    }


    public HideFromPlayerGoal(PathfinderMob mob, double speed) {
        this.mob = mob;
        this.speed = speed;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (mob instanceof StalkerEntity stalker && stalker.isAggressionReady() && !stalker.hasStruckRecently()) {
            return false;
        }

        if (mob.tickCount - lastSearchTick < SEARCH_COOLDOWN) {
            return false;
        }

        Player player = mob.level().getNearestPlayer(mob, 32.0);
        if (player == null) return false;

        return VisionUtils.isPlayerLookingAt(player, mob);
    }

    private BlockPos findHideSpot(Player player) {
        BlockPos mobPos = mob.blockPosition();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        for (BlockPos obstaclePos : BlockPos.betweenClosed(
                mobPos.offset(-SEARCH_RADIUS, -5, -SEARCH_RADIUS),
                mobPos.offset(SEARCH_RADIUS, 5, SEARCH_RADIUS))) {

            if (!mob.level().getBlockState(obstaclePos)
                    .isFaceSturdy(mob.level(), obstaclePos, Direction.UP)) {
                continue;
            }

            BlockPos candidate = computeHideOffset(obstaclePos, player.blockPosition());

            if (!isSpotWalkable(candidate, player)) continue;

            double dist = mobPos.distSqr(candidate);
            if (dist < bestDist) {
                bestDist = dist;
                best = candidate.immutable();
            }
        }

        return best;
    }

    private BlockPos computeHideOffset(BlockPos obstaclePos, BlockPos playerPos) {
        int dx = Integer.signum(obstaclePos.getX() - playerPos.getX());
        int dz = Integer.signum(obstaclePos.getZ() - playerPos.getZ());
        return obstaclePos.offset(dx, 0, dz);
    }

    private boolean isSpotWalkable(BlockPos pos, Player player) {
        boolean floorSolid = mob.level().getBlockState(pos.below())
                .isFaceSturdy(mob.level(), pos.below(), Direction.UP);
        boolean spaceFree = mob.level().getBlockState(pos).isAir()
                && mob.level().getBlockState(pos.above()).isAir();
        if(!floorSolid || !spaceFree) return false;
        return isFullyConcealed(pos, player);
    }
    private boolean isFullyConcealed(BlockPos standPos, Player player){
        Vec3 base = Vec3.atBottomCenterOf(standPos);
        Vec3 feet = base;
        Vec3 mid = base.add(0, mob.getBbHeight() / 2.0, 0);
        Vec3 head = base.add(0, mob.getBbHeight(), 0);
        Vec3 eyePos = player.getEyePosition();

        for (Vec3 point : new Vec3[]{feet, mid, head}){
            ClipContext ctx = new ClipContext(eyePos, point, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
            if(mob.level().clip(ctx).getType() == HitResult.Type.MISS){
                return false;
            }
        }
        return true;
    }

    @Override
    public void start() {
        lastSearchTick = mob.tickCount;

        Player player = mob.level().getNearestPlayer(mob, 16.0);
        if (player == null) return;

        hideSpot = findHideSpot(player);

        if (hideSpot != null) {
            mob.getNavigation().moveTo(hideSpot.getX() + 0.5, hideSpot.getY(), hideSpot.getZ() + 0.5, speed);
        }
    }

    @Override
    public void tick() {
        if (hideSpot == null) {
            return;
        }

        // Ще не добіг до укриття
        if (!mob.getNavigation().isDone()) {
            return;
        }

        Player player = mob.level().getNearestPlayer(mob, 32.0);

        if (player == null) {
            hideSpot = null;
            return;
        }

        // Моб вже в укритті, але гравець його все ще бачить
        if (!isHiddenFromPlayer(player)) {
            hideSpot = null;
            return;
        }

        // Моб реально схований -> перевіряємо шанс
        tryDisappear();

        // Завершуємо це ховання,
        // щоб наступного разу canUse() запустив його знову
        hideSpot = null;
    }

    @Override
    public boolean canContinueToUse() {
        return hideSpot != null;
    }

}
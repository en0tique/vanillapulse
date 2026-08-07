package net.kn.horrormod.entity.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.datatransfer.Clipboard;

public class VisionUtils {

    private static final double LOOK_ANGLE_THRESHOLD = 0.97;

    public static boolean isPlayerLookingAt(Player player, LivingEntity target){
        Vec3 eyePos = player.getEyePosition();
        Vec3 lookVec = player.getLookAngle();

        Vec3 feet = target.position();
        Vec3 mid = target.position().add(0, target.getBbHeight() / 2.0, 0);
        Vec3 head = target.getEyePosition();

        for (Vec3 point : new Vec3[]{feet, mid, head}){
            Vec3 toPoint = point.subtract(eyePos).normalize();
            double angleCos = toPoint.dot(lookVec);
            if (angleCos > LOOK_ANGLE_THRESHOLD && hasClearSight(player.level(), eyePos, point)){
               return true;
            }
        }
        return false;
    }
    private static boolean hasClearSight(Level level, Vec3 from, Vec3 to){
        ClipContext ctx = new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null);
        BlockHitResult result = level.clip(ctx);
        return result.getType() == HitResult.Type.MISS;
    }


}

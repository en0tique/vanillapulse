package net.kn.horrormod.entity;

import net.kn.horrormod.entity.ai.HideFromPlayerGoal;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;


public class StalkerEntity  extends Monster {
    public StalkerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
    public final AnimationState walkAnimationState = new AnimationState();
    private int walkAnimationTimeout = 0;

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 40.0)
                .add(Attributes.ARMOR, 10.0)
                .add(Attributes.ATTACK_DAMAGE, 10.0)
                .add(Attributes.MOVEMENT_SPEED, 0.5)
                .add(Attributes.FOLLOW_RANGE, 32.0);
                

    }
    @Override
    protected void registerGoals(){
        this.goalSelector.addGoal(1, new HideFromPlayerGoal(this, 0.8));
    }
    @Override
    public void tick() {
        super.tick();
        setupWalkAnimationState();
        Player player = level().getNearestPlayer(this, 16.0);
        if (player != null) {
            this.getLookControl().setLookAt(
                    player.getX(),
                    player.getEyeY(),
                    player.getZ(),
                    30.0f,
                    30.0f
            );
        }
    }

    private void setupWalkAnimationState() {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.walkAnimationTimeout <= 0) {
                this.walkAnimationTimeout = 40;
                this.walkAnimationState.startIfStopped(this.tickCount);
            } else {
                --this.walkAnimationTimeout;
            }
        } else {
            this.walkAnimationState.stop();
        }
    }
}

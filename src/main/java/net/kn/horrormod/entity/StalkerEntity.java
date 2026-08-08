package net.kn.horrormod.entity;

import net.kn.horrormod.entity.ai.HideFromPlayerGoal;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


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
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 32.0f, 1.0f));
    }
    @Override
    public void tick() {
        super.tick();
        setupWalkAnimationState();
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
    @Override
    public int getMaxHeadYRot() {
        return 180;
    }
}

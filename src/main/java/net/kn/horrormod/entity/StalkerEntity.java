package net.kn.horrormod.entity;

import net.kn.horrormod.entity.ai.HideFromPlayerGoal;
import net.kn.horrormod.entity.util.VisionUtils;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;


public class StalkerEntity  extends Monster {
    public StalkerEntity(EntityType<? extends Monster> type, Level level) {
        super(type, level);
    }
    public final AnimationState walkAnimationState = new AnimationState();
    private int walkAnimationTimeout = 0;

    private boolean hasStruck = false;
    private int watchedTicks = 0;
    private static final int CHARGE_THRESHOLD_TICKS = 100;
    private static final long AGGRESSION_START_DAY = 4;

    private int getChargeThresholdTicks() {
        long days = level().getDayTime() / 24000L;

        if (days < 6) return 100;
        if (days < 10) return 80;
        if (days < 20) return 40;
        return 20;
    }

    @Override

    public boolean doHurtTarget(Entity target){
        boolean result = super.doHurtTarget(target);
        if(result){
            hasStruck = true;
            this.setTarget(null);
        }
        return result;
    }
    public boolean hasStruckRecently(){
        return hasStruck;
    }
    public void resetStrike(){
        hasStruck = false;
    }
    private boolean isAggressionUnlocked(){
        long days = level().getDayTime() / 24000L;
        return days >= AGGRESSION_START_DAY;
    }
    public boolean isAggressionReady(){
        return isAggressionUnlocked();
    }


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
        this.goalSelector.addGoal(0, new MeleeAttackGoal(this, 1.0, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }
    @Override
    public void tick() {
        super.tick();
        setupWalkAnimationState();
        updateWatchedState();
    }

    private void updateWatchedState(){
        if(!isAggressionUnlocked()){
            watchedTicks = 0;
            return;
        }

        Player player = level().getNearestPlayer(this, 32.0);
        if (player != null && VisionUtils.isPlayerLookingAt(player, this)){
            watchedTicks++;
            if (watchedTicks >= getChargeThresholdTicks()){
                if(hasStruck){
                    resetStrike();
                }
                triggerAttack(player);
            }
        } else {
            watchedTicks = 0;
        }
    }
    private void triggerAttack(Player player){
        this.setTarget(player);
        watchedTicks = 0;
    }


    private void setupWalkAnimationState() {
        if (this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-6) {
            if (this.walkAnimationTimeout <= 0) {
                this.walkAnimationTimeout = 1;
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

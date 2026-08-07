package net.kn.horrormod.entity;

import net.kn.horrormod.entity.ai.HideFromPlayerGoal;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;


public class StalkerEntity  extends Zombie {
    public StalkerEntity(EntityType<? extends Zombie> type, Level level){
        super(type, level);
    }
    public static AttributeSupplier.Builder createAttributes(){
        return Zombie.createAttributes()
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

}

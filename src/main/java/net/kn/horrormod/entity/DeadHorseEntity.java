package net.kn.horrormod.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DeadHorseEntity extends Horse {

    public DeadHorseEntity(
            EntityType<? extends Horse> entityType,
            Level level
    ) {
        super(entityType, level);
    }

    public static AttributeSupplier.Builder createAttributes() {

        return createBaseHorseAttributes()
                .add(
                        net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE,
                        6.0D
                )
                .add(
                        net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED,
                        1.0D
                );
    }


    @Override
    protected void registerGoals() {

        // Не даємо потонути
        this.goalSelector.addGoal(
                0,
                new FloatGoal(this)
        );

        // Переслідування і атака гравця
        this.goalSelector.addGoal(
                1,
                new MeleeAttackGoal(
                        this,
                        1.25D,
                        true
                )
        );

        // Дивиться на найближчого гравця
        this.goalSelector.addGoal(
                2,
                new LookAtPlayerGoal(
                        this,
                        Player.class,
                        16.0F
                )
        );

        // Випадкове пересування
        this.goalSelector.addGoal(
                5,
                new WaterAvoidingRandomStrollGoal(
                        this,
                        1.0D
                )
        );

        // Випадково дивиться навколо
        this.goalSelector.addGoal(
                6,
                new RandomLookAroundGoal(this)
        );

        // Вибирає гравця як ціль
        this.targetSelector.addGoal(
                1,
                new NearestAttackableTargetGoal<>(
                        this,
                        Player.class,
                        true
                )
        );
    }

    @Override
    public boolean isFood(net.minecraft.world.item.ItemStack stack) {
        return false;
    }
}


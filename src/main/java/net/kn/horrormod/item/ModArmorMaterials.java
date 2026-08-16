package net.kn.horrormod.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.ArmorItem;

public enum ModArmorMaterials implements ArmorMaterial {

    STRAW_HAT(
            "straw_hat",
            9,
            new int[] {
                    0, 0, 0, 0
            },
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            () -> Ingredient.of(Items.WHEAT)
    );

    private static final int[] DURABILITY_MULTIPLIERS = {
            13, 15, 16, 11
    };

    private final String name;
    private final int durabilityMultiplier;
    private final int[] protection;
    private final int enchantmentValue;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final java.util.function.Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(
            String name,
            int durabilityMultiplier,
            int[] protection,
            int enchantmentValue,
            SoundEvent equipSound,
            float toughness,
            float knockbackResistance,
            java.util.function.Supplier<Ingredient> repairIngredient
    ) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.protection = protection;
        this.enchantmentValue = enchantmentValue;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return DURABILITY_MULTIPLIERS[type.getSlot().getIndex()]
                * durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return protection[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return "horrormod:" + name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
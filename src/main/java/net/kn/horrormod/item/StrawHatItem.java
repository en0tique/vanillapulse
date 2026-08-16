package net.kn.horrormod.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

import java.util.UUID;

public class StrawHatItem extends ArmorItem {

    private static final UUID ARMOR_UUID =
            UUID.fromString(
                    "7d9c2f1e-5c4a-4f91-9e36-3f0e8c1b2a77"
            );

    private static final Multimap<Attribute, AttributeModifier> HEAD_MODIFIERS =
            createHeadModifiers();

    public StrawHatItem(Properties properties) {
        super(
                ModArmorMaterials.STRAW_HAT,
                Type.HELMET,
                properties
        );
    }

    private static Multimap<Attribute, AttributeModifier> createHeadModifiers() {

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder =
                ImmutableMultimap.builder();

        builder.put(
                Attributes.ARMOR,
                new AttributeModifier(
                        ARMOR_UUID,
                        "Straw hat armor",
                        0.5D,
                        AttributeModifier.Operation.ADDITION
                )
        );

        return builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(
            EquipmentSlot slot
    ) {
        if (slot == EquipmentSlot.HEAD) {
            return HEAD_MODIFIERS;
        }

        return super.getDefaultAttributeModifiers(slot);
    }
}
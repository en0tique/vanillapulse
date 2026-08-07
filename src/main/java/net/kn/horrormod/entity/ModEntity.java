package net.kn.horrormod.entity;

import net.kn.horrormod.HorrorMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.swing.text.html.parser.Entity;

public class ModEntity {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, HorrorMod.MOD_ID);
    public static final RegistryObject<EntityType<StalkerEntity>> STALKER =
            ENTITY_TYPES.register("stalker", ()->
                    EntityType.Builder.of(StalkerEntity::new, MobCategory.MONSTER)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build("stalker")
            );
}

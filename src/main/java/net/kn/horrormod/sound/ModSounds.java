package net.kn.horrormod.sound;

import net.kn.horrormod.HorrorMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(
                    ForgeRegistries.SOUND_EVENTS,
                    HorrorMod.MOD_ID
            );

    public static final RegistryObject<SoundEvent> JUMPSCARE =
            SOUNDS.register(
                    "jumpscare",
                    () -> SoundEvent.createVariableRangeEvent(
                            new ResourceLocation(
                                    HorrorMod.MOD_ID,
                                    "jumpscare"
                            )
                    )
            );
}
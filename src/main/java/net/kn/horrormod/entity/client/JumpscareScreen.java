package net.kn.horrormod.entity.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.kn.horrormod.sound.ModSounds;

public class JumpscareScreen extends Screen {

    private int ticks = 0;

    private static final ResourceLocation JUMPSCARE_TEXTURE =
            new ResourceLocation(
                    "horrormod",
                    "textures/gui/horse_jumpscare.png"
            );



    public JumpscareScreen() {
        super(Component.empty());
    }

    @Override
    protected void init() {

        super.init();

        Minecraft.getInstance().getSoundManager().play(
                SimpleSoundInstance.forUI(
                        ModSounds.JUMPSCARE.get(),
                        1.0F
                )
        );
    }

    @Override
    public void render(
            GuiGraphics guiGraphics,
            int mouseX,
            int mouseY,
            float partialTick
    ) {

        // PNG на весь екран
        guiGraphics.blit(
                JUMPSCARE_TEXTURE,
                0,
                0,
                0,
                0,
                this.width,
                this.height,
                this.width,
                this.height
        );
    }

    @Override
    public void tick() {

        ticks++;

        // 40 ticks ≈ 2 секунди
        if (ticks >= 40) {
            this.onClose();
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
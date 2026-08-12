package net.kn.horrormod.network;

import net.kn.horrormod.entity.client.JumpscareScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JumpscarePacket {

    public JumpscarePacket() {
    }

    public static void encode(
            JumpscarePacket packet,
            FriendlyByteBuf buffer
    ) {
    }

    public static JumpscarePacket decode(
            FriendlyByteBuf buffer
    ) {
        return new JumpscarePacket();
    }

    public static void handle(
            JumpscarePacket packet,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {

        NetworkEvent.Context context =
                contextSupplier.get();

        context.enqueueWork(() -> {

            DistExecutor.unsafeRunWhenOn(
                    Dist.CLIENT,
                    () -> () -> {
                        net.minecraft.client.Minecraft.getInstance()
                                .setScreen(
                                        new JumpscareScreen()
                                );
                    }
            );

        });

        context.setPacketHandled(true);
    }
}
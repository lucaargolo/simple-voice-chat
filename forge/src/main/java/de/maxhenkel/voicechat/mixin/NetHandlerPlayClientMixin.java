package de.maxhenkel.voicechat.mixin;

import de.maxhenkel.voicechat.intercompatibility.ClientCompatibilityManager;
import de.maxhenkel.voicechat.intercompatibility.ForgeClientCompatibilityManager;
import de.maxhenkel.voicechat.net.ForgeNetworkEvents;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetHandlerPlayClient.class)
public class NetHandlerPlayClientMixin {

    @Inject(method = "handleCustomPayload", at = @At("HEAD"), cancellable = true)
    private void handleCustomPayload(S3FPacketCustomPayload packetIn, CallbackInfo ci) {
        if (ForgeNetworkEvents.onCustomPayloadClient(packetIn)) {
            ci.cancel();
        }
    }

    @Inject(method = "handleJoinGame", at = @At("TAIL"))
    private void handleJoinGame(S01PacketJoinGame packetIn, CallbackInfo ci) {
        ((ForgeClientCompatibilityManager) ClientCompatibilityManager.INSTANCE).onJoinWorld();
    }

}

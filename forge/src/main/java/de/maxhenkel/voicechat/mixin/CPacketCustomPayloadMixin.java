package de.maxhenkel.voicechat.mixin;

import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(C17PacketCustomPayload.class)
public class CPacketCustomPayloadMixin {

    @ModifyConstant(method = "readPacketData", constant = @Constant(intValue = 20))
    private int injected(int value) {
        return 64;
    }

}

package de.maxhenkel.voicechat.mixin;

import de.maxhenkel.voicechat.intercompatibility.ClientCompatibilityManager;
import de.maxhenkel.voicechat.intercompatibility.ForgeClientCompatibilityManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.world.WorldSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(method = "shareToLAN", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/management/ServerConfigurationManager;setCommandsAllowedForAll(Z)V", shift = At.Shift.AFTER), locals = LocalCapture.CAPTURE_FAILHARD)
    private void shareToLAN(WorldSettings.GameType type, boolean allowCheats, CallbackInfoReturnable<String> cir, int i) {
        ((ForgeClientCompatibilityManager) ClientCompatibilityManager.INSTANCE).onOpenPort(i);
    }

}

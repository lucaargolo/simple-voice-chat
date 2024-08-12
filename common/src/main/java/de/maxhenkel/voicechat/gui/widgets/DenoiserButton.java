package de.maxhenkel.voicechat.gui.widgets;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.voice.client.Denoiser;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public class DenoiserButton extends BooleanConfigButton {

    private static final IChatComponent ENABLED = new ChatComponentTranslation("message.voicechat.enabled");
    private static final IChatComponent DISABLED = new ChatComponentTranslation("message.voicechat.disabled");

    public DenoiserButton(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height, VoicechatClient.CLIENT_CONFIG.denoiser, enabled -> {
            return new ChatComponentTranslation("message.voicechat.denoiser", enabled ? ENABLED : DISABLED);
        });
        if (Denoiser.createDenoiser() == null) {
            enabled = false;
        }
    }

}

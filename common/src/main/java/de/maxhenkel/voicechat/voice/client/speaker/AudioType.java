package de.maxhenkel.voicechat.voice.client.speaker;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public enum AudioType {

    NORMAL(new ChatComponentTranslation("message.voicechat.audio_type.normal")), REDUCED(new ChatComponentTranslation("message.voicechat.audio_type.reduced")), OFF(new ChatComponentTranslation("message.voicechat.audio_type.off"));

    private final IChatComponent component;

    AudioType(IChatComponent component) {
        this.component = component;
    }

    public IChatComponent getText() {
        return component;
    }
}

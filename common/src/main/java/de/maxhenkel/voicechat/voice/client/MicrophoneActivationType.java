package de.maxhenkel.voicechat.voice.client;

import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public enum MicrophoneActivationType {

    PTT(new ChatComponentTranslation("message.voicechat.activation_type.ptt")), VOICE(new ChatComponentTranslation("message.voicechat.activation_type.voice"));

    private final IChatComponent component;

    MicrophoneActivationType(IChatComponent component) {
        this.component = component;
    }

    public IChatComponent getText() {
        return component;
    }
}

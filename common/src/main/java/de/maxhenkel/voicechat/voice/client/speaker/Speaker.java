package de.maxhenkel.voicechat.voice.client.speaker;

import net.minecraft.util.Vec3;

import javax.annotation.Nullable;

public interface Speaker {

    void open() throws SpeakerException;

    void play(short[] data, float volume, @Nullable Vec3 position, @Nullable String category, float maxDistance);

    default void play(short[] data, float volume, @Nullable String category) {
        play(data, volume, null, category, 0F);
    }

    void close();

}

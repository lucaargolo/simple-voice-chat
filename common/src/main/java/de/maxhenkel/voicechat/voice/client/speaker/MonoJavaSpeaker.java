package de.maxhenkel.voicechat.voice.client.speaker;

import de.maxhenkel.voicechat.voice.client.PositionalAudioUtils;
import net.minecraft.util.Vec3;

import javax.annotation.Nullable;

public class MonoJavaSpeaker extends JavaSpeakerBase {

    @Override
    protected short[] convertToStereo(short[] data, @Nullable Vec3 position) {
        return PositionalAudioUtils.convertToStereo(data);
    }
}
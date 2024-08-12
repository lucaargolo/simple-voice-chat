package de.maxhenkel.voicechat.integration.freecam;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.voice.client.PositionalAudioUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

public class FreecamUtil {

    private static final Minecraft mc = Minecraft.getMinecraft();

    /**
     * @return whether freecam is currently in use
     */
    public static boolean isFreecamEnabled() {
        if (mc.thePlayer == null) {
            return false;
        }
        return VoicechatClient.CLIENT_CONFIG.freecamMode.get().equals(FreecamMode.PLAYER) && !(mc.thePlayer.isSpectator() || mc.thePlayer.equals(mc.getRenderViewEntity()));
    }

    /**
     * Gets the proximity reference point. Unless freecam is active, this is the main camera's position.
     *
     * @return the position distances should be measured from
     */
    public static Vec3 getReferencePoint() {
        if (mc.thePlayer == null) {
            return new Vec3(0.0, 0.0, 0.0);
        }
        return isFreecamEnabled() ? mc.thePlayer.getPositionEyes(1F) : PositionalAudioUtils.getCameraPosition();
    }

    /**
     * Measures the distance to the provided position.
     * <p>
     * Distance is relative to either the player or camera, depending on whether freecam is enabled.
     *
     * @param pos the position to be measured
     * @return the distance to the position
     */
    public static double getDistanceTo(Vec3 pos) {
        return getReferencePoint().distanceTo(pos);
    }

    /**
     * Gets the volume for the provided distance.
     * <p>
     * Distance is relative to either the player or camera, depending on whether freecam is enabled.
     *
     * @param maxDistance the maximum distance of the sound
     * @param pos         the position of the audio
     * @return the resulting audio volume
     */
    public static float getDistanceVolume(float maxDistance, Vec3 pos) {
        return PositionalAudioUtils.getDistanceVolume(maxDistance, getReferencePoint(), pos);
    }
}

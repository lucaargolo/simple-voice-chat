package de.maxhenkel.voicechat.voice.common;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Vec3;

import javax.annotation.Nullable;
import java.util.UUID;

public class LocationSoundPacket extends SoundPacket<LocationSoundPacket> {

    protected Vec3 location;
    protected float distance;

    public LocationSoundPacket(UUID channelId, UUID sender, Vec3 location, byte[] data, long sequenceNumber, float distance, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
        this.location = location;
        this.distance = distance;
    }

    public LocationSoundPacket(UUID channelId, UUID sender, short[] data, Vec3 location, float distance, @Nullable String category) {
        super(channelId, sender, data, category);
        this.location = location;
        this.distance = distance;
    }

    public LocationSoundPacket() {

    }

    public Vec3 getLocation() {
        return location;
    }

    public float getDistance() {
        return distance;
    }

    @Override
    public LocationSoundPacket fromBytes(PacketBuffer buf) {
        LocationSoundPacket soundPacket = new LocationSoundPacket();
        soundPacket.channelId = buf.readUuid();
        soundPacket.sender = buf.readUuid();
        soundPacket.location = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();
        soundPacket.distance = buf.readFloat();

        byte data = buf.readByte();
        if (hasFlag(data, HAS_CATEGORY_MASK)) {
            soundPacket.category = buf.readStringFromBuffer(16);
        }

        return soundPacket;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUuid(channelId);
        buf.writeUuid(sender);
        buf.writeDouble(location.xCoord);
        buf.writeDouble(location.yCoord);
        buf.writeDouble(location.zCoord);
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);
        buf.writeFloat(distance);

        byte data = 0b0;
        if (category != null) {
            data = setFlag(data, HAS_CATEGORY_MASK);
        }
        buf.writeByte(data);
        if (category != null) {
            buf.writeString(category);
        }
    }
}

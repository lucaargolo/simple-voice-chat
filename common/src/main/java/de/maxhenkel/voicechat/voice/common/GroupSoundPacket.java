package de.maxhenkel.voicechat.voice.common;

import net.minecraft.network.PacketBuffer;

import javax.annotation.Nullable;
import java.util.UUID;

public class GroupSoundPacket extends SoundPacket<GroupSoundPacket> {

    public GroupSoundPacket(UUID channelId, UUID sender, byte[] data, long sequenceNumber, @Nullable String category) {
        super(channelId, sender, data, sequenceNumber, category);
    }

    public GroupSoundPacket(UUID channelId, UUID sender, short[] data, @Nullable String category) {
        super(channelId, sender, data, category);
    }

    public GroupSoundPacket() {

    }

    @Override
    public GroupSoundPacket fromBytes(PacketBuffer buf) {
        GroupSoundPacket soundPacket = new GroupSoundPacket();
        soundPacket.channelId = buf.readUuid();
        soundPacket.sender = buf.readUuid();
        soundPacket.data = buf.readByteArray();
        soundPacket.sequenceNumber = buf.readLong();

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
        buf.writeByteArray(data);
        buf.writeLong(sequenceNumber);

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

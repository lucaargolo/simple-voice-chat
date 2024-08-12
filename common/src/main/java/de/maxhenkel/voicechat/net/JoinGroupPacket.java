package de.maxhenkel.voicechat.net;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.UUID;

public class JoinGroupPacket implements Packet<JoinGroupPacket> {

    public static final ResourceLocation SET_GROUP = new ResourceLocation(NetManager.CHANNEL, "set_group");

    private UUID group;
    @Nullable
    private String password;

    public JoinGroupPacket() {

    }

    public JoinGroupPacket(UUID group, @Nullable String password) {
        this.group = group;
        this.password = password;
    }

    public UUID getGroup() {
        return group;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    @Override
    public ResourceLocation getIdentifier() {
        return SET_GROUP;
    }

    @Override
    public JoinGroupPacket fromBytes(PacketBuffer buf) {
        group = buf.readUuid();
        if (buf.readBoolean()) {
            password = buf.readStringFromBuffer(512);
        }
        return this;
    }

    @Override
    public void toBytes(PacketBuffer buf) {
        buf.writeUuid(group);
        buf.writeBoolean(password != null);
        if (password != null) {
            buf.writeString(password);
        }
    }

}

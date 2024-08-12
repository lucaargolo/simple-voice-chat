package de.maxhenkel.voicechat.gui;

import de.maxhenkel.voicechat.api.Group;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public enum GroupType {
    NORMAL(new ChatComponentTranslation("message.voicechat.group_type.normal"), new ChatComponentTranslation("message.voicechat.group_type.normal.description"), Group.Type.NORMAL),
    OPEN(new ChatComponentTranslation("message.voicechat.group_type.open"), new ChatComponentTranslation("message.voicechat.group_type.open.description"), Group.Type.OPEN),
    ISOLATED(new ChatComponentTranslation("message.voicechat.group_type.isolated"), new ChatComponentTranslation("message.voicechat.group_type.isolated.description"), Group.Type.ISOLATED);

    private final IChatComponent translation;
    private final IChatComponent description;
    private final Group.Type type;

    GroupType(IChatComponent translation, IChatComponent description, Group.Type type) {
        this.translation = translation;
        this.description = description;
        this.type = type;
    }

    public IChatComponent getTranslation() {
        return translation;
    }

    public IChatComponent getDescription() {
        return description;
    }

    public Group.Type getType() {
        return type;
    }

    public static GroupType fromType(Group.Type type) {
        for (GroupType groupType : values()) {
            if (groupType.getType() == type) {
                return groupType;
            }
        }
        return NORMAL;
    }

}

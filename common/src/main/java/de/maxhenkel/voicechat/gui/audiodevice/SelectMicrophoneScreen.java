package de.maxhenkel.voicechat.gui.audiodevice;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.voice.client.microphone.MicrophoneManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.*;

import javax.annotation.Nullable;
import java.util.List;

public class SelectMicrophoneScreen extends SelectDeviceScreen {

    public static final ResourceLocation MICROPHONE_ICON = new ResourceLocation(Voicechat.MODID, "textures/icons/microphone.png");
    public static final IChatComponent TITLE = new ChatComponentTranslation("gui.voicechat.select_microphone.title");
    public static final IChatComponent NO_MICROPHONE = new ChatComponentTranslation("message.voicechat.no_microphone").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));

    public SelectMicrophoneScreen(@Nullable GuiScreen parent) {
        super(TITLE, parent);
    }

    @Override
    public List<String> getDevices() {
        return MicrophoneManager.deviceNames();
    }

    @Override
    public ResourceLocation getIcon() {
        return MICROPHONE_ICON;
    }

    @Override
    public IChatComponent getEmptyListComponent() {
        return NO_MICROPHONE;
    }

    @Override
    public ConfigEntry<String> getConfigEntry() {
        return VoicechatClient.CLIENT_CONFIG.microphone;
    }
}

package de.maxhenkel.voicechat.gui.tooltips;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.widgets.ImageButton;
import de.maxhenkel.voicechat.voice.client.ClientPlayerStateManager;
import de.maxhenkel.voicechat.voice.client.MicrophoneActivationType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class MuteTooltipSupplier implements ImageButton.TooltipSupplier {

    private GuiScreen screen;
    private ClientPlayerStateManager stateManager;

    public MuteTooltipSupplier(GuiScreen screen, ClientPlayerStateManager stateManager) {
        this.screen = screen;
        this.stateManager = stateManager;
    }

    @Override
    public void onTooltip(ImageButton button, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();

        if (!canMuteMic()) {
            tooltip.add(new ChatComponentTranslation("message.voicechat.mute.disabled_ptt").getUnformattedText());
        } else if (stateManager.isMuted()) {
            tooltip.add(new ChatComponentTranslation("message.voicechat.mute.enabled").getUnformattedText());
        } else {
            tooltip.add(new ChatComponentTranslation("message.voicechat.mute.disabled").getUnformattedText());
        }

        GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, screen.width, screen.height, -1, screen.mc.fontRendererObj);
    }

    public static boolean canMuteMic() {
        return VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals(MicrophoneActivationType.VOICE);
    }

}

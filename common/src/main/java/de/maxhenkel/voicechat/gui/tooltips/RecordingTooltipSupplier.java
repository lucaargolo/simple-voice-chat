package de.maxhenkel.voicechat.gui.tooltips;

import de.maxhenkel.voicechat.gui.widgets.ImageButton;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class RecordingTooltipSupplier implements ImageButton.TooltipSupplier {

    private final GuiScreen screen;

    public RecordingTooltipSupplier(GuiScreen screen) {
        this.screen = screen;
    }

    @Override
    public void onTooltip(ImageButton button, int mouseX, int mouseY) {
        ClientVoicechat client = ClientManager.getClient();
        if (client == null) {
            return;
        }

        List<String> tooltip = new ArrayList<>();

        if (client.getRecorder() == null) {
            tooltip.add(new ChatComponentTranslation("message.voicechat.recording.disabled").getUnformattedText());
        } else {
            tooltip.add(new ChatComponentTranslation("message.voicechat.recording.enabled").getUnformattedText());
        }

        GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, screen.width, screen.height, -1, screen.mc.fontRendererObj);
    }

}

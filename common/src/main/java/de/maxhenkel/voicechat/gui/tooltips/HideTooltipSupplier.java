package de.maxhenkel.voicechat.gui.tooltips;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.widgets.ImageButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class HideTooltipSupplier implements ImageButton.TooltipSupplier {

    private final GuiScreen screen;

    public HideTooltipSupplier(GuiScreen screen) {
        this.screen = screen;
    }

    @Override
    public void onTooltip(ImageButton button, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();

        if (VoicechatClient.CLIENT_CONFIG.hideIcons.get()) {
            tooltip.add(new ChatComponentTranslation("message.voicechat.hide_icons.enabled").getUnformattedText());
        } else {
            tooltip.add(new ChatComponentTranslation("message.voicechat.hide_icons.disabled").getUnformattedText());
        }

        GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, screen.width, screen.height, -1, screen.mc.fontRendererObj);
    }

}

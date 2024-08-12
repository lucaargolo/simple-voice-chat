package de.maxhenkel.voicechat.gui.tooltips;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.widgets.ImageButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class HideGroupHudTooltipSupplier implements ImageButton.TooltipSupplier {

    private final GuiScreen screen;

    public HideGroupHudTooltipSupplier(GuiScreen screen) {
        this.screen = screen;
    }

    @Override
    public void onTooltip(ImageButton button, int mouseX, int mouseY) {
        List<String> tooltip = new ArrayList<>();

        if (VoicechatClient.CLIENT_CONFIG.showGroupHUD.get()) {
            tooltip.add(new ChatComponentTranslation("message.voicechat.show_group_hud.enabled").getUnformattedText());
        } else {
            tooltip.add(new ChatComponentTranslation("message.voicechat.show_group_hud.disabled").getUnformattedText());
        }

        GuiUtils.drawHoveringText(tooltip, mouseX, mouseY, screen.width, screen.height, -1, screen.mc.fontRendererObj);
    }

}

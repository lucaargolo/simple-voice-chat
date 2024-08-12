package de.maxhenkel.voicechat.gui.audiodevice;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.VoiceChatScreenBase;
import de.maxhenkel.voicechat.gui.widgets.ListScreenEntryBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class AudioDeviceEntry extends ListScreenEntryBase {

    protected static final ResourceLocation SELECTED = new ResourceLocation(Voicechat.MODID, "textures/icons/device_selected.png");

    protected static final int PADDING = 4;
    protected static final int BG_FILL = VoiceChatScreenBase.color(255, 74, 74, 74);
    protected static final int BG_FILL_HOVERED = VoiceChatScreenBase.color(255, 90, 90, 90);
    protected static final int BG_FILL_SELECTED = VoiceChatScreenBase.color(255, 40, 40, 40);
    protected static final int DEVICE_NAME_COLOR = VoiceChatScreenBase.color(255, 255, 255, 255);

    protected final Minecraft minecraft;
    protected final String device;
    protected final String visibleDeviceName;
    @Nullable
    protected final ResourceLocation icon;
    protected final Supplier<Boolean> isSelected;

    public AudioDeviceEntry(String device, String name, @Nullable ResourceLocation icon, Supplier<Boolean> isSelected) {
        this.device = device;
        this.icon = icon;
        this.isSelected = isSelected;
        this.visibleDeviceName = name;
        this.minecraft = Minecraft.getMinecraft();
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean hovered) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, hovered);
        boolean selected = isSelected.get();
        if (selected) {
            GuiScreen.drawRect(x, y, x + listWidth, y + slotHeight, BG_FILL_SELECTED);
        } else if (hovered) {
            GuiScreen.drawRect(x, y, x + listWidth, y + slotHeight, BG_FILL_HOVERED);
        } else {
            GuiScreen.drawRect(x, y, x + listWidth, y + slotHeight, BG_FILL);
        }
        GlStateManager.color(1F, 1F, 1F, 1F);

        if (icon != null) {
            minecraft.getTextureManager().bindTexture(icon);
            GuiScreen.drawModalRectWithCustomSizedTexture(x + PADDING, y + slotHeight / 2 - 8, 16, 16, 16, 16, 16, 16);
        }
        if (selected) {
            minecraft.getTextureManager().bindTexture(SELECTED);
            GuiScreen.drawModalRectWithCustomSizedTexture(x + PADDING, y + slotHeight / 2 - 8, 16, 16, 16, 16, 16, 16);
        }

        float deviceWidth = minecraft.fontRendererObj.getStringWidth(visibleDeviceName);
        float space = listWidth - PADDING - 16 - PADDING - PADDING;
        float scale = Math.min(space / deviceWidth, 1F);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x + PADDING + 16 + PADDING, y + slotHeight / 2 - (minecraft.fontRendererObj.FONT_HEIGHT * scale) / 2, 0D);
        GlStateManager.scale(scale, scale, 1F);

        minecraft.fontRendererObj.drawString(visibleDeviceName, 0, 0, DEVICE_NAME_COLOR);
        GlStateManager.popMatrix();
    }

    public String getDevice() {
        return device;
    }
}

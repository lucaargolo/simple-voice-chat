package de.maxhenkel.voicechat.gui.volume;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.VoiceChatScreenBase;
import de.maxhenkel.voicechat.gui.widgets.ListScreenEntryBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

public abstract class VolumeEntry extends ListScreenEntryBase {

    protected static final IChatComponent OTHER_VOLUME = new ChatComponentTranslation("message.voicechat.other_volume");
    protected static final IChatComponent OTHER_VOLUME_DESCRIPTION = new ChatComponentTranslation("message.voicechat.other_volume.description");
    protected static final ResourceLocation OTHER_VOLUME_ICON = new ResourceLocation(Voicechat.MODID, "textures/icons/other_volume.png");

    protected static final int SKIN_SIZE = 24;
    protected static final int PADDING = 4;
    protected static final int BG_FILL = VoiceChatScreenBase.color(255, 74, 74, 74);
    protected static final int PLAYER_NAME_COLOR = VoiceChatScreenBase.color(255, 255, 255, 255);

    protected final Minecraft minecraft;
    protected final AdjustVolumesScreen screen;
    protected final AdjustVolumeSlider volumeSlider;

    public VolumeEntry(AdjustVolumesScreen screen, AdjustVolumeSlider.VolumeConfigEntry entry) {
        this.minecraft = Minecraft.getMinecraft();
        this.screen = screen;
        this.volumeSlider = new AdjustVolumeSlider(0, 0, 0, 100, 20, entry);
        this.children.add(volumeSlider);
    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        super.drawEntry(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected);
        int skinX = x + PADDING;
        int skinY = y + (slotHeight - SKIN_SIZE) / 2;
        int textX = skinX + SKIN_SIZE + PADDING;
        int textY = y + (slotHeight - minecraft.fontRendererObj.FONT_HEIGHT) / 2;

        GuiScreen.drawRect(x, y, x + listWidth, y + slotHeight, BG_FILL);

        renderElement(slotIndex, x, y, listWidth, slotHeight, mouseX, mouseY, isSelected, skinX, skinY, textX, textY);

        volumeSlider.xPosition = x + (listWidth - volumeSlider.width - PADDING);
        volumeSlider.yPosition = y + (slotHeight - volumeSlider.height) / 2;
        volumeSlider.drawButton(minecraft, mouseX, mouseY);
    }

    public abstract void renderElement(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, int skinX, int skinY, int textX, int textY);

}

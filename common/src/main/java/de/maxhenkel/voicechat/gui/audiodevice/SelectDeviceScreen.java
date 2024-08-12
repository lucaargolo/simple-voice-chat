package de.maxhenkel.voicechat.gui.audiodevice;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.VoiceChatScreenBase;
import de.maxhenkel.voicechat.gui.widgets.ButtonBase;
import de.maxhenkel.voicechat.gui.widgets.IngameListScreenBase;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;
import java.util.List;

public abstract class SelectDeviceScreen extends IngameListScreenBase {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_audio_devices.png");
    protected static final IChatComponent BACK = new ChatComponentTranslation("message.voicechat.back");

    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 32;
    protected static final int UNIT_SIZE = 18;


    @Nullable
    protected GuiScreen parent;
    protected AudioDeviceList deviceList;
    protected ButtonBase back;
    protected int units;

    public SelectDeviceScreen(IChatComponent title, @Nullable GuiScreen parent) {
        super(title, 236, 0);
        this.parent = parent;
    }

    public abstract List<String> getDevices();

    public abstract ResourceLocation getIcon();

    public abstract IChatComponent getEmptyListComponent();

    public abstract ConfigEntry<String> getConfigEntry();

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = guiLeft + 2;
        guiTop = 32;
        int minUnits = MathHelper.ceiling_float_int((float) (AudioDeviceList.CELL_HEIGHT + 4) / (float) UNIT_SIZE);
        units = Math.max(minUnits, (height - HEADER_SIZE - FOOTER_SIZE - guiTop * 2) / UNIT_SIZE);
        ySize = HEADER_SIZE + units * UNIT_SIZE + FOOTER_SIZE;

        deviceList = new AudioDeviceList(width, units * UNIT_SIZE, guiTop + HEADER_SIZE).setIcon(getIcon()).setConfigEntry(getConfigEntry());
        setList(deviceList);

        back = new ButtonBase(0, guiLeft + 7, guiTop + ySize - 20 - 7, xSize - 14, 20, BACK) {
            @Override
            public void onPress() {
                mc.displayGuiScreen(parent);
            }
        };
        addButton(back);

        deviceList.setAudioDevices(getDevices());
    }

    @Override
    public void renderBackground(int mouseX, int mouseY, float delta) {
        if (isIngame()) {
            mc.getTextureManager().bindTexture(TEXTURE);
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, HEADER_SIZE);
            for (int i = 0; i < units; i++) {
                drawTexturedModalRect(guiLeft, guiTop + HEADER_SIZE + UNIT_SIZE * i, 0, HEADER_SIZE, xSize, UNIT_SIZE);
            }
            drawTexturedModalRect(guiLeft, guiTop + HEADER_SIZE + UNIT_SIZE * units, 0, HEADER_SIZE + UNIT_SIZE, xSize, FOOTER_SIZE);
            drawTexturedModalRect(guiLeft + 10, guiTop + HEADER_SIZE + 6 - 2, xSize, 0, 12, 12);
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, float delta) {
        fontRendererObj.drawString(title.getUnformattedText(), width / 2 - fontRendererObj.getStringWidth(title.getUnformattedText()) / 2, guiTop + 5, isIngame() ? VoiceChatScreenBase.FONT_COLOR : 0xFFFFFF);
        if (deviceList == null || deviceList.isEmpty()) {
            drawCenteredString(fontRendererObj, getEmptyListComponent().getUnformattedText(), width / 2, guiTop + HEADER_SIZE + (units * UNIT_SIZE) / 2 - fontRendererObj.FONT_HEIGHT / 2, -1);
        }
    }

}

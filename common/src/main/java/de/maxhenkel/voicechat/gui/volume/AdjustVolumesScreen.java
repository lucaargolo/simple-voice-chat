package de.maxhenkel.voicechat.gui.volume;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.VoiceChatScreenBase;
import de.maxhenkel.voicechat.gui.widgets.IngameListScreenBase;
import net.minecraft.client.gui.GuiPageButtonList;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.Locale;

public class AdjustVolumesScreen extends IngameListScreenBase {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_volumes.png");
    protected static final IChatComponent TITLE = new ChatComponentTranslation("gui.voicechat.adjust_volume.title");
    protected static final IChatComponent SEARCH_HINT = new ChatComponentTranslation("message.voicechat.search_hint").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true));
    protected static final IChatComponent EMPTY_SEARCH = new ChatComponentTranslation("message.voicechat.search_empty").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));

    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 8;
    protected static final int SEARCH_HEIGHT = 16;
    protected static final int UNIT_SIZE = 18;
    protected static final int CELL_HEIGHT = 36;

    protected AdjustVolumeList volumeList;
    protected GuiTextField searchBox;
    protected String lastSearch;
    protected int units;

    public AdjustVolumesScreen() {
        super(TITLE, 236, 0);
        this.lastSearch = "";
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        searchBox.updateCursorCounter();
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = guiLeft + 2;
        guiTop = 32;
        int minUnits = MathHelper.ceiling_float_int((float) (CELL_HEIGHT + SEARCH_HEIGHT + 4) / (float) UNIT_SIZE);
        units = Math.max(minUnits, (height - HEADER_SIZE - FOOTER_SIZE - guiTop * 2 - SEARCH_HEIGHT) / UNIT_SIZE);
        ySize = HEADER_SIZE + units * UNIT_SIZE + FOOTER_SIZE;

        Keyboard.enableRepeatEvents(true);

        volumeList = new AdjustVolumeList(width, units * UNIT_SIZE - SEARCH_HEIGHT, guiTop + HEADER_SIZE + SEARCH_HEIGHT, CELL_HEIGHT, this);

        String string = searchBox != null ? searchBox.getText() : "";
        searchBox = new GuiTextField(0, fontRendererObj, guiLeft + 28, guiTop + HEADER_SIZE + 6, 196, SEARCH_HEIGHT);
        searchBox.setMaxStringLength(16);
        searchBox.setEnableBackgroundDrawing(false);
        searchBox.setVisible(true);
        searchBox.setTextColor(0xFFFFFF);
        searchBox.setText(string);
        searchBox.func_175207_a(new GuiPageButtonList.GuiResponder() {
            @Override
            public void func_175321_a(int p_175321_1_, boolean p_175321_2_) {

            }

            @Override
            public void onTick(int id, float value) {

            }

            @Override
            public void func_175319_a(int p_175319_1_, String p_175319_2_) {
                checkSearchStringUpdate(p_175319_2_);
            }
        });
        setList(volumeList);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void renderBackground(int mouseX, int mouseY, float delta) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, HEADER_SIZE);
        for (int i = 0; i < units; i++) {
            drawTexturedModalRect(guiLeft, guiTop + HEADER_SIZE + UNIT_SIZE * i, 0, HEADER_SIZE, xSize, UNIT_SIZE);
        }
        drawTexturedModalRect(guiLeft, guiTop + HEADER_SIZE + UNIT_SIZE * units, 0, HEADER_SIZE + UNIT_SIZE, xSize, FOOTER_SIZE);
        drawTexturedModalRect(guiLeft + 10, guiTop + HEADER_SIZE + 6 - 2, xSize, 0, 12, 12);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, float delta) {
        fontRendererObj.drawString(TITLE.getFormattedText(), width / 2 - fontRendererObj.getStringWidth(TITLE.getUnformattedText()) / 2, guiTop + 5, VoiceChatScreenBase.FONT_COLOR);

        if (volumeList == null) {
            return;
        }

        if (!volumeList.isEmpty()) {
            volumeList.drawScreen(mouseX, mouseY, delta);
        } else if (!searchBox.getText().isEmpty()) {
            drawCenteredString(fontRendererObj, EMPTY_SEARCH.getFormattedText(), width / 2, guiTop + HEADER_SIZE + (units * UNIT_SIZE) / 2 - fontRendererObj.FONT_HEIGHT / 2, -1);
        }
        if (!searchBox.isFocused() && searchBox.getText().isEmpty()) {
            drawString(fontRendererObj, SEARCH_HINT.getFormattedText(), searchBox.xPosition, searchBox.yPosition, -1);
        } else {
            GlStateManager.disableLighting();
            GlStateManager.disableBlend();
            searchBox.drawTextBox();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (volumeList == null) {
            return;
        }
        searchBox.mouseClicked(mouseX, mouseY, mouseButton);
        volumeList.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (searchBox == null) {
            return;
        }
        searchBox.textboxKeyTyped(typedChar, keyCode);
    }

    private void checkSearchStringUpdate(String string) {
        if (!(string = string.toLowerCase(Locale.ROOT)).equals(lastSearch)) {
            volumeList.setFilter(string);
            lastSearch = string;
        }
    }

}
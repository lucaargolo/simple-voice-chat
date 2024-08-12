package de.maxhenkel.voicechat.gui;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.widgets.ButtonBase;
import de.maxhenkel.voicechat.net.CreateGroupPacket;
import de.maxhenkel.voicechat.net.NetManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class CreateGroupScreen extends VoiceChatScreenBase {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_create_group.png");
    private static final IChatComponent TITLE = new ChatComponentTranslation("gui.voicechat.create_group.title");
    private static final IChatComponent CREATE = new ChatComponentTranslation("message.voicechat.create");
    private static final IChatComponent CREATE_GROUP = new ChatComponentTranslation("message.voicechat.create_group");
    private static final IChatComponent GROUP_NAME = new ChatComponentTranslation("message.voicechat.group_name");
    private static final IChatComponent OPTIONAL_PASSWORD = new ChatComponentTranslation("message.voicechat.optional_password");
    private static final IChatComponent GROUP_TYPE = new ChatComponentTranslation("message.voicechat.group_type");

    private GuiTextField groupName;
    private GuiTextField password;
    private GroupType groupType;
    private ButtonBase groupTypeButton;
    private ButtonBase createGroup;

    public CreateGroupScreen() {
        super(TITLE, 195, 124);
        groupType = GroupType.NORMAL;
    }

    @Override
    public void initGui() {
        super.initGui();
        hoverAreas.clear();
        hoverAreas.clear();
        buttonList.clear();

        Keyboard.enableRepeatEvents(true);

        groupName = new GuiTextField(0, fontRendererObj, guiLeft + 7, guiTop + 32, xSize - 7 * 2, 10);
        groupName.setMaxStringLength(24);
        groupName.setValidator(s -> s.isEmpty() || Voicechat.GROUP_REGEX.matcher(s).matches());

        password = new GuiTextField(1, fontRendererObj, guiLeft + 7, guiTop + 58, xSize - 7 * 2, 10);
        password.setMaxStringLength(32);
        password.setValidator(s -> s.isEmpty() || Voicechat.GROUP_REGEX.matcher(s).matches());

        groupTypeButton = new ButtonBase(2, guiLeft + 6, guiTop + 71, xSize - 12, 20, GROUP_TYPE.getUnformattedText() + ": " + groupType.getTranslation().getUnformattedText()) {
            @Override
            public void onPress() {
                groupType = GroupType.values()[(groupType.ordinal() + 1) % GroupType.values().length];
                displayString = GROUP_TYPE.getUnformattedText() + ": " + groupType.getTranslation().getUnformattedText();
            }
        };
        addButton(groupTypeButton);

        createGroup = new ButtonBase(3, guiLeft + 6, guiTop + ySize - 27, xSize - 12, 20, CREATE) {
            @Override
            public void onPress() {
                createGroup();
            }
        };
        addButton(createGroup);
    }

    private void createGroup() {
        if (!groupName.getText().isEmpty()) {
            NetManager.sendToServer(new CreateGroupPacket(groupName.getText(), password.getText().isEmpty() ? null : password.getText(), groupType.getType()));
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (groupName == null) {
            return;
        }
        groupName.updateCursorCounter();
        password.updateCursorCounter();
        createGroup.enabled = !groupName.getText().isEmpty();
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void renderBackground(int mouseX, int mouseY, float delta) {
        mc.getTextureManager().bindTexture(TEXTURE);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, float delta) {
        if (groupName == null) {
            return;
        }
        groupName.drawTextBox();
        password.drawTextBox();
        fontRendererObj.drawString(CREATE_GROUP.getUnformattedText(), guiLeft + xSize / 2 - fontRendererObj.getStringWidth(CREATE_GROUP.getUnformattedText()) / 2, guiTop + 7, FONT_COLOR);
        fontRendererObj.drawString(GROUP_NAME.getUnformattedText(), guiLeft + 8, guiTop + 7 + fontRendererObj.FONT_HEIGHT + 5, FONT_COLOR);
        fontRendererObj.drawString(OPTIONAL_PASSWORD.getUnformattedText(), guiLeft + 8, guiTop + 7 + (fontRendererObj.FONT_HEIGHT + 5) * 2 + 10 + 2, FONT_COLOR);

        if (mouseX >= groupTypeButton.xPosition && mouseY >= groupTypeButton.yPosition && mouseX < groupTypeButton.xPosition + groupTypeButton.width && mouseY < groupTypeButton.yPosition + groupTypeButton.height) {
            drawHoveringText(mc.fontRendererObj.listFormattedStringToWidth(groupType.getDescription().getUnformattedText(), 200), mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        if (groupName == null) {
            return;
        }
        if (groupName.textboxKeyTyped(typedChar, keyCode) | password.textboxKeyTyped(typedChar, keyCode)) {
            return;
        }

        if (keyCode == Keyboard.KEY_RETURN) {
            createGroup();
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (groupName == null) {
            return;
        }
        groupName.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void onResize(Minecraft minecraft, int width, int height) {
        String groupNameText = groupName.getText();
        String passwordText = password.getText();
        super.onResize(minecraft, width, height);
        groupName.setText(groupNameText);
        password.setText(passwordText);
    }

}

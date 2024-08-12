package de.maxhenkel.voicechat.gui.group;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.CreateGroupScreen;
import de.maxhenkel.voicechat.gui.EnterPasswordScreen;
import de.maxhenkel.voicechat.gui.widgets.ButtonBase;
import de.maxhenkel.voicechat.gui.widgets.IngameListScreenBase;
import de.maxhenkel.voicechat.net.JoinGroupPacket;
import de.maxhenkel.voicechat.net.NetManager;
import de.maxhenkel.voicechat.voice.common.ClientGroup;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.*;

import java.io.IOException;

public class JoinGroupScreen extends IngameListScreenBase {

    protected static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_join_group.png");
    protected static final IChatComponent TITLE = new ChatComponentTranslation("gui.voicechat.join_create_group.title");
    protected static final IChatComponent CREATE_GROUP = new ChatComponentTranslation("message.voicechat.create_group_button");
    protected static final IChatComponent JOIN_CREATE_GROUP = new ChatComponentTranslation("message.voicechat.join_create_group");
    protected static final IChatComponent NO_GROUPS = new ChatComponentTranslation("message.voicechat.no_groups").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY));

    protected static final int HEADER_SIZE = 16;
    protected static final int FOOTER_SIZE = 32;
    protected static final int UNIT_SIZE = 18;
    protected static final int CELL_HEIGHT = 36;

    protected JoinGroupList groupList;
    protected ButtonBase createGroup;
    protected int units;

    public JoinGroupScreen() {
        super(TITLE, 236, 0);
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = guiLeft + 2;
        guiTop = 32;
        int minUnits = MathHelper.ceiling_float_int((float) (CELL_HEIGHT + 4) / (float) UNIT_SIZE);
        units = Math.max(minUnits, (height - HEADER_SIZE - FOOTER_SIZE - guiTop * 2) / UNIT_SIZE);
        ySize = HEADER_SIZE + units * UNIT_SIZE + FOOTER_SIZE;

        groupList = new JoinGroupList(this, width, units * UNIT_SIZE, guiTop + HEADER_SIZE, CELL_HEIGHT);
        setList(groupList);

        createGroup = new ButtonBase(0, guiLeft + 7, guiTop + ySize - 20 - 7, xSize - 14, 20, CREATE_GROUP) {
            @Override
            public void onPress() {
                mc.displayGuiScreen(new CreateGroupScreen());
            }
        };
        addButton(createGroup);
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
        fontRendererObj.drawString(JOIN_CREATE_GROUP.getFormattedText(), guiLeft + xSize / 2 - fontRendererObj.getStringWidth(JOIN_CREATE_GROUP.getUnformattedText()) / 2, guiTop + 5, FONT_COLOR);

        if (groupList != null && !groupList.isEmpty()) {
            groupList.drawScreen(mouseX, mouseY, delta);
        } else {
            drawCenteredString(fontRendererObj, NO_GROUPS.getUnformattedText(), width / 2, guiTop + HEADER_SIZE + (units * UNIT_SIZE) / 2 - fontRendererObj.FONT_HEIGHT / 2, -1);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) throws IOException {
        super.mouseClicked(mouseX, mouseY, button);
        if (groupList == null) {
            return;
        }
        for (JoinGroupEntry entry : groupList.children()) {
            if (entry.isSelected()) {
                ClientGroup group = entry.getGroup().getGroup();
                mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.F));
                if (group.hasPassword()) {
                    mc.displayGuiScreen(new EnterPasswordScreen(group));
                } else {
                    NetManager.sendToServer(new JoinGroupPacket(group.getId(), null));
                }
                return;
            }
        }
    }

}

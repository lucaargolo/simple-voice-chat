package de.maxhenkel.voicechat.gui;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.audiodevice.SelectMicrophoneScreen;
import de.maxhenkel.voicechat.gui.audiodevice.SelectSpeakerScreen;
import de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen;
import de.maxhenkel.voicechat.gui.widgets.*;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.ClientVoicechat;
import de.maxhenkel.voicechat.voice.client.KeyEvents;
import de.maxhenkel.voicechat.voice.client.MicrophoneActivationType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.IChatComponent;
import de.maxhenkel.voicechat.voice.client.speaker.AudioType;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collections;

public class VoiceChatSettingsScreen extends VoiceChatScreenBase {

    private static final ResourceLocation TEXTURE = new ResourceLocation(Voicechat.MODID, "textures/gui/gui_voicechat_settings.png");
    private static final IChatComponent TITLE = new ChatComponentTranslation("gui.voicechat.voice_chat_settings.title");

    private static final IChatComponent ASSIGN_TOOLTIP = new ChatComponentTranslation("message.voicechat.press_to_reassign_key");
    private static final IChatComponent PUSH_TO_TALK = new ChatComponentTranslation("message.voicechat.activation_type.ptt");
    private static final IChatComponent ADJUST_VOLUMES = new ChatComponentTranslation("message.voicechat.adjust_volumes");
    private static final IChatComponent SELECT_MICROPHONE = new ChatComponentTranslation("message.voicechat.select_microphone");
    private static final IChatComponent SELECT_SPEAKER = new ChatComponentTranslation("message.voicechat.select_speaker");
    private static final IChatComponent BACK = new ChatComponentTranslation("message.voicechat.back");

    @Nullable
    private final GuiScreen parent;
    private VoiceActivationSlider voiceActivationSlider;
    private MicTestButton micTestButton;
    private KeybindButton keybindButton;

    public VoiceChatSettingsScreen(@Nullable GuiScreen parent) {
        super(TITLE, 248, 219);
        this.parent = parent;
    }

    public VoiceChatSettingsScreen() {
        this(null);
    }

    @Override
    public void initGui() {
        super.initGui();

        int y = guiTop + 20;

        addButton(new VoiceSoundSlider(0, guiLeft + 10, y, xSize - 20, 20));
        y += 21;
        addButton(new MicAmplificationSlider(1, guiLeft + 10, y, xSize - 20, 20));
        y += 21;
        addButton(new DenoiserButton(2, guiLeft + 10, y, xSize - 20, 20));
        y += 21;

        voiceActivationSlider = new VoiceActivationSlider(5, guiLeft + 10 + 20 + 1, y + 21, xSize - 20 - 20 - 1, 20);
        micTestButton = new MicTestButton(4, guiLeft + 10, y + 21, voiceActivationSlider);
        keybindButton = new KeybindButton(6, KeyEvents.KEY_PTT, guiLeft + 10 + 20 + 1, y + 21, xSize - 20 - 20 - 1, 20, PUSH_TO_TALK);
        addButton(new MicActivationButton(3, guiLeft + 10, y, xSize - 20, 20, type -> {
            voiceActivationSlider.visible = MicrophoneActivationType.VOICE.equals(type);
            keybindButton.visible = MicrophoneActivationType.PTT.equals(type);
        }));
        addButton(micTestButton);
        addButton(voiceActivationSlider);
        addButton(keybindButton);
        y += 21 * 2;

        addButton(new EnumButton<AudioType>(7, guiLeft + 10, y, xSize - 20, 20, VoicechatClient.CLIENT_CONFIG.audioType) {

            @Override
            protected IChatComponent getText(AudioType type) {
                return new ChatComponentTranslation("message.voicechat.audio_type", type.getText());
            }

            @Override
            protected void onUpdate(AudioType type) {
                ClientVoicechat client = ClientManager.getClient();
                if (client != null) {
                    micTestButton.stop();
                    client.reloadAudio();
                }
            }
        });
        y += 21;
        if (isIngame()) {
            addButton(new ButtonBase(8, guiLeft + 10, y, xSize - 20, 20, ADJUST_VOLUMES) {
                @Override
                public void onPress() {
                    mc.displayGuiScreen(new AdjustVolumesScreen());
                }
            });
            y += 21;
        }
        addButton(new ButtonBase(9, guiLeft + 10, y, xSize / 2 - 15, 20, SELECT_MICROPHONE) {
            @Override
            public void onPress() {
                mc.displayGuiScreen(new SelectMicrophoneScreen(VoiceChatSettingsScreen.this));
            }
        });
        addButton(new ButtonBase(10, guiLeft + xSize / 2 + 1, y, (xSize - 20) / 2 - 1, 20, SELECT_SPEAKER) {
            @Override
            public void onPress() {
                mc.displayGuiScreen(new SelectSpeakerScreen(VoiceChatSettingsScreen.this));
            }
        });
        y += 21;
        if (!isIngame() && parent != null) {
            addButton(new ButtonBase(11, guiLeft + 10, y, xSize - 20, 20, BACK) {
                @Override
                public void onPress() {
                    mc.displayGuiScreen(parent);
                }
            });
        }
    }

    @Override
    public void renderBackground(int mouseX, int mouseY, float delta) {
        mc.getTextureManager().bindTexture(TEXTURE);
        if (isIngame()) {
            drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
        }
    }

    @Override
    public void renderForeground(int mouseX, int mouseY, float delta) {
        int titleWidth = fontRendererObj.getStringWidth(TITLE.getUnformattedText());
        fontRendererObj.drawString(TITLE.getFormattedText(), guiLeft + (xSize - titleWidth) / 2, guiTop + 7, getFontColor());

        if (voiceActivationSlider == null) {
            return;
        }

        IChatComponent sliderTooltip = voiceActivationSlider.getHoverText();
        if (voiceActivationSlider.isHovered() && sliderTooltip != null) {
            drawHoveringText(Collections.singletonList(sliderTooltip.getFormattedText()), mouseX, mouseY);
        } else if (micTestButton.isHovered()) {
            micTestButton.onTooltip(micTestButton, mouseX, mouseY);
        } else if (keybindButton.isHovered()) {
            drawHoveringText(Collections.singletonList(ASSIGN_TOOLTIP.getFormattedText()), mouseX, mouseY);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keybindButton.keyPressed(keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (keybindButton.mousePressed(mouseButton)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }
}

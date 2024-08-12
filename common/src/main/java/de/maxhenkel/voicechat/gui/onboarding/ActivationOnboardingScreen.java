package de.maxhenkel.voicechat.gui.onboarding;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.widgets.ButtonBase;
import de.maxhenkel.voicechat.voice.client.MicrophoneActivationType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;

public class ActivationOnboardingScreen extends OnboardingScreenBase {

    private static final IChatComponent TITLE = new ChatComponentTranslation("message.voicechat.onboarding.activation.title").setChatStyle(new ChatStyle().setBold(true));
    private static final IChatComponent DESCRIPTION = new ChatComponentTranslation("message.voicechat.onboarding.activation")
            .appendText("\n\n")
            .appendSibling(new ChatComponentTranslation("message.voicechat.onboarding.activation.ptt", new ChatComponentTranslation("message.voicechat.onboarding.activation.ptt.name").setChatStyle(new ChatStyle().setBold(true).setUnderlined(true))))
            .appendText("\n\n")
            .appendSibling(new ChatComponentTranslation("message.voicechat.onboarding.activation.voice", new ChatComponentTranslation("message.voicechat.onboarding.activation.voice.name").setChatStyle(new ChatStyle().setBold(true).setUnderlined(true))));

    public ActivationOnboardingScreen(@Nullable GuiScreen previous) {
        super(TITLE, previous);
    }

    @Override
    public void initGui() {
        super.initGui();

        ButtonBase ptt = new ButtonBase(0, guiLeft, guiTop + contentHeight - BUTTON_HEIGHT * 2 - PADDING, contentWidth / 2 - PADDING / 2, BUTTON_HEIGHT, new ChatComponentTranslation("message.voicechat.onboarding.activation.ptt.name")) {
            @Override
            public void onPress() {
                VoicechatClient.CLIENT_CONFIG.microphoneActivationType.set(MicrophoneActivationType.PTT).save();
                mc.displayGuiScreen(new PttOnboardingScreen(ActivationOnboardingScreen.this));
            }
        };
        addButton(ptt);

        ButtonBase voice = new ButtonBase(1, guiLeft + contentWidth / 2 + PADDING / 2, guiTop + contentHeight - BUTTON_HEIGHT * 2 - PADDING, contentWidth / 2 - PADDING / 2, BUTTON_HEIGHT, new ChatComponentTranslation("message.voicechat.onboarding.activation.voice.name")) {
            @Override
            public void onPress() {
                VoicechatClient.CLIENT_CONFIG.microphoneActivationType.set(MicrophoneActivationType.VOICE).save();
                mc.displayGuiScreen(new VoiceActivationOnboardingScreen(ActivationOnboardingScreen.this));
            }
        };
        addButton(voice);

        addBackOrCancelButton(2, true);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderTitle(TITLE);
        renderMultilineText(DESCRIPTION);
    }
}

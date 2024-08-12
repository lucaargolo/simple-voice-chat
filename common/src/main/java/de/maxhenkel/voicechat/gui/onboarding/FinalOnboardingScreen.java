package de.maxhenkel.voicechat.gui.onboarding;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.VoiceChatScreen;
import de.maxhenkel.voicechat.intercompatibility.ClientCompatibilityManager;
import de.maxhenkel.voicechat.voice.client.KeyEvents;
import de.maxhenkel.voicechat.voice.client.MicrophoneActivationType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;
import java.io.IOException;

public class FinalOnboardingScreen extends OnboardingScreenBase {

    private static final IChatComponent TITLE = new ChatComponentTranslation("message.voicechat.onboarding.final").setChatStyle(new ChatStyle().setBold(true));
    private static final IChatComponent FINISH_SETUP = new ChatComponentTranslation("message.voicechat.onboarding.final.finish_setup");

    protected IChatComponent description;

    public FinalOnboardingScreen(@Nullable GuiScreen previous) {
        super(TITLE, previous);
        description = new ChatComponentText("");
    }

    @Override
    public void initGui() {
        super.initGui();

        IChatComponent text = new ChatComponentTranslation("message.voicechat.onboarding.final.description.success",
                new ChatComponentText(GameSettings.getKeyDisplayString(KeyEvents.KEY_VOICE_CHAT.getKeyCode())).setChatStyle(new ChatStyle().setBold(true).setUnderlined(true))
        ).appendText("\n\n");

        if (VoicechatClient.CLIENT_CONFIG.microphoneActivationType.get().equals(MicrophoneActivationType.PTT)) {
            text = text.appendSibling(new ChatComponentTranslation("message.voicechat.onboarding.final.description.ptt",
                    new ChatComponentText(GameSettings.getKeyDisplayString(KeyEvents.KEY_PTT.getKeyCode())).setChatStyle(new ChatStyle().setBold(true).setUnderlined(true))
            ).setChatStyle(new ChatStyle().setBold(true))).appendText("\n\n");
        } else {
            text = text.appendSibling(new ChatComponentTranslation("message.voicechat.onboarding.final.description.voice",
                    new ChatComponentText(GameSettings.getKeyDisplayString(KeyEvents.KEY_MUTE.getKeyCode())).setChatStyle(new ChatStyle().setBold(true).setUnderlined(true))
            ).setChatStyle(new ChatStyle().setBold(true))).appendText("\n\n");
        }

        description = text.appendSibling(new ChatComponentTranslation("message.voicechat.onboarding.final.description.configuration"));

        addBackOrCancelButton(0);
        addPositiveButton(1, FINISH_SETUP, button -> OnboardingManager.finishOnboarding());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderTitle(TITLE);
        renderMultilineText(description);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keyCode == Keyboard.KEY_ESCAPE) {
            OnboardingManager.finishOnboarding();
            return;
        }
        if (keyCode == ClientCompatibilityManager.INSTANCE.getBoundKeyOf(KeyEvents.KEY_VOICE_CHAT)) {
            OnboardingManager.finishOnboarding();
            mc.displayGuiScreen(new VoiceChatScreen());
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

}

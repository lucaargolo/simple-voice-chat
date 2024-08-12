package de.maxhenkel.voicechat.gui.onboarding;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.voice.client.ChatUtils;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import de.maxhenkel.voicechat.voice.client.KeyEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;

import javax.annotation.Nullable;

public class OnboardingManager {

    private static final Minecraft MC = Minecraft.getMinecraft();

    public static boolean isOnboarding() {
        return !VoicechatClient.CLIENT_CONFIG.onboardingFinished.get();
    }

    public static void startOnboarding(@Nullable GuiScreen parent) {
        MC.displayGuiScreen(getOnboardingScreen(parent));
    }

    public static GuiScreen getOnboardingScreen(@Nullable GuiScreen parent) {
        return new IntroductionOnboardingScreen(parent);
    }

    public static void finishOnboarding() {
        VoicechatClient.CLIENT_CONFIG.muted.set(true).save();
        VoicechatClient.CLIENT_CONFIG.disabled.set(false).save();
        VoicechatClient.CLIENT_CONFIG.onboardingFinished.set(true).save();
        ClientManager.getPlayerStateManager().onFinishOnboarding();
        MC.displayGuiScreen(null);
    }

    public static void onConnecting() {
        if (!isOnboarding()) {
            return;
        }
        ChatUtils.sendModMessage(new ChatComponentTranslation("message.voicechat.set_up",
                new ChatComponentText(GameSettings.getKeyDisplayString(KeyEvents.KEY_VOICE_CHAT.getKeyCode())).setChatStyle(new ChatStyle().setBold(true).setUnderlined(true))
        ));
    }
}

package de.maxhenkel.voicechat.voice.client;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.gui.VoiceChatScreen;
import de.maxhenkel.voicechat.gui.VoiceChatSettingsScreen;
import de.maxhenkel.voicechat.gui.group.GroupScreen;
import de.maxhenkel.voicechat.gui.group.JoinGroupScreen;
import de.maxhenkel.voicechat.gui.onboarding.OnboardingManager;
import de.maxhenkel.voicechat.gui.volume.AdjustVolumesScreen;
import de.maxhenkel.voicechat.intercompatibility.ClientCompatibilityManager;
import de.maxhenkel.voicechat.voice.common.ClientGroup;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

public class KeyEvents {

    private final Minecraft minecraft;

    public static KeyBinding KEY_PTT;
    public static KeyBinding KEY_WHISPER;
    public static KeyBinding KEY_MUTE;
    public static KeyBinding KEY_DISABLE;
    public static KeyBinding KEY_HIDE_ICONS;
    public static KeyBinding KEY_VOICE_CHAT;
    public static KeyBinding KEY_VOICE_CHAT_SETTINGS;
    public static KeyBinding KEY_GROUP;
    public static KeyBinding KEY_TOGGLE_RECORDING;
    public static KeyBinding KEY_ADJUST_VOLUMES;

    public static KeyBinding[] ALL_KEYS;

    public KeyEvents() {
        minecraft = Minecraft.getInstance();
        ClientCompatibilityManager.INSTANCE.onHandleKeyBinds(this::handleKeybinds);

        KEY_PTT = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.push_to_talk", InputMappings.UNKNOWN.getValue(), "key.categories.voicechat"));
        KEY_WHISPER = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.whisper", InputMappings.UNKNOWN.getValue(), "key.categories.voicechat"));
        KEY_MUTE = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.mute_microphone", GLFW.GLFW_KEY_M, "key.categories.voicechat"));
        KEY_DISABLE = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.disable_voice_chat", GLFW.GLFW_KEY_N, "key.categories.voicechat"));
        KEY_HIDE_ICONS = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.hide_icons", GLFW.GLFW_KEY_H, "key.categories.voicechat"));
        KEY_VOICE_CHAT = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.voice_chat", GLFW.GLFW_KEY_V, "key.categories.voicechat"));
        KEY_VOICE_CHAT_SETTINGS = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.voice_chat_settings", InputMappings.UNKNOWN.getValue(), "key.categories.voicechat"));
        KEY_GROUP = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.voice_chat_group", GLFW.GLFW_KEY_G, "key.categories.voicechat"));
        KEY_TOGGLE_RECORDING = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.voice_chat_toggle_recording", InputMappings.UNKNOWN.getValue(), "key.categories.voicechat"));
        KEY_ADJUST_VOLUMES = ClientCompatibilityManager.INSTANCE.registerKeyBinding(new KeyBinding("key.voice_chat_adjust_volumes", InputMappings.UNKNOWN.getValue(), "key.categories.voicechat"));

        ALL_KEYS = new KeyBinding[]{
                KEY_PTT, KEY_WHISPER, KEY_MUTE, KEY_DISABLE, KEY_HIDE_ICONS, KEY_VOICE_CHAT, KEY_VOICE_CHAT_SETTINGS, KEY_GROUP, KEY_TOGGLE_RECORDING, KEY_ADJUST_VOLUMES
        };
    }

    private void handleKeybinds() {
        ClientPlayerEntity player = minecraft.player;
        if (player == null) {
            return;
        }
        if (OnboardingManager.isOnboarding()) {
            for (KeyBinding allKey : ALL_KEYS) {
                if (allKey.consumeClick()) {
                    OnboardingManager.startOnboarding(null);
                    return;
                }
            }
            return;
        }

        ClientVoicechat client = ClientManager.getClient();
        ClientPlayerStateManager playerStateManager = ClientManager.getPlayerStateManager();
        if (KEY_VOICE_CHAT.consumeClick()) {
            if (Screen.hasAltDown()) {
                if (Screen.hasControlDown()) {
                    VoicechatClient.CLIENT_CONFIG.onboardingFinished.set(false).save();
                    player.displayClientMessage(new TranslationTextComponent("message.voicechat.onboarding.reset"), true);
                } else {
                    ClientManager.getDebugOverlay().toggle();
                }
            } else {
                minecraft.setScreen(new VoiceChatScreen());
            }
        }

        if (KEY_GROUP.consumeClick()) {
            if (client != null && client.getConnection() != null && client.getConnection().getData().groupsEnabled()) {
                ClientGroup group = playerStateManager.getGroup();
                if (group != null) {
                    minecraft.setScreen(new GroupScreen(group));
                } else {
                    minecraft.setScreen(new JoinGroupScreen());
                }
            } else {
                player.displayClientMessage(new TranslationTextComponent("message.voicechat.groups_disabled"), true);
            }
        }

        if (KEY_VOICE_CHAT_SETTINGS.consumeClick()) {
            minecraft.setScreen(new VoiceChatSettingsScreen());
        }

        if (KEY_ADJUST_VOLUMES.consumeClick()) {
            minecraft.setScreen(new AdjustVolumesScreen());
        }

        if (KEY_PTT.consumeClick()) {
            checkConnected();
        }

        if (KEY_WHISPER.consumeClick()) {
            checkConnected();
        }

        if (KEY_MUTE.consumeClick()) {
            playerStateManager.setMuted(!playerStateManager.isMuted());
        }

        if (KEY_DISABLE.consumeClick()) {
            playerStateManager.setDisabled(!playerStateManager.isDisabled());
        }

        if (KEY_TOGGLE_RECORDING.consumeClick() && client != null) {
            ClientManager.getClient().toggleRecording();
        }

        if (KEY_HIDE_ICONS.consumeClick()) {
            boolean hidden = !VoicechatClient.CLIENT_CONFIG.hideIcons.get();
            VoicechatClient.CLIENT_CONFIG.hideIcons.set(hidden).save();

            if (hidden) {
                player.displayClientMessage(new TranslationTextComponent("message.voicechat.icons_hidden"), true);
            } else {
                player.displayClientMessage(new TranslationTextComponent("message.voicechat.icons_visible"), true);
            }
        }
    }

    private boolean checkConnected() {
        if (ClientManager.getClient() == null || ClientManager.getClient().getConnection() == null || !ClientManager.getClient().getConnection().isInitialized()) {
            sendNotConnectedMessage();
            return false;
        }
        return true;
    }

    private void sendNotConnectedMessage() {
        ClientPlayerEntity player = minecraft.player;
        if (player == null) {
            Voicechat.LOGGER.warn("Voice chat not connected");
            return;
        }
        player.displayClientMessage(new TranslationTextComponent("message.voicechat.voice_chat_not_connected"), true);
    }

}

package de.maxhenkel.voicechat.gui.widgets;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.voice.common.Utils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.*;

import javax.annotation.Nullable;

public class VoiceActivationSlider extends DebouncedSlider implements MicTestButton.MicListener {

    private static final ResourceLocation SLIDER = new ResourceLocation(Voicechat.MODID, "textures/gui/voice_activation_slider.png");
    private static final IChatComponent NO_ACTIVATION = new ChatComponentTranslation("message.voicechat.voice_activation.disabled").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));

    private double micValue;

    public VoiceActivationSlider(int id, int x, int y, int width, int height) {
        super(id, x, y, width, height, Utils.dbToPerc(VoicechatClient.CLIENT_CONFIG.voiceActivationThreshold.get().floatValue()));
        updateMessage();
    }

    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
        super.mouseDragged(mc, mouseX, mouseY);
        mc.getTextureManager().bindTexture(SLIDER);
        GlStateManager.color(1F, 1F, 1F, 1F);
        int width = (int) ((getButtonWidth() - 2) * micValue);
        drawTexturedModalRect(xPosition + 1, yPosition + 1, 0, 0, width, 18);
    }

    @Override
    protected void updateMessage() {
        long db = Math.round(Utils.percToDb(value));
        ChatComponentTranslation component = new ChatComponentTranslation("message.voicechat.voice_activation", db);

        if (db >= -10L) {
            component.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED));
        }

        displayString = component.getFormattedText();
    }

    @Nullable
    public IChatComponent getHoverText() {
        if (value >= 1D) {
            return NO_ACTIVATION;
        }
        return null;
    }

    @Override
    public void applyDebounced() {
        VoicechatClient.CLIENT_CONFIG.voiceActivationThreshold.set(Utils.percToDb(value)).save();
    }

    @Override
    public void onMicValue(double percentage) {
        this.micValue = percentage;
    }

    public boolean isHovered() {
        return hovered;
    }

}

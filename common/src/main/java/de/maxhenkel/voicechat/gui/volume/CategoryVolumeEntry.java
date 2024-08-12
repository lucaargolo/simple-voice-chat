package de.maxhenkel.voicechat.gui.volume;

import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.plugins.impl.VolumeCategoryImpl;
import de.maxhenkel.voicechat.voice.client.ClientManager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.Collections;

public class CategoryVolumeEntry extends VolumeEntry {

    protected final VolumeCategoryImpl category;
    protected final ResourceLocation texture;

    public CategoryVolumeEntry(VolumeCategoryImpl category, AdjustVolumesScreen screen) {
        super(screen, new CategoryVolumeConfigEntry(category.getId()));
        this.category = category;
        this.texture = ClientManager.getCategoryManager().getTexture(category.getId(), OTHER_VOLUME_ICON);
    }

    public VolumeCategoryImpl getCategory() {
        return category;
    }

    @Override
    public void renderElement(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected, int skinX, int skinY, int textX, int textY) {
        GlStateManager.color(1F, 1F, 1F, 1F);
        minecraft.getTextureManager().bindTexture(texture);
        Gui.drawScaledCustomSizeModalRect(skinX, skinY, 16, 16, 16, 16, SKIN_SIZE, SKIN_SIZE, 16, 16);
        minecraft.fontRendererObj.drawString(category.getName(), textX, textY, PLAYER_NAME_COLOR);
        if (isSelected && category.getDescription() != null) {
            screen.postRender(() -> {
                GuiUtils.drawHoveringText(Collections.singletonList(category.getDescription()), mouseX, mouseY, screen.width, screen.height, -1, screen.mc.fontRendererObj);
            });
        }
    }

    private static class CategoryVolumeConfigEntry implements AdjustVolumeSlider.VolumeConfigEntry {

        private final String category;

        public CategoryVolumeConfigEntry(String category) {
            this.category = category;
        }

        @Override
        public void save(double value) {
            VoicechatClient.VOLUME_CONFIG.setCategoryVolume(category, value);
            VoicechatClient.VOLUME_CONFIG.save();
        }

        @Override
        public double get() {
            return VoicechatClient.VOLUME_CONFIG.getCategoryVolume(category);
        }
    }

}

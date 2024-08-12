package de.maxhenkel.voicechat.gui.widgets;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.IChatComponent;

public abstract class ButtonBase extends GuiButton {

    public ButtonBase(int id, int x, int y, int width, int height, IChatComponent text) {
        super(id, x, y, width, height, text.getUnformattedText());
    }

    public ButtonBase(int id, int x, int y, int width, int height, String text) {
        super(id, x, y, width, height, text);
    }

    public abstract void onPress();

    public void renderTooltips(int mouseX, int mouseY, float delta) {

    }

}

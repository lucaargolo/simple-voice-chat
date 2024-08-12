package de.maxhenkel.voicechat.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;

import javax.annotation.Nullable;

public class KeybindButton extends ButtonBase {

    private static final Minecraft mc = Minecraft.getMinecraft();

    protected KeyBinding keyMapping;
    @Nullable
    protected IChatComponent description;
    protected boolean listening;

    public KeybindButton(int id, KeyBinding mapping, int x, int y, int width, int height, @Nullable IChatComponent description) {
        super(id, x, y, width, height, "");
        this.keyMapping = mapping;
        this.description = description;
        updateText();
    }

    public KeybindButton(int id, KeyBinding mapping, int x, int y, int width, int height) {
        this(id, mapping, x, y, width, height, null);
    }

    protected void updateText() {
        IChatComponent text;
        if (listening) {
            text = new ChatComponentText("> ").appendSibling(getText(keyMapping).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.BOLD).setColor(EnumChatFormatting.UNDERLINE))).appendText(" <").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.YELLOW));
        } else {
            text = getText(keyMapping);
        }

        if (description != null) {
            text = new ChatComponentText("").appendSibling(description).appendText(": ").appendSibling(text);
        }

        displayString = text.getFormattedText();
    }

    private static IChatComponent getText(KeyBinding keyMapping) {
        return new ChatComponentText(GameSettings.getKeyDisplayString(keyMapping.getKeyCode()));
    }

    public boolean isHovered() {
        return hovered;
    }

    @Override
    public void onPress() {
        listening = true;
        updateText();
    }

    public boolean mousePressed(int button) {
        if (listening) {
            setKeyBind(button - 100);
            listening = false;
            updateText();
            return true;
        }
        return false;
    }

    public boolean keyPressed(int key) {
        if (listening) {
            if (key == Keyboard.KEY_ESCAPE) {
                setKeyBind(Keyboard.KEY_NONE);
            } else {
                setKeyBind(key);
            }
            listening = false;
            updateText();
            return true;
        }
        return false;
    }

    protected void setKeyBind(int key) {
        mc.gameSettings.setOptionKeyBinding(keyMapping, key);
        KeyBinding.resetKeyBindingArrayAndHash();
    }

    public boolean isListening() {
        return listening;
    }
}

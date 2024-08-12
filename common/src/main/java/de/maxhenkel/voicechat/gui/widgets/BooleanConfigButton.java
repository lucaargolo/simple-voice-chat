package de.maxhenkel.voicechat.gui.widgets;

import de.maxhenkel.configbuilder.entry.ConfigEntry;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentText;

import java.util.function.Function;

public class BooleanConfigButton extends ButtonBase {

    protected ConfigEntry<Boolean> entry;
    protected Function<Boolean, IChatComponent> component;

    public BooleanConfigButton(int id, int x, int y, int width, int height, ConfigEntry<Boolean> entry, Function<Boolean, IChatComponent> component) {
        super(id, x, y, width, height, new ChatComponentText(""));
        this.entry = entry;
        this.component = component;
        updateText();
    }

    private void updateText() {
        displayString = component.apply(entry.get()).getUnformattedText();
    }

    @Override
    public void onPress() {
        entry.set(!entry.get()).save();
        updateText();
    }

}

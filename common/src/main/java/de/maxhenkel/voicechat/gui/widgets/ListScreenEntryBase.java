package de.maxhenkel.voicechat.gui.widgets;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;

import java.util.List;

public abstract class ListScreenEntryBase implements GuiListExtended.IGuiListEntry {

    protected final Minecraft mc;
    protected final List<GuiButton> children;
    protected boolean selected;

    public ListScreenEntryBase() {
        this.children = Lists.newArrayList();
        mc = Minecraft.getMinecraft();
    }

    public List<? extends GuiButton> children() {
        return children;
    }

    @Override
    public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {

    }

    @Override
    public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
        selected = isSelected;
    }

    @Override
    public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
        for (GuiButton button : children) {
            if (button.mousePressed(mc, mouseX, mouseY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
        for (GuiButton button : children) {
            button.mouseReleased(x, y);
        }
    }

    public boolean isSelected() {
        return selected;
    }
}

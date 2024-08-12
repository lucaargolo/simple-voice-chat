package de.maxhenkel.voicechat.gui.onboarding;

import de.maxhenkel.voicechat.gui.widgets.KeybindButton;
import de.maxhenkel.voicechat.voice.client.KeyEvents;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;
import java.io.IOException;

public class PttOnboardingScreen extends OnboardingScreenBase {

    private static final IChatComponent TITLE = new ChatComponentTranslation("message.voicechat.onboarding.ptt.title").setChatStyle(new ChatStyle().setBold(true));
    private static final IChatComponent DESCRIPTION = new ChatComponentTranslation("message.voicechat.onboarding.ptt.description");
    private static final IChatComponent BUTTON_DESCRIPTION = new ChatComponentTranslation("message.voicechat.onboarding.ptt.button_description");

    protected KeybindButton keybindButton;

    protected int keybindButtonPos;

    public PttOnboardingScreen(@Nullable GuiScreen previous) {
        super(TITLE, previous);
    }

    @Override
    public void initGui() {
        super.initGui();

        keybindButtonPos = guiTop + contentHeight - BUTTON_HEIGHT * 3 - PADDING * 2 - 40;
        keybindButton = new KeybindButton(0, KeyEvents.KEY_PTT, guiLeft + 40, keybindButtonPos, contentWidth - 40 * 2, BUTTON_HEIGHT);
        addButton(keybindButton);

        addBackOrCancelButton(1);
        addNextButton(2);
    }

    @Override
    public GuiScreen getNextScreen() {
        return new FinalOnboardingScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderTitle(TITLE);
        renderMultilineText(DESCRIPTION);
        fontRendererObj.drawStringWithShadow(BUTTON_DESCRIPTION.getFormattedText(), width / 2 - fontRendererObj.getStringWidth(BUTTON_DESCRIPTION.getUnformattedText()) / 2, keybindButtonPos - fontRendererObj.FONT_HEIGHT - PADDING, TEXT_COLOR);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (keybindButton.keyPressed(keyCode)) {
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (keybindButton.mousePressed(mouseButton)) {
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

}

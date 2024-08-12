package de.maxhenkel.voicechat.gui.onboarding;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ChatComponentTranslation;

import javax.annotation.Nullable;

public class SkipOnboardingScreen extends OnboardingScreenBase {

    private static final IChatComponent TITLE = new ChatComponentTranslation("message.voicechat.onboarding.skip.title").setChatStyle(new ChatStyle().setBold(true));
    private static final IChatComponent DESCRIPTION = new ChatComponentTranslation("message.voicechat.onboarding.skip.description");
    private static final IChatComponent CONFIRM = new ChatComponentTranslation("message.voicechat.onboarding.confirm");

    public SkipOnboardingScreen(@Nullable GuiScreen previous) {
        super(TITLE, previous);
    }

    @Override
    public void initGui() {
        super.initGui();

        addBackOrCancelButton(0);
        addPositiveButton(1, CONFIRM, button -> OnboardingManager.finishOnboarding());
    }

    @Override
    public GuiScreen getNextScreen() {
        return previous;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderTitle(TITLE);
        renderMultilineText(DESCRIPTION);
    }

}

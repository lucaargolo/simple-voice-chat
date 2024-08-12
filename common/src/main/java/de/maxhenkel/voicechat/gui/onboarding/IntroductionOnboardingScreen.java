package de.maxhenkel.voicechat.gui.onboarding;

import de.maxhenkel.voicechat.gui.widgets.ButtonBase;
import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;

import javax.annotation.Nullable;

public class IntroductionOnboardingScreen extends OnboardingScreenBase {

    private static final IChatComponent TITLE = new ChatComponentTranslation("message.voicechat.onboarding.introduction.title", CommonCompatibilityManager.INSTANCE.getModName()).setChatStyle(new ChatStyle().setBold(true));
    private static final IChatComponent DESCRIPTION = new ChatComponentTranslation("message.voicechat.onboarding.introduction.description");
    private static final IChatComponent SKIP = new ChatComponentTranslation("message.voicechat.onboarding.introduction.skip");

    public IntroductionOnboardingScreen(@Nullable GuiScreen previous) {
        super(TITLE, previous);
    }

    public IntroductionOnboardingScreen() {
        this(null);
    }

    @Override
    public void initGui() {
        super.initGui();

        ButtonBase skipButton = new ButtonBase(0, guiLeft, guiTop + contentHeight - BUTTON_HEIGHT * 2 - PADDING, contentWidth, BUTTON_HEIGHT, SKIP) {
            @Override
            public void onPress() {
                mc.displayGuiScreen(new SkipOnboardingScreen(IntroductionOnboardingScreen.this));
            }
        };

        addButton(skipButton);

        addBackOrCancelButton(1);
        addNextButton(2);
    }

    @Override
    public GuiScreen getNextScreen() {
        return new MicOnboardingScreen(this);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderTitle(TITLE);
        renderMultilineText(DESCRIPTION);
    }

}

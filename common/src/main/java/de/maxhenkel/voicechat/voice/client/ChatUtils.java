package de.maxhenkel.voicechat.voice.client;

import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.*;
import javax.annotation.Nullable;

public class ChatUtils {

    public static void sendPlayerError(String translationKey, @Nullable Exception e) {
        ChatStyle style = new ChatStyle().setColor(EnumChatFormatting.RED);
        if (e != null) {
            style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(e.getMessage()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED))));
        }
        IChatComponent message = wrapInSquareBrackets(new ChatComponentText(CommonCompatibilityManager.INSTANCE.getModName()))
                .setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                .appendText(" ")
                .appendSibling(new ChatComponentTranslation(translationKey).setChatStyle(style));
        sendPlayerMessage(message);
    }

    private static IChatComponent wrapInSquareBrackets(IChatComponent component) {
        return new ChatComponentText("[").appendSibling(component).appendText("]");
    }

    public static void sendModMessage(IChatComponent message) {
        sendPlayerMessage(createModMessage(message));
    }

    public static IChatComponent createModMessage(IChatComponent message) {
        return new ChatComponentText("")
                .appendSibling(wrapInSquareBrackets(new ChatComponentText(CommonCompatibilityManager.INSTANCE.getModName())).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)))
                .appendText(" ")
                .appendSibling(message);
    }

    public static void sendPlayerMessage(IChatComponent component) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player == null) {
            return;
        }
        player.addChatMessage(component);
    }
}

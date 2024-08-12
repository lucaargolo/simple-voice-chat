package de.maxhenkel.voicechat.command;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import de.maxhenkel.voicechat.permission.PermissionManager;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import de.maxhenkel.voicechat.voice.server.ClientConnection;
import de.maxhenkel.voicechat.voice.server.Group;
import de.maxhenkel.voicechat.voice.server.PingManager;
import de.maxhenkel.voicechat.voice.server.Server;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VoicechatCommand extends CommandBase {

    public static final String VOICECHAT_COMMAND = "voicechat";

    @Override
    public String getCommandName() {
        return VOICECHAT_COMMAND;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/voicechat <help|test|invite|join|leave>";
    }

    @Override
    public void processCommand(ICommandSender commandSender, String[] args) throws CommandException {
        if (checkNoVoicechat(commandSender)) {
            return;
        }
        if (!(commandSender instanceof EntityPlayerMP)) {
            return;
        }

        EntityPlayerMP sender = (EntityPlayerMP) commandSender;

        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("help")) {
                helpCommand(sender, args);
                return;
            } else if (args[0].equalsIgnoreCase("test")) {
                if (PermissionManager.INSTANCE.ADMIN_PERMISSION.hasPermission(sender)) {
                    testCommand(sender, args);
                    return;
                }
            } else if (args[0].equalsIgnoreCase("invite")) {
                inviteCommand(sender, args);
                return;
            } else if (args[0].equalsIgnoreCase("join")) {
                joinCommand(sender, args);
                return;
            } else if (args[0].equalsIgnoreCase("leave")) {
                leaveCommand(sender);
                return;
            }
        }
        helpCommand(sender, args);
    }

    private boolean helpCommand(EntityPlayerMP commandSender, String[] args) {
        commandSender.addChatMessage(new ChatComponentText("/voicechat [help]"));
        commandSender.addChatMessage(new ChatComponentText("/voicechat [test] <target>"));
        commandSender.addChatMessage(new ChatComponentText("/voicechat [invite] <target>"));
        commandSender.addChatMessage(new ChatComponentText("/voicechat [join] <group> [<password>]"));
        commandSender.addChatMessage(new ChatComponentText("/voicechat [leave]"));
        return true;
    }

    private boolean testCommand(EntityPlayerMP commandSender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        EntityPlayerMP player = commandSender.mcServer.getConfigurationManager().getPlayerByUsername(args[1]);

        if (player == null) {
            commandSender.addChatMessage(new ChatComponentTranslation("commands.generic.player.notFound", args[1]));
            return true;
        }

        if (!Voicechat.SERVER.isCompatible(player)) {
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.player_no_voicechat", player.getDisplayName(), new ChatComponentText(CommonCompatibilityManager.INSTANCE.getModName())));
            return true;
        }

        ClientConnection clientConnection = Voicechat.SERVER.getServer().getConnections().get(player.getUniqueID());

        if (clientConnection == null) {
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.client_not_connected"));
            return true;
        }

        try {
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.sending_ping"));
            Voicechat.SERVER.getServer().getPingManager().sendPing(clientConnection, 500, 10, new PingManager.PingListener() {

                @Override
                public void onPong(int attempts, long pingMilliseconds) {
                    if (attempts <= 1) {
                        commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.ping_received", new ChatComponentText(String.valueOf(pingMilliseconds))));
                    } else {
                        commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.ping_received_attempt", new ChatComponentText(String.valueOf(attempts)), new ChatComponentText(String.valueOf(pingMilliseconds))));
                    }
                }

                @Override
                public void onFailedAttempt(int attempts) {
                    commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.ping_retry"));
                }

                @Override
                public void onTimeout(int attempts) {
                    commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.ping_timed_out", new ChatComponentText(String.valueOf(attempts))));
                }
            });
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.ping_sent_waiting"));
        } catch (Exception e) {
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.failed_to_send_ping", new ChatComponentText(String.valueOf(e.getMessage()))));
            Voicechat.LOGGER.warn("Failed to send ping", e);
        }
        return true;
    }

    private boolean inviteCommand(EntityPlayerMP commandSender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        EntityPlayerMP player = parsePlayer(commandSender, args[1]);

        if (player == null) {
            commandSender.addChatMessage(new ChatComponentTranslation("commands.generic.player.notFound", args[1]));
            return true;
        }

        PlayerState state = Voicechat.SERVER.getServer().getPlayerStateManager().getState(commandSender.getUniqueID());

        if (state == null || !state.hasGroup()) {
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.not_in_group"));
            return true;
        }

        Group group = Voicechat.SERVER.getServer().getGroupManager().getGroup(state.getGroup());
        if (group == null) {
            return true;
        }

        if (!Voicechat.SERVER.isCompatible(player)) {
            commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.player_no_voicechat", player.getDisplayName(), new ChatComponentText(CommonCompatibilityManager.INSTANCE.getModName())));
            return true;
        }

        String passwordSuffix = group.getPassword() == null ? "" : " \"" + group.getPassword() + "\"";
        player.addChatMessage(new ChatComponentTranslation("message.voicechat.invite",
                new ChatComponentText(commandSender.getName()),
                new ChatComponentText(group.getName()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)),
                new ChatComponentText("[").appendSibling(
                        new ChatComponentTranslation("message.voicechat.accept_invite")
                                .setChatStyle(new ChatStyle()
                                        .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/voicechat join " + group.getId().toString() + passwordSuffix))
                                        .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentTranslation("message.voicechat.accept_invite.hover")))
                                        .setColor(EnumChatFormatting.GREEN)
                                ).appendText("]").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))
                )));
        commandSender.addChatMessage(new ChatComponentTranslation("message.voicechat.invite_successful", new ChatComponentText(player.getName())));

        return true;
    }

    private boolean joinCommand(EntityPlayerMP player, String[] args) {
        if (args.length < 2) {
            return false;
        }

        int argIndex = 1;
        UUID groupUUID;
        try {
            groupUUID = UUID.fromString(args[argIndex]);
        } catch (Exception e) {
            String groupName;
            if (args[argIndex].startsWith("\"")) {
                StringBuilder sb = new StringBuilder();
                for (; argIndex < args.length; argIndex++) {
                    sb.append(args[argIndex]).append(" ");
                    if (args[argIndex].endsWith("\"") && !args[argIndex].endsWith("\\\"")) {
                        break;
                    }
                }
                groupName = sb.toString().trim();
                String[] split = groupName.split("\"");
                if (split.length > 1) {
                    groupName = split[1];
                }
            } else {
                groupName = args[argIndex];
            }
            groupUUID = getGroupUUID(player, Voicechat.SERVER.getServer(), groupName);
        }

        if (groupUUID == null) {
            return true;
        }

        argIndex++;

        String password = null;
        if (args.length >= argIndex + 1) {
            StringBuilder sb = new StringBuilder();
            for (; argIndex < args.length; argIndex++) {
                sb.append(args[argIndex]).append(" ");
            }
            password = sb.toString().trim();
            if (password.startsWith("\"")) {
                String[] split = password.split("\"");
                if (split.length > 1) {
                    password = split[1];
                }
            }
        }

        joinGroup(player, groupUUID, password);
        return true;
    }

    private UUID getGroupUUID(EntityPlayerMP player, Server server, String groupName) {
        List<Group> groups = server.getGroupManager().getGroups().values().stream().filter(group -> group.getName().equals(groupName)).collect(Collectors.toList());

        if (groups.isEmpty()) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.group_does_not_exist"));
            return null;
        }

        if (groups.size() > 1) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.group_name_not_unique"));
            return null;
        }

        return groups.get(0).getId();
    }

    private static void joinGroup(EntityPlayerMP player, UUID groupID, @Nullable String password) {
        if (!Voicechat.SERVER_CONFIG.groupsEnabled.get()) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.groups_disabled"));
            return;
        }

        Server server = Voicechat.SERVER.getServer();

        if (!PermissionManager.INSTANCE.GROUPS_PERMISSION.hasPermission(player)) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.no_group_permission"));
            return;
        }

        Group group = server.getGroupManager().getGroup(groupID);

        if (group == null) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.group_does_not_exist"));
            return;
        }

        server.getGroupManager().joinGroup(group, player, password);
        player.addChatMessage(new ChatComponentTranslation("message.voicechat.join_successful", new ChatComponentText(group.getName()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN))));
    }

    private boolean leaveCommand(EntityPlayerMP player) {
        if (!Voicechat.SERVER_CONFIG.groupsEnabled.get()) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.groups_disabled"));
            return true;
        }

        Server server = Voicechat.SERVER.getServer();
        PlayerState state = server.getPlayerStateManager().getState(player.getUniqueID());
        if (state == null || !state.hasGroup()) {
            player.addChatMessage(new ChatComponentTranslation("message.voicechat.not_in_group"));
            return true;
        }

        server.getGroupManager().leaveGroup(player);
        player.addChatMessage(new ChatComponentTranslation("message.voicechat.leave_successful"));
        return true;
    }

    private static boolean checkNoVoicechat(ICommandSender commandSender) {
        if (commandSender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) commandSender;
            if (Voicechat.SERVER.isCompatible(player)) {
                return false;
            }
            commandSender.addChatMessage(new ChatComponentText(String.format(Voicechat.TRANSLATIONS.voicechatNeededForCommandMessage.get(), CommonCompatibilityManager.INSTANCE.getModName())));
        } else {
            commandSender.addChatMessage(new ChatComponentText(Voicechat.TRANSLATIONS.playerCommandMessage.get()));
        }
        return true;
    }

    @Nullable
    public static EntityPlayerMP parsePlayer(ICommandSender commandSender, String playerArg) {
        World world = commandSender.getEntityWorld();
        if(world instanceof WorldServer) {
            MinecraftServer server = ((WorldServer) world).getMinecraftServer();
            EntityPlayerMP player = server.getConfigurationManager().getPlayerByUsername(playerArg);
            if (player != null) {
                return player;
            }
            try {
                UUID uuid = UUID.fromString(playerArg);
                return server.getConfigurationManager().getPlayerByUUID(uuid);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;

    }

}

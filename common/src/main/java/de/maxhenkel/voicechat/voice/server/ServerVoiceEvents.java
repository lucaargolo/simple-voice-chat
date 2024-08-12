package de.maxhenkel.voicechat.voice.server;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.VoicechatClient;
import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import de.maxhenkel.voicechat.net.NetManager;
import de.maxhenkel.voicechat.net.SecretPacket;
import de.maxhenkel.voicechat.plugins.PluginManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerVoiceEvents {

    private final Map<UUID, Integer> clientCompatibilities;
    private Server server;

    public ServerVoiceEvents() {
        clientCompatibilities = new ConcurrentHashMap<>();
        CommonCompatibilityManager.INSTANCE.onServerStarting(this::serverStarting);
        CommonCompatibilityManager.INSTANCE.onPlayerLoggedIn(this::playerLoggedIn);
        CommonCompatibilityManager.INSTANCE.onPlayerLoggedOut(this::playerLoggedOut);
        CommonCompatibilityManager.INSTANCE.onServerStopping(this::serverStopping);

        CommonCompatibilityManager.INSTANCE.onServerVoiceChatConnected(this::serverVoiceChatConnected);
        CommonCompatibilityManager.INSTANCE.onServerVoiceChatDisconnected(this::serverVoiceChatDisconnected);
        CommonCompatibilityManager.INSTANCE.onPlayerCompatibilityCheckSucceeded(this::playerCompatibilityCheckSucceeded);

        CommonCompatibilityManager.INSTANCE.getNetManager().requestSecretChannel.setServerListener((server, player, handler, packet) -> {
            Voicechat.LOGGER.info("Received secret request of {} ({})", player.getDisplayNameString(), packet.getCompatibilityVersion());
            clientCompatibilities.put(player.getUniqueID(), packet.getCompatibilityVersion());
            if (packet.getCompatibilityVersion() != Voicechat.COMPATIBILITY_VERSION) {
                Voicechat.LOGGER.warn("Connected client {} has incompatible voice chat version (server={}, client={})", player.getDisplayNameString(), Voicechat.COMPATIBILITY_VERSION, packet.getCompatibilityVersion());
                player.addChatMessage(getIncompatibleMessage(packet.getCompatibilityVersion()));
            } else {
                initializePlayerConnection(player);
            }
        });
    }

    public IChatComponent getIncompatibleMessage(int clientCompatibilityVersion) {
        if (clientCompatibilityVersion <= 6) {
            return new ChatComponentText(String.format(Voicechat.TRANSLATIONS.voicechatNotCompatibleMessage.get(), CommonCompatibilityManager.INSTANCE.getModVersion(), CommonCompatibilityManager.INSTANCE.getModName()));
        } else {
            return new ChatComponentTranslation("message.voicechat.incompatible_version",
                    new ChatComponentText(CommonCompatibilityManager.INSTANCE.getModVersion()).setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.BOLD)),
                    new ChatComponentText(CommonCompatibilityManager.INSTANCE.getModName()).setChatStyle((new ChatStyle()).setColor(EnumChatFormatting.BOLD)));
        }
    }

    public boolean isCompatible(EntityPlayerMP player) {
        return isCompatible(player.getUniqueID());
    }

    public boolean isCompatible(UUID playerUuid) {
        return clientCompatibilities.getOrDefault(playerUuid, -1) == Voicechat.COMPATIBILITY_VERSION;
    }

    public void serverStarting(MinecraftServer mcServer) {
        if (server != null) {
            server.close();
            server = null;
        }

        if (!mcServer.isDedicatedServer() && VoicechatClient.CLIENT_CONFIG != null && !VoicechatClient.CLIENT_CONFIG.runLocalServer.get()) {
            Voicechat.LOGGER.info("Disabling voice chat in singleplayer");
            return;
        }

        if (mcServer.isDedicatedServer()) {
            if (!mcServer.isServerInOnlineMode()) {
                Voicechat.LOGGER.warn("Running in offline mode - Voice chat encryption is not secure!");
            }
        }

        try {
            server = new Server(mcServer);
            server.start();
            PluginManager.instance().onServerStarted();
        } catch (Exception e) {
            Voicechat.LOGGER.error("Failed to start voice chat server", e);
        }
    }

    public void initializePlayerConnection(EntityPlayerMP player) {
        if (server == null) {
            return;
        }
        CommonCompatibilityManager.INSTANCE.emitPlayerCompatibilityCheckSucceeded(player);

        UUID secret = server.generateNewSecret(player.getUniqueID());
        if (secret == null) {
            Voicechat.LOGGER.warn("Player already requested secret - ignoring");
            return;
        }
        NetManager.sendToClient(player, new SecretPacket(player, secret, server.getPort(), Voicechat.SERVER_CONFIG));
        Voicechat.LOGGER.info("Sent secret to {}", player.getDisplayNameString());
    }

    public void playerLoggedIn(EntityPlayerMP serverPlayer) {
        if (server != null) {
            server.onPlayerLoggedIn(serverPlayer);
        }
        if (!Voicechat.SERVER_CONFIG.forceVoiceChat.get()) {
            return;
        }

        Timer timer = new Timer(serverPlayer.getGameProfile().getName() + "%s-login-timer", true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                timer.cancel();
                timer.purge();
                if (serverPlayer.mcServer.isServerStopped()) {
                    return;
                }
                if (!serverPlayer.playerNetServerHandler.netManager.isChannelOpen()) {
                    return;
                }
                if (!isCompatible(serverPlayer)) {
                    serverPlayer.mcServer.addScheduledTask(() -> {
                        serverPlayer.playerNetServerHandler.onDisconnect(
                                new ChatComponentText(String.format(
                                        Voicechat.TRANSLATIONS.forceVoicechatKickMessage.get(),
                                        CommonCompatibilityManager.INSTANCE.getModName(),
                                        CommonCompatibilityManager.INSTANCE.getModVersion()
                                ))
                        );
                    });
                }
            }
        }, Voicechat.SERVER_CONFIG.loginTimeout.get());
    }

    public void playerLoggedOut(EntityPlayerMP player) {
        clientCompatibilities.remove(player.getUniqueID());
        if (server == null) {
            return;
        }

        server.onPlayerLoggedOut(player);
        Voicechat.LOGGER.info("Disconnecting client {}", player.getDisplayNameString());
    }

    public void serverVoiceChatConnected(EntityPlayerMP serverPlayer) {
        if (server == null) {
            return;
        }

        server.onPlayerVoicechatConnect(serverPlayer);
    }

    public void serverVoiceChatDisconnected(UUID uuid) {
        if (server == null) {
            return;
        }

        server.onPlayerVoicechatDisconnect(uuid);
    }

    public void playerCompatibilityCheckSucceeded(EntityPlayerMP player) {
        if (server == null) {
            return;
        }

        server.onPlayerCompatibilityCheckSucceeded(player);
    }

    @Nullable
    public Server getServer() {
        return server;
    }

    public void serverStopping(MinecraftServer mcServer) {
        if (server != null) {
            server.close();
            server = null;
        }
    }

}

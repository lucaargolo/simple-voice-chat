package de.maxhenkel.voicechat.voice.server;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import de.maxhenkel.voicechat.net.NetManager;
import de.maxhenkel.voicechat.net.PlayerStatePacket;
import de.maxhenkel.voicechat.net.PlayerStatesPacket;
import de.maxhenkel.voicechat.plugins.PluginManager;
import de.maxhenkel.voicechat.voice.common.PlayerState;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerStateManager {

    private final ConcurrentHashMap<UUID, PlayerState> states;
    private final Server voicechatServer;

    public PlayerStateManager(Server voicechatServer) {
        this.voicechatServer = voicechatServer;
        this.states = new ConcurrentHashMap<>();

        CommonCompatibilityManager.INSTANCE.getNetManager().updateStateChannel.setServerListener((server, player, handler, packet) -> {
            PlayerState state = states.get(player.getUniqueID());

            if (state == null) {
                state = defaultDisconnectedState(player);
            }

            state.setDisabled(packet.isDisabled());

            states.put(player.getUniqueID(), state);

            broadcastState(state);
            Voicechat.LOGGER.debug("Got state of {}: {}", player.getDisplayNameString(), state);
        });
    }

    public void broadcastState(PlayerState state) {
        PlayerStatePacket packet = new PlayerStatePacket(state);
        voicechatServer.getServer().getConfigurationManager().getPlayerList().forEach(p -> NetManager.sendToClient(p, packet));
        PluginManager.instance().onPlayerStateChanged(state);
    }

    public void onPlayerCompatibilityCheckSucceeded(EntityPlayerMP player) {
        PlayerStatesPacket packet = new PlayerStatesPacket(states);
        NetManager.sendToClient(player, packet);
        Voicechat.LOGGER.debug("Sending initial states to {}", player.getDisplayNameString());
    }

    public void onPlayerLoggedIn(EntityPlayerMP player) {
        PlayerState state = defaultDisconnectedState(player);
        states.put(player.getUniqueID(), state);
        broadcastState(state);
        Voicechat.LOGGER.debug("Setting default state of {}: {}", player.getDisplayNameString(), state);
    }

    public void onPlayerLoggedOut(EntityPlayerMP player) {
        states.remove(player.getUniqueID());
        broadcastState(new PlayerState(player.getUniqueID(), player.getGameProfile().getName(), false, true));
        Voicechat.LOGGER.debug("Removing state of {}", player.getDisplayNameString());
    }

    public void onPlayerVoicechatDisconnect(UUID uuid) {
        PlayerState state = states.get(uuid);
        if (state == null) {
            return;
        }

        state.setDisconnected(true);

        broadcastState(state);
        Voicechat.LOGGER.debug("Set state of {} to disconnected: {}", uuid, state);
    }

    public void onPlayerVoicechatConnect(EntityPlayerMP player) {
        PlayerState state = states.get(player.getUniqueID());

        if (state == null) {
            state = defaultDisconnectedState(player);
        }

        state.setDisconnected(false);

        states.put(player.getUniqueID(), state);

        broadcastState(state);
        Voicechat.LOGGER.debug("Set state of {} to connected: {}", player.getDisplayNameString(), state);
    }

    @Nullable
    public PlayerState getState(UUID playerUUID) {
        return states.get(playerUUID);
    }

    public static PlayerState defaultDisconnectedState(EntityPlayerMP player) {
        return new PlayerState(player.getUniqueID(), player.getGameProfile().getName(), false, true);
    }

    public void setGroup(EntityPlayerMP player, @Nullable UUID group) {
        PlayerState state = states.get(player.getUniqueID());
        if (state == null) {
            state = PlayerStateManager.defaultDisconnectedState(player);
            Voicechat.LOGGER.debug("Defaulting to default state for {}: {}", player.getDisplayNameString(), state);
        }
        state.setGroup(group);
        states.put(player.getUniqueID(), state);
        broadcastState(state);
        Voicechat.LOGGER.debug("Setting group of {}: {}", player.getDisplayNameString(), state);
    }

    public Collection<PlayerState> getStates() {
        return states.values();
    }

}

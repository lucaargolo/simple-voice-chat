package de.maxhenkel.voicechat;

import de.maxhenkel.voicechat.command.VoicechatCommand;
import de.maxhenkel.voicechat.intercompatibility.CommonCompatibilityManager;
import de.maxhenkel.voicechat.intercompatibility.ForgeCommonCompatibilityManager;
import de.maxhenkel.voicechat.permission.ForgePermissionManager;
import de.maxhenkel.voicechat.permission.PermissionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

import javax.annotation.Nullable;

@Mod(modid = ForgeVoicechatMod.MODID, acceptedMinecraftVersions = "[1.8.9]", updateJSON = "https://maxhenkel.de/update/voicechat.json", guiFactory = "de.maxhenkel.voicechat.VoicechatGuiFactory")
public class ForgeVoicechatMod extends Voicechat {

    public static ForgeVoicechatMod INSTANCE;
    @Nullable
    private static ForgeVoicechatClientMod CLIENT_MOD;

    private final ForgeCommonCompatibilityManager compatibilityManager;

    public ForgeVoicechatMod() {
        INSTANCE = this;
        compatibilityManager = ((ForgeCommonCompatibilityManager) CommonCompatibilityManager.INSTANCE);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient()) {
            CLIENT_MOD = new ForgeVoicechatClientMod(event);
        }
        compatibilityManager.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        initialize();
        MinecraftForge.EVENT_BUS.register(compatibilityManager);
        ((ForgePermissionManager) PermissionManager.INSTANCE).registerPermissions();
        if (CLIENT_MOD != null) {
            CLIENT_MOD.clientSetup(event);
        }
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new VoicechatCommand());
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartedEvent event) {
        compatibilityManager.serverStarted(event);
    }

    @Mod.EventHandler
    public void serverStopping(FMLServerStoppingEvent event) {
        compatibilityManager.serverStopping(event);
    }

}
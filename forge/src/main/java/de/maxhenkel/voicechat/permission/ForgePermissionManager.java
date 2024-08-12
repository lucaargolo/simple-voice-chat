package de.maxhenkel.voicechat.permission;

public class ForgePermissionManager extends PermissionManager {

    @Override
    public Permission createPermissionInternal(String modId, String node, PermissionType type) {
        return new ForgePermission(modId + "." + node, type);
    }

    public void registerPermissions() {
        //TODO: This
//        getPermissions().stream().map(ForgePermission.class::cast).forEach(permission -> {
//            PermissionAPI.registerNode(permission.getNode(), permission.getLevel(), "");
//        });
    }

}

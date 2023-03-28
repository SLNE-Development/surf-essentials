package dev.slne.surf.essentials.utils.permission;

import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private final PluginManager pluginManager;
    private static boolean initializedPermissions = false;

    public PermissionManager(Plugin plugin){
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    public void initializePermissions() {
        if (initializedPermissions) return;
        EssentialsUtil.sendDebug("Initializing permissions...");
        Permission parentPermission = new Permission("surf.essentials.*", "Allows access to all essentials commands but also alerts (e.g. gamemode change)");
        EssentialsUtil.sendDebug("Adding parent permission: " + parentPermission.getName());
        pluginManager.addPermission(parentPermission);

        EssentialsUtil.sendDebug("Getting permission list...");
        List<String> permissionList = getPermissionList();
        for (String permissionString : permissionList) {
            EssentialsUtil.sendDebug("Linking permissions: " + permissionString +  " -> " + parentPermission.getName());
            addPermission(permissionString, parentPermission);
        }
        initializedPermissions = true;
    }

    private List<String> getPermissionList() {
        Field[] fields = Permissions.class.getDeclaredFields();
        List<String> permissions = new ArrayList<>();

        for (Field field : fields) {
            String permission = getPermissionFromField(field);
            if (permission != null){
                EssentialsUtil.sendDebug("Getting permission: " + permission);
                permissions.add(permission);
            }
        }
        return permissions;
    }

    private void addPermission(String permissionString, Permission parentPermission){
        Permission permission = new Permission(permissionString);
        permission.addParent(parentPermission, true);
        pluginManager.addPermission(permission);
    }

    private String getPermissionFromField(Field field){
        try {
            if (field.get(null) instanceof String permissionString){
                return permissionString;
            }else {
                SurfEssentials.logger().error("Invalid type for field '%s' in Permissions class! Expected String, but found '%s'. See stacktrace below:".formatted(field.getName(), field.getType().getSimpleName()));
                new IllegalArgumentException("").printStackTrace();
                return null;
            }
        } catch (IllegalAccessException e) {
            SurfEssentials.logger().error("Could not register permissions! See stacktrace below: ");
            e.printStackTrace();
        }
        return null;
    }
}

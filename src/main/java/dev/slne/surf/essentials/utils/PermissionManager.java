package dev.slne.surf.essentials.utils;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;

public class PermissionManager {
    private final Plugin plugin;
    private final PluginManager pluginManager;

    public PermissionManager(Plugin plugin){
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    public void initializePermissions() {
        Field[] fields = Permissions.class.getDeclaredFields();
        Permission parentPermission = new Permission("surf.essentials.*", "Allows access to all essentials commands but also alerts (e.g. gamemode change)");

        pluginManager.addPermission(parentPermission);

        try {
            for (Field field : fields) {
                String permissionString = (String) field.get(null);
                Permission permission = new Permission(permissionString);
                permission.addParent(parentPermission, true);
                pluginManager.addPermission(permission);
            }
        }catch (IllegalAccessException e) {
            plugin.getLogger().severe("Could not register permissions! See stacktrace below: ");
            e.printStackTrace();
        }
    }
}

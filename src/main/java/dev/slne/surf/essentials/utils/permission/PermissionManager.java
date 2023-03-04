package dev.slne.surf.essentials.utils.permission;

import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class PermissionManager {
    private final Plugin plugin;
    private final PluginManager pluginManager;

    public PermissionManager(Plugin plugin){
        this.plugin = plugin;
        this.pluginManager = plugin.getServer().getPluginManager();
    }

    public void initializePermissions() {
        Permission parentPermission = new Permission("surf.essentials.*", "Allows access to all essentials commands but also alerts (e.g. gamemode change)");
        pluginManager.addPermission(parentPermission);

        List<String> permissionList = getPermissionList();
        for (String permissionString : permissionList) {
            addPermission(permissionString, parentPermission);
        }
    }

    private List<String> getPermissionList() {
        Field[] fields = Permissions.class.getDeclaredFields();
        List<String> permissions = new ArrayList<>();

        for (Field field : fields) {
            String permission = getPermissionFromField(field);
            if (permission != null){
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
                plugin.getLogger().severe("Invalid type for field '%s' in Permissions class! Expected String, but found '%s'. See stacktrace below:".formatted(field.getName(), field.getType().getSimpleName()));
                new IllegalArgumentException("").printStackTrace();
                return null;
            }
        } catch (IllegalAccessException e) {
            plugin.getLogger().severe("Could not register permissions! See stacktrace below: ");
            e.printStackTrace();
        }
        return null;
    }
}

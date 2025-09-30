package dev.slne.surf.essentialsold.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class GodmodeCommand extends EssentialsCommand { // TODO
    public GodmodeCommand() {
        super("godmode", "god [<enable | disable> <players>]", "Change the godmode of players", "god");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.GOD_MODE_SELF_PERMISSION, Permissions.GOD_MODE_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> godMode(
                sender.getCallee(),
                List.of(getEntityOrException(sender)),
                !getEntityOrException(sender).isInvulnerable()
        ));

        then(literal("enable")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> godMode(sender.getCallee(), List.of(getEntityOrException(sender)), true))
                .then(entitiesArgument("entities")
                        .withPermission(Permissions.GOD_MODE_OTHER_PERMISSION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> godMode(sender.getCallee(), args.getUnchecked("entities"), true))));

        then(literal("disable")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> godMode(sender.getCallee(), List.of(getEntityOrException(sender)), false))
                .then(playersArgument("entities")
                        .withPermission(Permissions.GOD_MODE_OTHER_PERMISSION)
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> godMode(sender.getCallee(), args.getUnchecked("entities"), false))));
    }

    private int godMode(CommandSender source, Collection<Entity> targetsUnchecked, boolean enable) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        for (Entity target : targets) {
            target.setInvulnerable(enable);
            successfulChanges++;
            EssentialsUtil.sendSuccess(target, (Component.text("Du bist nun ", Colors.GREEN))
                    .append(Component.text(target.isInvulnerable() ? "unverwundbar!" : "verwundbar!", Colors.GREEN)));
        }

        val target = targets.iterator().next();
        boolean isSelf = source instanceof Player player && player.equals(target);
        if (successfulChanges == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" ist nun ", Colors.SUCCESS))
                    .append(Component.text(target.isInvulnerable() ? "unverwundbar!" : "verwundbar!", Colors.SUCCESS)));
        } else if (successfulChanges >= 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text(successfulChanges, Colors.TERTIARY)
                    .append(Component.text(" Entities sind nun ", Colors.SUCCESS))
                    .append(Component.text(enable ? "unverwundbar!" : "verwundbar!", Colors.SUCCESS)));
        }
        return successfulChanges;
    }
}

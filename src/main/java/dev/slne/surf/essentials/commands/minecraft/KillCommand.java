package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.List;

public class KillCommand extends EssentialsCommand {
    public KillCommand() {
        super("kill", "[<targets>]", "Kill the targets");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.KILL_SELF_PERMISSION, Permissions.KILL_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> kill(sender.getCallee(), List.of(getEntityOrException(sender))));
        then(entitiesArgument("targets")
                .withPermission(Permissions.KILL_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> kill(sender.getCallee(), args.getUnchecked("targets"))));
    }

    private int kill(CommandSender sender, Collection<Entity> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(sender, targetsUnchecked);

        for (Entity entity : targets) {
            if (entity instanceof Damageable damageable) {
                damageable.setHealth(0);
            } else {
                entity.remove();
            }
        }

        if (targets.size() == 1) {
            EssentialsUtil.sendSuccess(sender, EssentialsUtil.getDisplayName(targets.iterator().next())
                    .append(Component.text(" wurde getötet!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(sender, Component.text(targets.size(), Colors.TERTIARY)
                    .append(Component.text(" entities wurden getötet!", Colors.SUCCESS)));
        }

        return 1;
    }
}

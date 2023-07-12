package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
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

        executesNative((NativeResultingCommandExecutor) (sender, args) -> kill(sender.getCallee(), List.of(getSpecialEntityOrException(sender, Damageable.class))));
        then(entitiesArgument("targets")
                .withPermission(Permissions.KILL_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> kill(sender.getCallee(), args.getUnchecked("targets"))));
    }

    private int kill(CommandSender sender, Collection<Entity> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(sender, targetsUnchecked);
        val livingEntities = targets.stream()
                .filter(entity -> entity instanceof Damageable)
                .map(entity -> (Damageable) entity)
                .toList();

        if (livingEntities.isEmpty() && targets.size() == 1)
            throw Exceptions.ERROR_NOT_VALID_ENTITY_FOR_COMMAND.create(targets.iterator().next());

        for (Damageable entity : livingEntities) {
            entity.setHealth(0);
        }

        if (livingEntities.size() == 1) {
            EssentialsUtil.sendSuccess(sender, EssentialsUtil.getDisplayName(livingEntities.iterator().next())
                    .append(Component.text(" wurde getötet!", Colors.SUCCESS)));
        } else {
            EssentialsUtil.sendSuccess(sender, Component.text(livingEntities.size(), Colors.TERTIARY)
                    .append(Component.text(" entities wurden getötet!", Colors.SUCCESS)));
        }

        return 1;
    }
}

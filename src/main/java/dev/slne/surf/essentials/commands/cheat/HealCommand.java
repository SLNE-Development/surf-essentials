package dev.slne.surf.essentials.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class HealCommand extends EssentialsCommand {
    public HealCommand() {
        super("heal", "heal [<entities>]", "Heals the entities");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.HEAL_SELF_PERMISSION, Permissions.HEAL_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> heal(sender.getCallee(), List.of(getEntityOrException(sender))));

        then(entitiesArgument("entities")
                .withPermission(Permissions.HEAL_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> heal(sender.getCallee(), args.getUnchecked("entities"))));
    }

    private int heal(CommandSender source, Collection<Entity> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkEntitySuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        for (Entity target : targets) {
            if (!(target instanceof LivingEntity livingEntity)) continue;

            EssentialsUtil.heal(livingEntity, Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue(), EntityRegainHealthEvent.RegainReason.REGEN, true);
            successfulChanges++;
            EssentialsUtil.sendSuccess(target, Component.text("Du wurdest geheilt! ", Colors.GREEN));
        }

        val target = targets.iterator().next();
        boolean isSelf = source instanceof Player player && player.equals(target);
        if (successfulChanges == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" wurde geheilt!", Colors.SUCCESS)));
        } else if (successfulChanges >= 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text(successfulChanges, Colors.TERTIARY)
                    .append(Component.text(" Entities wurden geheilt!", Colors.SUCCESS)));
        }
        return successfulChanges;
    }
}

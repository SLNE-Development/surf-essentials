package dev.slne.surf.essentialsold.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.SurfEssentials;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;
import java.util.List;

public class FlyCommand extends EssentialsCommand {
    public static NamespacedKey PDC_IN_FLY_MODE = new NamespacedKey(SurfEssentials.getInstance(), "in_fly_mode");

    public FlyCommand() {
        super("fly", "fly [<players>] [<enable | disable>]", "Toggles the fly mode", "flymode");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.FLY_SELF_PERMISSION, Permissions.FLY_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> fly(sender.getCallee(), List.of(getPlayerOrException(sender)), true, true));
        then(playersArgument("players")
                .withPermission(Permissions.FLY_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> fly(sender.getCallee(),  args.getUnchecked("players"), true, true))

                .then(literal("enable")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fly(sender.getCallee(), args.getUnchecked("players"), false, true)))
                .then(literal("disable")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> fly(sender.getCallee(), args.getUnchecked("players"), false, false))));
    }

    private int fly(CommandSender source, Collection<Player> targetsUnchecked, boolean toggle, Boolean allowFly) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulChanges = 0;

        for (Player target : targets) {
            if (toggle) {
                final boolean shouldFly = target.getAllowFlight();
                target.setAllowFlight(!shouldFly);
                target.setFlying(!shouldFly);
            } else {
                target.setAllowFlight(allowFly);
                target.setFlying(allowFly);
            }
            successfulChanges++;
            EssentialsUtil.sendSuccess(target, Component.text("Du kannst nun ", Colors.GREEN)
                    .append(Component.text((target.getAllowFlight()) ? "fliegen!" : "nicht mehr fliegen!", Colors.GREEN)));
            target.getPersistentDataContainer().set(PDC_IN_FLY_MODE, PersistentDataType.BOOLEAN, target.getAllowFlight());
        }

        if (successfulChanges == 1) {
            if (source instanceof Player player && player == targets.iterator().next()) return 1;
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
                    .append(Component.text(" kann nun " + ((targets.iterator().next().getAllowFlight()) ? "fliegen!" : "nicht mehr fliegen!"))));
        } else {
            if (toggle) {
                EssentialsUtil.sendSuccess(source, Component.text("Die Flugfunktion wurde für ", Colors.SUCCESS)
                        .append(Component.text(successfulChanges, Colors.TERTIARY))
                        .append(Component.text(" Spieler umgeschaltet!", Colors.SUCCESS)));
            } else {
                EssentialsUtil.sendSuccess(source, Component.text("Die Flugfunktion wurde für ", Colors.SUCCESS)
                        .append(Component.text(successfulChanges, Colors.TERTIARY))
                        .append(Component.text(" Spieler ", Colors.SUCCESS))
                        .append(Component.text((allowFly) ? "aktiviert" : "deaktiviert", Colors.TERTIARY))
                        .append(Component.text("!", Colors.SUCCESS)));
            }
        }
        return successfulChanges;
    }
}

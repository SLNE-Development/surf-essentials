package dev.slne.surf.essentialsold.commands.general;

import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

public class ChangeSlotCommand extends EssentialsCommand {

    public ChangeSlotCommand() {
        super("changeslot", "changeslot <slot>", "Change the slot of the server", "changeslot");

        withPermission(Permissions.CHANGE_SLOTS_PERMISSION);

        then(integerArgument("slot", 1)
                .executesNative((nativeProxyCommandSender, commandArguments) -> {
                    final Integer slot = commandArguments.getOrDefaultUnchecked("slot", 1);
                    Bukkit.setMaxPlayers(slot);
                    EssentialsUtil.sendSuccess(nativeProxyCommandSender.getCallee(), Component.text("Der Slot wurde auf ", Colors.SUCCESS)
                            .append(Component.text(slot, Colors.VARIABLE_VALUE))
                            .append(Component.text(" gesetzt!", Colors.SUCCESS)));
                    return 1;
                }));
    }
}

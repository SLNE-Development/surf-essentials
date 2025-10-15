package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Objects;

public class SetItemNameCommand extends EssentialsCommand {
    public SetItemNameCommand() {
        super("setitemname", "setitemname <name>", "Set the name of an item", "itemname", "setname", "rename");

        withPermission(Permissions.SET_ITEM_NAME_PERMISSION);

        then(greedyStringArgument("name")
                .replaceSuggestions(EssentialsUtil.suggestColors())
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setName(getPlayerOrException(sender), Objects.requireNonNull(args.getUnchecked("name")))));
    }

    private int setName(Player source, String name) {
        val itemStackInHand = source.getInventory().getItemInMainHand();;

        EssentialsUtil.changeName(
                itemStackInHand,
                EssentialsUtil.deserialize(name).colorIfAbsent(Colors.WHITE)
        );

        EssentialsUtil.sendSuccess(source, Component.text("Das Item ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(itemStackInHand))
                .append(Component.text(" wurde umbenannt!", Colors.SUCCESS)));

        return 1;
    }
}

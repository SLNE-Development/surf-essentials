package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

public class SetItemLoreCommand extends EssentialsCommand {
    public SetItemLoreCommand() {
        super("setitemlore", "lore <lore>", "Set the lore of an item", "lore", "setlore");

        withPermission(Permissions.SET_ITEM_LORE_PERMISSION);

        then(greedyStringArgument("lore")
                .replaceSuggestions(EssentialsUtil.suggestColors())
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> setLore(getPlayerOrException(sender), Objects.requireNonNull(args.getUnchecked("lore")))));
    }

    private int setLore(Player source, String loreString) {
        val itemStack = source.getInventory().getItemInMainHand();

        itemStack.lore(
                Arrays.stream(loreString.translateEscapes().split("\n"))
                .map(s -> EssentialsUtil.deserialize(s).colorIfAbsent(Colors.INFO))
                .toList()
        );

        EssentialsUtil.sendSuccess(source, Component.text("Die Beschreibung von ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(itemStack))
                .append(Component.text(" wurde geändert!", Colors.SUCCESS)));

        return 1;
    }
}

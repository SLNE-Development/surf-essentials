package dev.slne.surf.essentialsold.commands.general;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.brigadier.Exceptions;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHeadCommand extends EssentialsCommand {
    public PlayerHeadCommand() {
        super("playerhead", "phead <player>", "Get the head of a player", "phead");

        withPermission(Permissions.PLAYER_HEAD_PERMISSION);

        then(playerArgument("player")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> getPlayerHead(getSpecialEntityOrException(sender, HumanEntity.class), args.getUnchecked("player"))));
    }

    private int getPlayerHead(HumanEntity source, Player playerUnchecked) throws WrapperCommandSyntaxException {
        val playerTarget = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        val skullStack = new ItemStack(Material.PLAYER_HEAD, 1);

        skullStack.editMeta(SkullMeta.class, skullMeta -> {
            skullMeta.setOwningPlayer(playerTarget);
            skullMeta.setPlayerProfile(playerTarget.getPlayerProfile());
            skullMeta.setNoteBlockSound(Registry.SOUNDS.getKey(Sound.ENTITY_PLAYER_HURT));
            EssentialsUtil.changeName(skullMeta, EssentialsUtil.getDisplayName(playerTarget)
                    .append(Component.text("´s Kopf", Colors.INFO))
                    .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
        });

        if (!source.getInventory().addItem(skullStack).isEmpty()) throw Exceptions.NO_SPACE_IN_INVENTORY.create(source);

        EssentialsUtil.sendSuccess(source, Component.text("Du hast ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(skullStack))
                .append(Component.text(" erhalten", Colors.SUCCESS)));
        return 1;
    }
}

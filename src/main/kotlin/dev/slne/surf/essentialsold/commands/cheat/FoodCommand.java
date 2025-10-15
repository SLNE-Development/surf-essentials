package dev.slne.surf.essentialsold.commands.cheat;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class FoodCommand extends EssentialsCommand {
    public FoodCommand() {
        super("feed", "feed [<players>]", "Feeds the players.");

        withRequirement(EssentialsUtil.checkPermissions(Permissions.FEED_SELF_PERMISSION, Permissions.FEED_OTHER_PERMISSION));

        executesNative((NativeResultingCommandExecutor) (sender, args) -> feed(sender.getCallee(), List.of(getPlayerOrException(sender))));
        then(playersArgument("players")
                .withPermission(Permissions.FEED_OTHER_PERMISSION)
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> feed(sender.getCallee(), args.getUnchecked("players"))));
    }

    private int feed(CommandSender source, Collection<Player> targetsUnchecked) throws WrapperCommandSyntaxException {
        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        int successfulFeeds = 0;

        for (Player target : targets) {
            target.setFoodLevel(20);
            target.setSaturation(20);
            target.sendHealthUpdate();
            successfulFeeds++;

            target.playSound(Sound.sound(builder -> {
                builder.type(org.bukkit.Sound.ENTITY_STRIDER_EAT);
                builder.volume(1f);
                builder.pitch(0f);
            }));

            EssentialsUtil.sendSuccess(target, Component.text("Du wurdest gefüttert!", Colors.GREEN));

        }

        boolean isSelf = source instanceof Player player && player == targets.iterator().next();
        if (successfulFeeds == 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(targets.iterator().next())
                    .append(Component.text(" wurde gefüttert!", Colors.SUCCESS)));
        } else if (successfulFeeds >= 1 && !isSelf) {
            EssentialsUtil.sendSuccess(source, Component.text(successfulFeeds, Colors.TERTIARY)
                    .append(Component.text(" Spieler wurden gefüttert!", Colors.SUCCESS)));
        }
        return successfulFeeds;
    }
}

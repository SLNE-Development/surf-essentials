package dev.slne.surf.essentialsold.commands.general.other;

import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentialsold.commands.EssentialsCommand;
import dev.slne.surf.essentialsold.utils.EssentialsUtil;
import dev.slne.surf.essentialsold.utils.color.Colors;
import dev.slne.surf.essentialsold.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.util.Collection;

public class TitlebroadcastCommand extends EssentialsCommand {
    public TitlebroadcastCommand() {
        super("titlebroadcast", "titlebroadcast <players> <message>", "Broadcast a title to the players");

        withPermission(Permissions.TITLE_BROADCAST_PERMISSION);

        then(playersArgument("players")
                .then(stringArgument("title")
                        .replaceSuggestions((info, builder) -> {
                            builder.suggest("\"!&cExample &atitle\"");
                            EssentialsUtil.suggestColors().suggest(info, builder);
                            return builder.buildFuture();
                        })
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcast(
                                sender.getCallee(),
                                args.getUnchecked("players"),
                                args.getUnchecked("title"),
                                null,
                                null,
                                null,
                                null
                        ))
                        .then(stringArgument("subTitle")
                                .replaceSuggestions((info, builder) -> {
                                    builder.suggest("\"!&cExample &asub-title\"");
                                    return EssentialsUtil.suggestColors().suggest(info, builder);
                                })
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcast(
                                        sender.getCallee(),
                                        args.getUnchecked("players"),
                                        args.getUnchecked("title"),
                                        args.getUnchecked("subTitle"),
                                        null,
                                        null,
                                        null
                                ))
                                .then(timeArgument("fadeIn")
                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcast(
                                                sender.getCallee(),
                                                args.getUnchecked("players"),
                                                args.getUnchecked("title"),
                                                args.getUnchecked("subTitle"),
                                                args.getUnchecked("fadeIn"),
                                                null,
                                                null
                                        ))
                                        .then(timeArgument("stay")
                                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcast(
                                                        sender.getCallee(),
                                                        args.getUnchecked("players"),
                                                        args.getUnchecked("title"),
                                                        args.getUnchecked("subTitle"),
                                                        args.getUnchecked("fadeIn"),
                                                        args.getUnchecked("stay"),
                                                        null
                                                ))
                                                .then(timeArgument("fadeOut")
                                                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> broadcast(
                                                                sender.getCallee(),
                                                                args.getUnchecked("players"),
                                                                args.getUnchecked("title"),
                                                                args.getUnchecked("subTitle"),
                                                                args.getUnchecked("fadeIn"),
                                                                args.getUnchecked("stay"),
                                                                args.getUnchecked("fadeOut")
                                                        ))))))));

    }


    private static int broadcast(
            @NotNull CommandSender source,
            Collection<Player> targetsUnchecked,
            String title,
            @Nullable String subTitle,
            @Nullable Integer fadeIn,
            @Nullable Integer stay,
            @Nullable Integer fadeOut
    ) throws WrapperCommandSyntaxException {

        val targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        subTitle = (subTitle == null) ? "" : subTitle;
        val titleComponent = EssentialsUtil.deserialize(title).colorIfAbsent(Colors.TERTIARY);
        val subTitleComponent = EssentialsUtil.deserialize(subTitle).colorIfAbsent(Colors.SECONDARY);

        int successfullyShowed = 0;
        fadeIn = ((fadeIn == null) ? 10 : fadeIn) * 50;
        stay = ((stay == null) ? 20 * 7 : stay) * 50;
        fadeOut = ((fadeOut == null) ? 10 : fadeOut) * 50;


        for (Audience target : targets) {
            target.showTitle(Title.title(
                    titleComponent,
                    subTitleComponent,
                    Title.Times.times(
                            Duration.ofMillis(fadeIn),
                            Duration.ofMillis(stay),
                            Duration.ofMillis(fadeOut)
                    )
            ));

            successfullyShowed++;
        }

        EssentialsUtil.sendSuccess(source, Component.text("Der ", Colors.SUCCESS)
                .append(Component.text("Titel", Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Titel: ", Colors.INFO)
                                .append(titleComponent)
                                .append(Component.newline())
                                .append(Component.text("Einblenden: ", Colors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(fadeIn / 50), Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("Dauer: ", Colors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(stay / 50), Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("Ausblenden: ", Colors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(fadeOut / 50), Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("Untertitel: ", Colors.INFO))
                                .append(subTitleComponent))))
                .append(Component.text(" wurde ", Colors.SUCCESS))
                .append(successfullyShowed == 1 ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(successfullyShowed, Colors.TERTIARY)
                        .append(Component.text(" Spielern", Colors.SUCCESS)))
                .append(Component.text(" gezeigt!", Colors.SUCCESS)));

        return successfullyShowed;
    }
}

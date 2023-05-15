package dev.slne.surf.essentials.commands.general.other;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundSetSubtitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class TitlebroadcastCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"titlebroadcast"};
    }

    @Override
    public String usage() {
        return "/titlebroadcast <players> <message>";
    }

    @Override
    public String description() {
        return "Broadcast a title to the players";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(Permissions.TITLE_BROADCAST_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("title", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("\"!&cExample &atitle\"");
                            return EssentialsUtil.suggestAllColorCodes(builder);
                        })
                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "title"),
                                null, null, null, null))

                        .then(Commands.argument("subtitle", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    builder.suggest("\"!&cExample &asub-title\"");
                                    return EssentialsUtil.suggestAllColorCodes(builder);
                                })
                                .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "title"),
                                        StringArgumentType.getString(context, "subtitle"), null, null, null))

                                .then(Commands.argument("fadeInTicks", IntegerArgumentType.integer(1))
                                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "title"),
                                                StringArgumentType.getString(context, "subtitle"), IntegerArgumentType.getInteger(context, "fadeInTicks"), null, null))

                                        .then(Commands.argument("stayTicks", IntegerArgumentType.integer(1))
                                                .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "title"),
                                                        StringArgumentType.getString(context, "subtitle"), IntegerArgumentType.getInteger(context, "fadeInTicks"),
                                                        IntegerArgumentType.getInteger(context, "stayTicks"), null))

                                                .then(Commands.argument("fadeOutTicks", IntegerArgumentType.integer(1))
                                                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "title"),
                                                                StringArgumentType.getString(context, "subtitle"), IntegerArgumentType.getInteger(context, "fadeInTicks"),
                                                                IntegerArgumentType.getInteger(context, "stayTicks"), IntegerArgumentType.getInteger(context, "fadeOutTicks")))))))));
    }

    private static int broadcast(@NotNull CommandSourceStack source, @NotNull Collection<ServerPlayer> targetsUnchecked, @NotNull String title,
                                 @Nullable String subTitle, @Nullable Integer fadeInTicks, @Nullable Integer stayTicks, @Nullable Integer fadeOutTicks) throws CommandSyntaxException{

        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);

        int successfullyShowed = 0;
        fadeInTicks = (fadeInTicks == null) ? 10 : fadeInTicks;
        stayTicks = (stayTicks == null) ? 20*7 : stayTicks;
        fadeOutTicks = (fadeOutTicks == null) ? 10 : fadeOutTicks;

        subTitle = (subTitle == null) ? "" : subTitle;

        Component titleComponent = EssentialsUtil.deserialize(title).colorIfAbsent(Colors.TERTIARY);
        Component subTitleComponent = EssentialsUtil.deserialize(subTitle).colorIfAbsent(Colors.SECONDARY);

        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks);
        ClientboundSetTitleTextPacket titleTextPacket = new ClientboundSetTitleTextPacket(PaperAdventure.asVanilla(titleComponent));
        ClientboundSetSubtitleTextPacket subtitleTextPacket = new ClientboundSetSubtitleTextPacket(PaperAdventure.asVanilla(subTitleComponent));

        for (ServerPlayer target : targets) {
            target.connection.send(animationPacket);
            target.connection.send(titleTextPacket);
            target.connection.send(subtitleTextPacket);
            successfullyShowed++;
        }

        if (source.isPlayer()){
            if (successfullyShowed == 1) {
                EssentialsUtil.sendSuccess(source, Component.text("Der ", Colors.SUCCESS)
                        .append(Component.text("Titel", Colors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Titel: ", Colors.INFO)
                                        .append(titleComponent)
                                        .append(Component.newline())
                                        .append(Component.text("Einblenden: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeInTicks), Colors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Dauer: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(stayTicks), Colors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Ausblenden: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), Colors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Untertitel: ", Colors.INFO))
                                        .append(subTitleComponent))))
                        .append(Component.text(" wurde ", Colors.SUCCESS))
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                        .append(Component.text(" gezeigt!", Colors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Der ", Colors.SUCCESS)
                        .append(Component.text("Titel", Colors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Titel: ", Colors.INFO)
                                        .append(titleComponent)
                                        .append(Component.newline())
                                        .append(Component.text("Einblenden: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeInTicks), Colors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Dauer: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(stayTicks), Colors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Ausblenden: ", Colors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), Colors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Untertitel: ", Colors.INFO))
                                        .append(subTitleComponent))))
                        .append(Component.text(" wurde ", Colors.SUCCESS))
                        .append(Component.text(successfullyShowed, Colors.TERTIARY))
                        .append(Component.text(" Spielern gezeigt!", Colors.SUCCESS)));
            }
        }else {
            if (successfullyShowed == 1) {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Showed ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(targets.iterator().next().getDisplayName())
                        .append(" the title!")
                        .withStyle(ChatFormatting.GREEN), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Showed ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(String.valueOf(successfullyShowed))
                        .withStyle(ChatFormatting.GOLD)
                        .append(" players the title!")
                        .withStyle(ChatFormatting.GREEN), false);
            }
        }
        return successfullyShowed;
    }
}

package dev.slne.surf.essentials.commands.general.other;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

public class TitlebroadcastCommand {

    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("titlebroadcast", TitlebroadcastCommand::literal).setUsage("/titlebroadcast <players> <title> [<subtitle>] [<fadeIn in ticks>] [<stay in ticks>] [fadeOut in ticks]")
                .setDescription("Broadcast a title to the targets");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TITLE_BROADCAST_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("title", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("\"!&cExample &atitle\"");
                            EssentialsUtil.suggestAllColorCodes(builder, context, "title");
                            return builder.buildFuture();
                        })
                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "title"),
                                null, null, null, null))

                        .then(Commands.argument("subtitle", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    builder.suggest("\"!&cExample &asub-title\"");
                                    EssentialsUtil.suggestAllColorCodes(builder, context, "subtitle");
                                    return builder.buildFuture();
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

        Component titleComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(title).colorIfAbsent(SurfColors.TERTIARY);
        Component subTitleComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(subTitle).colorIfAbsent(SurfColors.SECONDARY);

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
                EssentialsUtil.sendSuccess(source, Component.text("Der ", SurfColors.SUCCESS)
                        .append(Component.text("Titel", SurfColors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Titel: ", SurfColors.INFO)
                                        .append(titleComponent)
                                        .append(Component.newline())
                                        .append(Component.text("Einblenden: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeInTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Dauer: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(stayTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Ausblenden: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Untertitel: ", SurfColors.INFO))
                                        .append(subTitleComponent))))
                        .append(Component.text(" wurde ", SurfColors.SUCCESS))
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" gezeigt!", SurfColors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Der ", SurfColors.SUCCESS)
                        .append(Component.text("Titel", SurfColors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Titel: ", SurfColors.INFO)
                                        .append(titleComponent)
                                        .append(Component.newline())
                                        .append(Component.text("Einblenden: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeInTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Dauer: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(stayTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Ausblenden: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Untertitel: ", SurfColors.INFO))
                                        .append(subTitleComponent))))
                        .append(Component.text(" wurde ", SurfColors.SUCCESS))
                        .append(Component.text(successfullyShowed, SurfColors.TERTIARY))
                        .append(Component.text(" Spielern gezeigt!", SurfColors.SUCCESS)));
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

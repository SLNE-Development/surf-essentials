package dev.slne.surf.essentials.commands.general.other;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class ActionbarBroadcast {
    public ActionbarBroadcast() {
        SurfEssentials.registerPluginBrigadierCommand("actionbarbroadcast", this::literal);
    }


    private void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.ACTION_BAR_BROADCAST_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("actionbar", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("\"!&cExample &aactionbar\"");
                            return EssentialsUtil.suggestAllColorCodes(builder);
                        })
                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "actionbar"), null, null))

                        .then(Commands.argument("stayTicks", IntegerArgumentType.integer(1))
                                .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "actionbar"),
                                        IntegerArgumentType.getInteger(context, "stayTicks"), null))

                                .then(Commands.argument("fadeOutTicks", IntegerArgumentType.integer(1))
                                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "actionbar"),
                                                IntegerArgumentType.getInteger(context, "stayTicks"), IntegerArgumentType.getInteger(context, "fadeOutTicks")))))));
    }

    private int broadcast(@NotNull CommandSourceStack source, @NotNull Collection<ServerPlayer> targetsUnchecked, @NotNull String actionbar, @Nullable Integer stayTicks, @Nullable Integer fadeOutTicks) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);

        int successfullyShowed = 0;
        int fadeInTicks = 20;
        stayTicks = (stayTicks == null) ? 20 * 7 : stayTicks;
        fadeOutTicks = (fadeOutTicks == null) ? 10 : fadeOutTicks;


        Component actionBarText = EssentialsUtil.deserialize(actionbar).colorIfAbsent(Colors.TERTIARY);

        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks);
        ClientboundSetActionBarTextPacket actionBarTextPacket = new ClientboundSetActionBarTextPacket(PaperAdventure.asVanilla(actionBarText));

        for (ServerPlayer target : targets) {
            EssentialsUtil.sendPackets(target, animationPacket, actionBarTextPacket);
            successfullyShowed++;
        }

        EssentialsUtil.sendSuccess(source, Component.text("Die ", Colors.SUCCESS)
                .append(Component.text("Actionbar", Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Text: ", Colors.INFO)
                                .append(actionBarText)
                                .append(Component.newline())
                                .append(Component.text("Dauer: ", Colors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(stayTicks), Colors.TERTIARY)))
                                .append(Component.newline())
                                .append(Component.text("Ausblenden: ", Colors.INFO)
                                        .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), Colors.TERTIARY)))
                                .append(Component.newline()))))
                .append(Component.text(" wurde ", Colors.SUCCESS))
                .append((successfullyShowed == 1) ? EssentialsUtil.getDisplayName(targets.iterator().next()) : Component.text(successfullyShowed, Colors.TERTIARY)
                        .append(Component.text(" Spielern ", Colors.SUCCESS)))
                .append(Component.text(" gezeigt!", Colors.SUCCESS)));
        return successfullyShowed;
    }
}

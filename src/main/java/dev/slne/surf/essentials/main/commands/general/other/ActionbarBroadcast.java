package dev.slne.surf.essentials.main.commands.general.other;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@PermissionTag(name = Permissions.ACTION_BAR_BROADCAST_PERMISSION, desc = "This is the permission for the 'actionbarbroadcast' command")
public class ActionbarBroadcast {
    public ActionbarBroadcast(){
        SurfEssentials.registerPluginBrigadierCommand("actionbarbroadcast", this::literal).setUsage("/actionbarbroadcast <players> <actionbar> [<stay in ticks>] [fadeOut in ticks]")
                .setDescription("Broadcast a text in the action bar to the targets");
    }


    private void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.ACTION_BAR_BROADCAST_PERMISSION));

        literal.then(Commands.argument("players", EntityArgument.players())
                .then(Commands.argument("actionbar", StringArgumentType.string())
                        .suggests((context, builder) -> {
                            builder.suggest("\"!&cExample &aactionbar\"");
                            EssentialsUtil.suggestAllColorCodes(builder, context, "actionbar");
                            return builder.buildFuture();
                        })
                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "actionbar"), null, null))

                        .then(Commands.argument("stayTicks", IntegerArgumentType.integer(1))
                                .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "actionbar"),
                                        IntegerArgumentType.getInteger(context, "stayTicks"), null))

                                .then(Commands.argument("fadeOutTicks", IntegerArgumentType.integer(1))
                                        .executes(context -> broadcast(context.getSource(), EntityArgument.getPlayers(context, "players"), StringArgumentType.getString(context, "actionbar"),
                                                IntegerArgumentType.getInteger(context, "stayTicks"), IntegerArgumentType.getInteger(context, "fadeOutTicks")))))));
    }

    private int broadcast(@NotNull CommandSourceStack source, @NotNull Collection<ServerPlayer> targets, @NotNull String actionbar, @Nullable Integer stayTicks, @Nullable Integer fadeOutTicks) throws CommandSyntaxException {

        int successfullyShowed = 0;
        int fadeInTicks = 20;
        stayTicks = (stayTicks == null) ? 20*7 : stayTicks;
        fadeOutTicks = (fadeOutTicks == null) ? 10 : fadeOutTicks;


        Component actionBarText = LegacyComponentSerializer.legacyAmpersand().deserialize(actionbar).colorIfAbsent(SurfColors.TERTIARY);

        ClientboundSetTitlesAnimationPacket animationPacket = new ClientboundSetTitlesAnimationPacket(fadeInTicks, stayTicks, fadeOutTicks);
        ClientboundSetActionBarTextPacket actionBarTextPacket = new ClientboundSetActionBarTextPacket(PaperAdventure.asVanilla(actionBarText));

        for (ServerPlayer target : targets) {
            target.connection.send(animationPacket);
            target.connection.send(actionBarTextPacket);
            successfullyShowed++;
        }

        if (source.isPlayer()){
            if (successfullyShowed == 1) {
                EssentialsUtil.sendSuccess(source, Component.text("Die ", SurfColors.SUCCESS)
                        .append(Component.text("Actionbar", SurfColors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Text: ", SurfColors.INFO)
                                        .append(actionBarText)
                                        .append(Component.newline())
                                        .append(Component.text("Dauer: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(stayTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Ausblenden: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline()))))
                        .append(Component.text(" wurde ", SurfColors.SUCCESS))
                        .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                        .append(Component.text(" gezeigt!", SurfColors.SUCCESS)));
            }else {
                EssentialsUtil.sendSuccess(source, Component.text("Die ", SurfColors.SUCCESS)
                        .append(Component.text("Actionbar", SurfColors.TERTIARY)
                                .hoverEvent(HoverEvent.showText(Component.text("Text: ", SurfColors.INFO)
                                        .append(actionBarText)
                                        .append(Component.newline())
                                        .append(Component.text("Dauer: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(stayTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline())
                                        .append(Component.text("Ausblenden: ", SurfColors.INFO)
                                                .append(Component.text(EssentialsUtil.ticksToString(fadeOutTicks), SurfColors.TERTIARY)))
                                        .append(Component.newline()))))
                        .append(Component.text(" wurde ", SurfColors.SUCCESS))
                        .append(Component.text(successfullyShowed, SurfColors.TERTIARY))
                        .append(Component.text(" Spielern gezeigt!", SurfColors.SUCCESS)));
            }
        }else {
            if (successfullyShowed == 1) {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Showed ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(targets.iterator().next().getDisplayName())
                        .append(" the actionbar!")
                        .withStyle(ChatFormatting.GREEN), false);
            }else {
                source.sendSuccess(net.minecraft.network.chat.Component.literal("Showed ")
                        .withStyle(ChatFormatting.GREEN)
                        .append(String.valueOf(successfullyShowed))
                        .withStyle(ChatFormatting.GOLD)
                        .append(" players the actionbar!")
                        .withStyle(ChatFormatting.GREEN), false);
            }
        }
        return successfullyShowed;
    }
}

package dev.slne.surf.essentials.main.commands.general;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import dev.slne.surf.essentials.main.utils.brigadier.BrigadierCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

@PermissionTag(name = Permissions.GET_OWN_PING_PERMISSION, desc = "Get your own ping")
@PermissionTag(name = Permissions.GET_PING_FROM_OTHERS_PERMISSION, desc = "Get ping from other players")
public class PingCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"ping", "getPing"};
    }

    @Override
    public String usage() {
        return "/ping [<player>]";
    }

    @Override
    public String description() {
        return "Get your or another player's ping";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(1, Permissions.GET_OWN_PING_PERMISSION));
        literal.executes(context -> getOwnPing(context.getSource()));

        literal.then(Commands.argument("player", EntityArgument.player())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GET_PING_FROM_OTHERS_PERMISSION))
                .executes(context -> getOtherPing(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int getOwnPing(CommandSourceStack source) throws CommandSyntaxException {
        int ping = source.getPlayerOrException().latency;

        EssentialsUtil.sendSuccess(source, Component.text("pong! ", SurfColors.SUCCESS)
                .append(Component.text("(", SurfColors.INFO))
                .append(Component.text(ping, getPingColor(ping)))
                .append(Component.text("ms)", SurfColors.INFO)));

        return ping;
    }

    private int getOtherPing(CommandSourceStack source, ServerPlayer target) throws CommandSyntaxException{
        int ping = target.latency;

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Der Spieler ", SurfColors.INFO)
                    .append(target.adventure$displayName.colorIfAbsent(SurfColors.SECONDARY))
                    .append(Component.text(" hat einen Ping von ", SurfColors.INFO))
                    .append(Component.text(ping, getPingColor(ping)))
                    .append(Component.text("ms.", SurfColors.INFO)));
        }else {
            source.sendSuccess(target.getDisplayName()
                    .copy().append(" has a ping of " + ping + "ms.")
                    .withStyle(ChatFormatting.GRAY), false);
        }
        return ping;
    }

    private TextColor getPingColor(int ping){
        if (ping <= 20) return TextColor.fromHexString("#006400"); // Dark Green
        else if (ping <= 40) return TextColor.fromHexString("#90EE90"); // Light Green
        else if (ping <= 60) return TextColor.fromHexString("#9ACD32"); // Yellow-Green
        else if (ping <= 80) return TextColor.fromHexString("#FFFF00"); // Yellow
        else if (ping <= 100) return TextColor.fromHexString("#FFA500"); // Orange
        else if (ping <= 120) return TextColor.fromHexString("#FF69B4"); // Light-Red
        else return TextColor.fromHexString("#8B0000"); // Dark-Red
    }
}

package dev.slne.surf.essentials.commands.general;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

import java.text.DecimalFormat;

@PermissionTag(name = Permissions.GET_POS_SELF_PERMISSION, desc = "Allows you to get your position")
@PermissionTag(name = Permissions.GET_POS_OTHER_PERMISSION, desc = "Allows you to get others position")
public class GetPosCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("getpos", GetPosCommand::literal).setUsage("/getpos [<player>]")
                .setDescription("Gets the position from the target");
        SurfEssentials.registerPluginBrigadierCommand("position", GetPosCommand::literal).setUsage("/getpos [<player>]")
                .setDescription("Gets the position from the target");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GET_POS_SELF_PERMISSION));
        literal.executes(context -> getpos(context.getSource(), context.getSource().getPlayerOrException()));
        literal.then(Commands.argument("player", EntityArgument.player())
                .requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GET_POS_OTHER_PERMISSION))
                .executes(context -> getpos(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private static int getpos(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        ServerPlayer player = EssentialsUtil.checkSinglePlayerSuggestion(source, playerUnchecked);
        double posX = Double.parseDouble(new DecimalFormat("#.#").format(player.getX()));
        double posY = Double.parseDouble(new DecimalFormat("#.#").format(player.getY()));
        double posZ = Double.parseDouble(new DecimalFormat("#.#").format(player.getZ()));

        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Die Position von ", SurfColors.INFO))
                    .append(player.adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                    .append(Component.text(" ist: ", SurfColors.INFO))
                    .append(Component.text("%s, %s, %s".formatted(posX, posY, posZ), SurfColors.TERTIARY))));
        }else {
            source.sendSuccess(player.getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal("'s position: ")
                            .withStyle(ChatFormatting.GRAY)
                            .append(posX + ", " + posY + ", " + posZ)
                                    .withStyle(ChatFormatting.GOLD)), false);
        }
        return 1;
    }
}

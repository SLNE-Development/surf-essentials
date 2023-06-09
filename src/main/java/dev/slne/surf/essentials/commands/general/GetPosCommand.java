package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;

public class GetPosCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"getpos", "position"};
    }

    @Override
    public String usage() {
        return "/position [<target>]";
    }

    @Override
    public String description() {
        return "Get the position of the target";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.GET_POS_SELF_PERMISSION, Permissions.GET_POS_OTHER_PERMISSION));
        literal.executes(context -> getPos(context.getSource(), context.getSource().getPlayerOrException()));

        literal.then(Commands.argument("player", EntityArgument.player())
                .requires(EssentialsUtil.checkPermissions(Permissions.GET_POS_OTHER_PERMISSION))
                .executes(context -> getPos(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    private int getPos(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        ServerPlayer player = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked);
        double posX = EssentialsUtil.makeDoubleReadable(player.getX());
        double posY = EssentialsUtil.makeDoubleReadable(player.getY());
        double posZ = EssentialsUtil.makeDoubleReadable(player.getZ());


        EssentialsUtil.sendSuccess(source, Component.text("Die Position von ", Colors.INFO)
                .append(EssentialsUtil.getDisplayName(player))
                .append(Component.text(" ist: ", Colors.INFO))
                .append(Component.text("%s, %s, %s".formatted(posX, posY, posZ), Colors.TERTIARY)
                        .hoverEvent(HoverEvent.showText(Component.text("Klicke zum Kopieren", Colors.INFO)))
                        .clickEvent(ClickEvent.copyToClipboard("%s %s %s".formatted(posX, posY, posZ)))));

        return 1;
    }
}

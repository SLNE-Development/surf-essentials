package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class SetPlayerIdleTimeoutCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"setidletimeout"};
    }

    @Override
    public String usage() {
        return "/setidletimeout";
    }

    @Override
    public String description() {
        return "/setidletimeout [<minutes>]";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.SET_PLAYER_IDLE_TIMEOUT_PERMISSION));

        literal.executes(context -> queryIdleTimeout(context.getSource()));
        literal.then(Commands.argument("minutes", IntegerArgumentType.integer(0))
                .executes(context -> setIdleTimeout(context.getSource(), IntegerArgumentType.getInteger(context, "minutes"))));
    }

    private int queryIdleTimeout(@NotNull CommandSourceStack source) {
        final int timeout = source.getServer().getPlayerIdleTimeout();
        EssentialsUtil.sendSourceSuccess(source, Component.text("Das Untätigkeitslimit ist gerade auf ", Colors.INFO)
                .append(Component.text(timeout, Colors.VARIABLE_VALUE))
                .append(Component.text(timeout == 1 ? " Minute" : " Minuten", Colors.INFO))
                .append(Component.text(" gesetzt.", Colors.INFO)));
        return timeout;
    }

    @Contract("_, _ -> param2")
    private int setIdleTimeout(@NotNull CommandSourceStack source, int minutes) {
        source.getServer().setPlayerIdleTimeout(minutes);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Das Untätigkeitslimit wurde auf ", Colors.SUCCESS)
                .append(Component.text(minutes, Colors.VARIABLE_VALUE))
                .append(Component.text((minutes == 1) ? " Minute" : " Minuten", Colors.SUCCESS))
                .append(Component.text(" gesetzt.", Colors.SUCCESS)));

        return minutes;
    }
}

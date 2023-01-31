package dev.slne.surf.essentials.main.commands.cheat;

import aetherial.spigot.plugin.annotation.permission.PermissionTag;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.main.utils.EssentialsUtil;
import dev.slne.surf.essentials.main.utils.Permissions;
import dev.slne.surf.essentials.main.utils.brigadier.BrigadierCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

@PermissionTag(name = Permissions.INFINITY_PERMISSION, desc = "Allows you to use the 'infinity' command")
public class InfinityCommand extends BrigadierCommand {
    private static final List<ServerPlayer> playersInInfinity = new ArrayList<>();

    @Override
    public String[] names() {
        return new String[]{"infinity"};
    }

    @Override
    public String usage() {
        return "/infinity";
    }

    @Override
    public String description() {
        return "Never run out of the Item you currently holding";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.INFINITY_PERMISSION));

        literal.executes(context -> infinity(context.getSource()));
    }

    private int infinity(CommandSourceStack source) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        if (playersInInfinity.contains(player)){
            playersInInfinity.remove(player);
            EssentialsUtil.sendSuccess(source, "Du hast nun nicht mehr unbegrenzt Items");

            return 1;
        }

        playersInInfinity.add(player);
        EssentialsUtil.sendSuccess(source, "Du hast nun unbegrenzt Items");

        return 1;
    }

    public static List<ServerPlayer> getPlayersInInfinity() {
        return playersInInfinity;
    }
}

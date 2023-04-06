package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.List;

public class ListCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"list"};
    }

    @Override
    public String usage() {
        return "/list [<uuids>]";
    }

    @Override
    public String description() {
        return "Lists all players that are currently online and visible";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.LIST_PERMISSION));
        literal.executes(context -> listPlayerNames(context.getSource(), false));
        literal.then(Commands.literal("uuids")
                .executes(context -> listPlayerNames(context.getSource(), true)));
    }

    private int listPlayerNames(CommandSourceStack source, boolean withUUID) throws CommandSyntaxException {
        List<ServerPlayer> list = EssentialsUtil.checkPlayerSuggestionWithoutException(source, source.getServer().getPlayerList().getPlayers());

        if (source.isPlayer()) {
            ComponentBuilder<TextComponent, TextComponent.Builder> builder = Component.text();

            builder.append(Component.text("Es sind gerade ", Colors.INFO)
                    .append(Component.text(list.size(), Colors.TERTIARY))
                    .append(Component.text(" von ", Colors.INFO))
                    .append(Component.text(Bukkit.getServer().getMaxPlayers(), Colors.TERTIARY))
                    .append(Component.text(" Spielern online: ", Colors.INFO)));
            for (ServerPlayer serverPlayer : list) {
                if (withUUID) {
                    builder.append(Component.text("(%s)".formatted(serverPlayer.adventure$displayName.colorIfAbsent(Colors.TERTIARY)), Colors.TERTIARY))
                            .append(Component.text(" %s, ".formatted(serverPlayer.getUUID()), Colors.SECONDARY));
                }else {
                    builder.append(serverPlayer.adventure$displayName.colorIfAbsent(Colors.TERTIARY))
                            .append(Component.text(", ", Colors.INFO));
                }
            }
            EssentialsUtil.sendSuccess(source, builder.build());
            return 1;
        }

        source.sendSuccess(net.minecraft.network.chat.Component.literal("There are " + list + " of a max of "
                + source.getServer().getPlayerList().getMaxPlayers() + " players online: " + Arrays.toString(list.stream().map(player -> player.getDisplayName().getString()).toArray())), false);
        return list.size();
    }
}

package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;

public class BroadcastWorldCommand extends BrigadierCommand {

    @Override
    public String[] names() {
        return new String[]{"worldbroadcast", "worldalert", "broadcastworld"};
    }

    @Override
    public String usage() {
        return "/worldbroadcast <world> <message>";
    }

    @Override
    public String description() {
        return "Broadcast a message to a single world";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.BROADCAST_WORLD_PERMISSION));

        literal.then(Commands.argument("world", DimensionArgument.dimension())
                .then(Commands.argument("broadcast message", StringArgumentType.greedyString())
                        .executes(context -> broadcastWorld(context.getSource(), DimensionArgument.getDimension(context, "world"),
                                StringArgumentType.getString(context, "broadcast message")))
                        .suggests((context, builder) -> {
                            EssentialsUtil.suggestAllColorCodes(builder);
                            return builder.buildFuture();
                        })));
    }

    private int broadcastWorld(CommandSourceStack source, ServerLevel level, String message) {
        for (ServerPlayer player : level.players()) {
            EssentialsUtil.sendMessage(player, EssentialsUtil.deserialize(message).colorIfAbsent(Colors.TERTIARY));
            player.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1f);
        }

        EssentialsUtil.sendSuccess(source, Component.text("Es wurde eine Nachricht an alle Spieler in der Welt ", Colors.SUCCESS)
                .append(Component.text(level.dimension().location().toString(), Colors.TERTIARY))
                .append(Component.text(" geschickt!", Colors.SUCCESS)));
        return 1;
    }
}

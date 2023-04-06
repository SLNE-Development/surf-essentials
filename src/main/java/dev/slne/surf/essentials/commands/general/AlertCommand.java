package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import org.bukkit.Bukkit;

public class AlertCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"alert", "broadcast", "al"};
    }

    @Override
    public String usage() {
        return "/alert <message>";
    }

    @Override
    public String description() {
        return "Sends an alert message to all online players";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(EssentialsUtil.checkPermissions(Permissions.ALERT_PERMISSION));

        literal.then(Commands.argument("message", StringArgumentType.greedyString())
                .suggests((context, builder) -> {
                    EssentialsUtil.suggestAllColorCodes(builder, context);
                    return builder.buildFuture();
                })
                .executes(context -> alert(context.getSource(), StringArgumentType.getString(context, "message"))));
    }

    private static int alert(CommandSourceStack source, String message){
        Bukkit.broadcast(EssentialsUtil.getPrefix()
                .append(LegacyComponentSerializer.legacyAmpersand().deserialize(message).colorIfAbsent(Colors.TERTIARY)));

        for (ServerPlayer serverPlayer : source.getServer().getPlayerList().getPlayers()) {
            serverPlayer.playSound(SoundEvents.NOTE_BLOCK_BELL.value(), 1f, 1f);
        }
        return 1;
    }
}

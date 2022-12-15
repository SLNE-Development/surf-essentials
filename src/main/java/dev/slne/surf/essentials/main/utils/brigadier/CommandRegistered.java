package dev.slne.surf.essentials.main.utils.brigadier;

import com.destroystokyo.paper.brigadier.BukkitBrigadierCommandSource;
import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.minecraft.commands.Commands.literal;

public class CommandRegistered implements Listener {
    @SuppressWarnings({"rawtypes", "unchecked"})
    @EventHandler
    public void onCommandRegistered(final CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (!(event.getCommand() instanceof PluginBrigadierCommand pluginBrigadierCommand)) {
            return;
        }
        final LiteralArgumentBuilder<CommandSourceStack> node = literal(event.getCommandLabel());
        pluginBrigadierCommand.command().accept(node);
        event.setLiteral((LiteralCommandNode) node.build());
    }
}

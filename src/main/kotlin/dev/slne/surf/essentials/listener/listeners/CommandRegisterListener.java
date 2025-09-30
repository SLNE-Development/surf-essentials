package dev.slne.surf.essentials.listener.listeners;

import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import lombok.Getter;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.command.UnknownCommandEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Listener} that registers and handles commands in the server.
 */
public class CommandRegisterListener implements Listener {
    @Getter
    private static final List<String> commandLabels = new ArrayList<>();


    /**
     * Overrides Bukkit's default commands when they are getting registered to the server
     *
     * @param event the command registered event
     */
    /**
    @SuppressWarnings({"UnstableApiUsage"})
    @EventHandler
    public void onCommandRegistered(@NotNull CommandRegisteredEvent<BukkitBrigadierCommandSource> event) {
        if (commandLabels.contains(event.getCommandLabel())) event.setCancelled(true);


        final var label = event.getCommandLabel();

        switch (label.toLowerCase()) { // TODO
            case "help", "?" -> event.setCancelled(true);
           // case "reload", "rl" -> buildCommand(event, label, ReloadCommand.getOrCreateCommand(ReloadCommand.class));
        }

    }
    */

    /**
     * Handles unknown commands that are executed by players and sends a custom message
     *
     * @param event the unknown command event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onUnknownCommand(@NotNull UnknownCommandEvent event) {
        if (event.getSender() instanceof ConsoleCommandSender || event.getSender() instanceof RemoteConsoleCommandSender)
            return;

        if (event.message() == null || LegacyComponentSerializer.builder().build().serialize(event.message()).equalsIgnoreCase(EssentialsUtil.unknowCommandMessage())) {
            event.message(null);
            event.getSender().sendMessage(EssentialsUtil.getPrefix()
                    .append(Component.translatable("command.unknown.command", Colors.ERROR))
                    .appendNewline()
                    .append(EssentialsUtil.getPrefix())
                    .append(Component.text(event.getCommandLine(), Colors.RED, TextDecoration.UNDERLINED)
                            .clickEvent(ClickEvent.suggestCommand("/%s".formatted(event.getCommandLine()))))
                    .append(Component.translatable("command.context.here", Colors.ERROR)));
            return;
        }

        val lines = LegacyComponentSerializer.legacySection().serialize(event.message()).split("\\n");
        val builder = net.kyori.adventure.text.Component.text();

        if (lines[0] == null) return;
        builder.append(EssentialsUtil.getPrefix().append(EssentialsUtil.deserialize(lines[0])
                .colorIfAbsent(Colors.GRAY)));

        for (int i = 1; i < lines.length; i++) {
            builder.append(Component.newline()
                    .append(EssentialsUtil.getPrefix().append(EssentialsUtil.deserialize(lines[i]))
                            .colorIfAbsent(Colors.GRAY)));
        }
        event.message(builder.build());
    }
}

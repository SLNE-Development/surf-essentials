package dev.slne.surf.essentials.listener

import dev.slne.surf.essentials.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.clickSuggestsCommand
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.command.RemoteConsoleCommandSender
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.command.UnknownCommandEvent

object UnknownCommandListener : Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    fun onUnknownCommand(event: UnknownCommandEvent) {
        if (event.sender is ConsoleCommandSender || event.sender is RemoteConsoleCommandSender) {
            return
        }

        if (event.message() == null) {
            return
        }

        event.message(buildText {
            appendPrefix()
            translatable("command.unknown.command").color(Colors.ERROR)

            appendNewline {
                appendPrefix()
                error(event.commandLine, TextDecoration.UNDERLINED)
            }
            clickSuggestsCommand("/${event.commandLine}")
            translatable("command.context.here").color(Colors.ERROR)
        })
    }
}
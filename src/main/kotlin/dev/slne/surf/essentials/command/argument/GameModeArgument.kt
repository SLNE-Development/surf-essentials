package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

class GameModeArgument(nodeName: String) :
    CustomArgument<GameMode, String>(StringArgument(nodeName), { info ->
        val gameMode = getGameMode(info.input.lowercase())
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Spielmodus wurde nicht gefunden.")
                }
            }

        val permission = "surf.essentials.gameMode.${gameMode.name.lowercase()}"
        val sender = info.sender

        if (!sender.hasPermission(permission)) {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Dazu hast du keine Berechtigung.")
                }
            }
        }

        gameMode
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> { sender ->
                GameMode.entries
                    .filter { sender.sender.hasPermission("surf.essentials.gameMode.${it.name.lowercase()}") }
                    .map { it.name.lowercase() }
            }
        )
    }
}

private fun getGameMode(gameModeValue: String) = when (gameModeValue) {
    "survival", "s", "0" -> GameMode.SURVIVAL
    "creative", "c", "1" -> GameMode.CREATIVE
    "adventure", "a", "2" -> GameMode.ADVENTURE
    "spectator", "sp", "3" -> GameMode.SPECTATOR
    else -> null
}

inline fun Argument<*>.gameModeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    GameModeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.gameModeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    GameModeArgument(nodeName).setOptional(optional).apply(block)
)

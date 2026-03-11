package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.GameMode
import org.bukkit.command.CommandSender

class GameModeArgument(nodeName: String) :
    CustomArgument<GameMode, String>(StringArgument(nodeName), { info ->
        val input = info.input.lowercase()
        val gameMode = getGameMode(input)
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Spielmodus wurde nicht gefunden.")
                }
            }

        val sender = info.sender
        if (sender != null) {
            val base = EssentialsPermissionRegistry.GAME_MODE_COMMAND
            val specific = "$base.${gameMode.name.lowercase()}"
            val wildcard = "$base.*"

            if (!sender.hasPermission(specific) &&
                !sender.hasPermission(wildcard)
            ) {
                throw CustomArgumentException.fromAdventureComponent {
                    buildText {
                        appendErrorPrefix()
                        error("Du hast keine Berechtigung für diesen Spielmodus.")
                    }
                }
            }
        }

        gameMode
    }) {

    init {
        replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> { sender ->
                val base = EssentialsPermissionRegistry.GAME_MODE_COMMAND
                val wildcard = "$base.*"

                GameMode.entries
                    .filter {
                        sender.sender.hasPermission(wildcard) ||
                                sender.sender.hasPermission("$base.${it.name.lowercase()}")
                    }
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

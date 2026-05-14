package dev.slne.surf.essentials.command.argument

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.GameMode
import org.bukkit.permissions.Permissible

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
            if (!hasPermissionForGameMode(sender, gameMode)) {
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
            ArgumentSuggestions.stringCollection { sender ->
                GameMode.entries
                    .filter { hasPermissionForGameMode(sender.sender, it) }
                    .map { it.name.lowercase() }
            }
        )
    }
}

fun hasPermissionForGameMode(permissible: Permissible, gameMode: GameMode) =
    permissible.hasPermission(EssentialsPermissionRegistry.GAME_MODE_WILDCARD) ||
            permissible.hasPermission(permissionForGameMode(gameMode))

fun permissionForGameMode(gameMode: GameMode) =
    "${EssentialsPermissionRegistry.GAME_MODE_BASE}.${gameMode.name.lowercase()}"

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

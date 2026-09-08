package dev.slne.surf.essentials.command.argument.world

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.buildText

class WorldTypeArgument(nodeName: String) :
    CustomArgument<WorldTypeArgument.WorldType, String>(StringArgument(nodeName), { info ->
        WorldType.entries.firstOrNull { it.name == info.input.uppercase() }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Welttyp wurde nicht gefunden.")
                }
            }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                WorldType.entries.map {
                    it.name.lowercase()
                }
            }
        )
    }

    enum class WorldType {
        NORMAL,
        FLAT,
        LARGE_BIOMES,
        AMPLIFIED,
        VOID;

        fun vanilla(): org.bukkit.WorldType = when (this) {
            NORMAL -> org.bukkit.WorldType.NORMAL
            FLAT -> org.bukkit.WorldType.FLAT
            LARGE_BIOMES -> org.bukkit.WorldType.LARGE_BIOMES
            AMPLIFIED -> org.bukkit.WorldType.AMPLIFIED
            VOID -> org.bukkit.WorldType.NORMAL
        }
    }
}

inline fun Argument<*>.worldTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    WorldTypeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.worldTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    WorldTypeArgument(nodeName).setOptional(optional).apply(block)
)
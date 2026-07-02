package dev.slne.surf.essentials.command.argument

import com.github.shynixn.mccoroutine.folia.scope
import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.GreedyStringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.essentials.plugin
import kotlinx.coroutines.future.future
import org.bukkit.entity.EntityType

class HashTagEntityTypeArgument(nodeName: String) :
    CustomArgument<EntityType, String>(GreedyStringArgument(nodeName), { info ->
        val type = info.input.removePrefix("#")
        EntityType.entries.firstOrNull { it.name.equals(type, ignoreCase = true) }
            ?: throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Entitytyp wurde nicht gefunden.")
                }
            }

    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollectionAsync {
                plugin.scope.future {
                    EntityType.entries.map { "#${it.name.lowercase()}" }
                }
            }
        )
    }
}

inline fun Argument<*>.hashTagEntityTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    HashTagEntityTypeArgument(nodeName).setOptional(optional).apply(block)
)

inline fun CommandTree.hashTagEntityTypeArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    HashTagEntityTypeArgument(nodeName).setOptional(optional).apply(block)
)
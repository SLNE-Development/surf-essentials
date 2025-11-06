package dev.slne.surf.essentials.command.argument.world

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.io.File

class WorldFoldersArgument(nodeName: String) :
    CustomArgument<String, String>(StringArgument(nodeName), { info ->
        if (File(Bukkit.getWorldContainer(), info.input).exists()) {
            info.input
        } else {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendPrefix()
                    error("Der Welten-Ordner wurde nicht gefunden.")
                }
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection<CommandSender> {
                Bukkit.getWorldContainer().listFiles().filter { isMinecraftWorldFolder(it) }
                    .map { it.name }
            }
        )
    }
}

private fun isMinecraftWorldFolder(folder: File): Boolean {
    if (!folder.isDirectory) return false

    val levelDat = File(folder, "level.dat")
    val regionFolder = File(folder, "region")

    return levelDat.exists() && regionFolder.exists() && regionFolder.isDirectory
}


inline fun CommandTree.worldFoldersArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): CommandTree = then(
    WorldFoldersArgument(nodeName).setOptional(optional).apply(block)
)

inline fun Argument<*>.worldFoldersArgument(
    nodeName: String,
    optional: Boolean = false,
    block: Argument<*>.() -> Unit = {}
): Argument<*> = then(
    WorldFoldersArgument(nodeName).setOptional(optional).apply(block)
)
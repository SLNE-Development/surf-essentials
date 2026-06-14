package dev.slne.surf.essentials.command.argument.world

import dev.jorel.commandapi.CommandTree
import dev.jorel.commandapi.arguments.Argument
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.arguments.CustomArgument
import dev.jorel.commandapi.arguments.StringArgument
import dev.slne.surf.api.core.messages.adventure.buildText
import org.bukkit.Bukkit
import java.io.File


val worldPath: File
    get() = Bukkit.getServer().levelDirectory.resolve("dimensions")
        .resolve("minecraft").toFile()

class WorldFoldersArgument(nodeName: String) :
    CustomArgument<String, String>(StringArgument(nodeName), { info ->
        if (File(worldPath, info.input).exists()) {
            info.input
        } else {
            throw CustomArgumentException.fromAdventureComponent {
                buildText {
                    appendErrorPrefix()
                    error("Der Welten-Ordner wurde nicht gefunden.")
                }
            }
        }
    }) {
    init {
        this.replaceSuggestions(
            ArgumentSuggestions.stringCollection {
                worldPath.listFiles()
                    ?.filter(::isMinecraftWorldFolder)
                    ?.map(File::getName)
                    ?: emptyList()
            }
        )
    }
}

private fun isMinecraftWorldFolder(folder: File): Boolean {
    if (!folder.isDirectory) return false

    val regionFolder = File(folder, "region")

    return regionFolder.exists() && regionFolder.isDirectory
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
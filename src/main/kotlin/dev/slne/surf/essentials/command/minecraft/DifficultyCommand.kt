package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.difficultyArgument
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.translatable
import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.Difficulty
import org.bukkit.World

fun difficultyCommand() = commandTree("difficulty") {
    withPermission(EssentialsPermissionRegistry.DIFFICULTY_COMMAND)
    playerExecutor { player, _ ->
        player.sendText {
            appendPrefix()
            info("Der Schwierigkeitsgrad der Welt ")
            variableValue(player.world.name)
            info(" ist auf ")
            translatable(player.world.difficulty.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
            info(" gesetzt.")
        }
    }
    difficultyArgument("difficulty") {
        playerExecutor { player, args ->
            val difficulty: Difficulty by args

            Bukkit.getWorlds().forEach { it.difficulty = difficulty }
            player.sendText {
                appendPrefix()
                success("Du hast den Schwierigkeitsgrad aller Welten auf ")
                translatable(difficulty.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                success(" gesetzt.")
            }
        }

        worldArgument("world") {
            anyExecutor { executor, args ->
                val difficulty: Difficulty by args
                val world: World by args

                if (difficulty == world.difficulty) {
                    executor.sendText {
                        appendPrefix()
                        error("Der Schwierigkeitsgrad der Welt ")
                        variableValue(world.name)
                        error(" ist bereits auf ")
                        translatable(difficulty.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                        error(" gesetzt.")
                    }
                    return@anyExecutor
                }

                world.difficulty = difficulty

                executor.sendText {
                    appendPrefix()
                    success("Du hast den Schwierigkeitsgrad der Welt ")
                    variableValue(world.name)
                    success(" auf ")
                    translatable(difficulty.translationKey()).colorIfAbsent(Colors.VARIABLE_VALUE)
                    success(" gesetzt.")
                }
            }
        }
    }
}
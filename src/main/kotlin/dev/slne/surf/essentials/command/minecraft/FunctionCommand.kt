package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.jorel.commandapi.wrappers.FunctionWrapper
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.isFolia
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.Bukkit
import org.bukkit.entity.Entity

fun functionCommand() = commandTree("function") {
    withPermission(EssentialsPermissionRegistry.FUNCTION_COMMAND)
    functionArgument("function") {
        anyExecutor { executor, args ->
            if (Bukkit.getServer().isFolia()) {
                executor.sendText {
                    appendPrefix()
                    error("Der Function-Befehl wird auf Folia-Servern nicht unterstützt.")
                }
                return@anyExecutor
            }

            val function: FunctionWrapper by args
            function.run()

            executor.sendText {
                appendPrefix()
                success("Die Funktion ")
                variableValue(function.key.toString())
                success(" wurde ausgeführt.")
            }
        }

        literalArgument("as") {
            entitySelectorArgumentOneEntity("entity") {
                anyExecutor { executor, args ->
                    if (Bukkit.getServer().isFolia()) {
                        executor.sendText {
                            appendPrefix()
                            error("Der Function-Befehl wird auf Folia-Servern nicht unterstützt.")
                        }
                        return@anyExecutor
                    }

                    val function: FunctionWrapper by args
                    val entity: Entity by args

                    function.runAs(entity)

                    executor.sendText {
                        appendPrefix()
                        success("Die Funktion ")
                        variableValue(function.key.toString())
                        success(" wurde als ")
                        variableValue(entity.name)
                        success(" ausgeführt.")
                    }
                }
            }
        }
    }
}
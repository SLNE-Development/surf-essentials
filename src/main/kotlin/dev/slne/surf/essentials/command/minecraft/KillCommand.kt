package dev.slne.surf.essentials.command.minecraft

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun killCommand() = commandTree("kill") {
    withPermission(EssentialsPermissionRegistry.KILL_COMMAND)
    playerExecutor { player, _ ->
        player.health = 0.0
        player.sendHealthUpdate()

        player.sendText {
            appendPrefix()
            success("Du wurdest getötet.")
        }
    }

    entitySelectorArgumentManyEntities("targets") {
        withPermission(EssentialsPermissionRegistry.KILL_COMMAND_OTHERS)
        anyExecutor { executor, args ->
            val targets: Collection<Entity> by args

            targets.forEach {
                if (it is LivingEntity) {
                    it.health = 0.0
                    if (it is Player) {
                        it.sendHealthUpdate()
                        it.sendText {
                            appendPrefix()
                            success("Du wurdest von ")
                            variableValue(executor.name)
                            success(" getötet.")
                        }
                    }
                } else {
                    it.remove()
                }
            }

            executor.sendText {
                appendPrefix()
                success("Du hast ")
                variableValue(targets.size.toString())
                success(" Entität(en) getötet.")
            }
        }
    }
}
package dev.slne.surf.essentials.command.minecraft

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.globalRegionDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.essentials.command.argument.hashTagEntityTypeArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import kotlinx.coroutines.withContext
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player

fun killCommand() = commandTree("kill") {
    withPermission(EssentialsPermissionRegistry.KILL_COMMAND)
    playerExecutor { player, _ ->
        player.health = 0.0
        player.sendHealthUpdate()

        player.sendText {
            appendSuccessPrefix()
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
                            appendSuccessPrefix()
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
                appendSuccessPrefix()
                success("Du hast ")
                variableValue(targets.size.toString())
                success(" Entität(en) getötet.")
            }
        }
    }

    hashTagEntityTypeArgument("entityType") {
        withPermission(EssentialsPermissionRegistry.KILL_COMMAND_HASHTAG)
        anyExecutor { sender, arguments ->
            val entityType: EntityType by arguments

            if (entityType == EntityType.PLAYER) {
                sender.sendText {
                    appendErrorPrefix()
                    error("Du kannst keine Spieler mit diesem Befehl töten. Verwende stattdessen /kill @a.")
                }
                return@anyExecutor
            }

            var amount = 0

            plugin.launch(plugin.globalRegionDispatcher) {
                server.worlds.forEach { world ->
                    world.entities.filter { it.type == entityType }.forEach { entity ->
                        withContext(plugin.entityDispatcher(entity)) {
                            entity.remove()
                            amount += 1
                        }
                    }
                }
            }

            if (amount > 0) {
                sender.sendText {
                    appendSuccessPrefix()
                    success("Du hast ")
                    variableValue(amount.toString())
                    success(" Entität(en) vom Typ ")
                    translatable(entityType.translationKey())
                    success(" getötet.")
                }
            } else {
                sender.sendText {
                    appendErrorPrefix()
                    error("Es wurden keine Entitäten vom Typ ")
                    translatable(entityType.translationKey())
                    error(" gefunden.")
                }
            }
        }
    }
}
package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.world.worldFoldersArgument
import dev.slne.surf.essentials.command.argument.world.worldsArgument
import dev.slne.surf.essentials.service.worldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.isFolia
import dev.slne.surf.surfapi.core.api.font.toSmallCaps
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.pagination.Pagination
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.World

fun worldCommand() = commandTree("world") {
    withPermission(EssentialsPermissionRegistry.WORLD_COMMAND)

    literalArgument("lock") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_LOCK)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                if (worldService.isLocked(world)) {
                    executor.sendText {
                        appendPrefix()
                        error("Die Welt ist bereits gesperrt.")
                    }
                    return@anyExecutor
                }

                worldService.lock(world)
                executor.sendText {
                    appendPrefix()
                    success("Die Welt ")
                    variableValue(world.name)
                    success(" wurde gesperrt.")
                }
            }
        }
    }

    literalArgument("unlock") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_UNLOCK)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                if (!worldService.isLocked(world)) {
                    executor.sendText {
                        appendPrefix()
                        error("Die Welt ist nicht gesperrt.")
                    }
                    return@anyExecutor
                }

                worldService.unlock(world)
                executor.sendText {
                    appendPrefix()
                    success("Die Welt ")
                    variableValue(world.name)
                    success(" wurde entsperrt.")
                }
            }
        }
    }

    literalArgument("join") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_JOIN)
            playerExecutor { player, args ->
                val world: World by args

                player.sendText {
                    appendPrefix()
                    info("Du wirst in die Welt ")
                    variableValue(world.name)
                    info(" teleportiert...")
                }

                player.teleportAsync(world.spawnLocation).thenRun {
                    player.sendText {
                        appendPrefix()
                        success("Du wurdest in die Welt ")
                        variableValue(world.name)
                        success(" teleportiert.")
                    }
                }
            }
        }
    }

    literalArgument("create") {
        stringArgument("name") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_CREATE)
            anyExecutor { executor, args ->
                val name: String by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                worldService.create(executor, name, null, null, null, null, null)
            }
        }
    }

    literalArgument("delete") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_DELETE)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                worldService.delete(executor, world)
            }
        }
    }

    literalArgument("load") {
        worldFoldersArgument("name") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_LOAD)
            anyExecutor { executor, args ->
                val name: String by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }


                worldService.load(executor, name)
            }
        }
    }

    literalArgument("unload") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_UNLOAD)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                worldService.unload(executor, world)
            }
        }
    }

    literalArgument("list") {
        anyExecutor { executor, _ ->
            val worlds = Bukkit.getWorlds()

            if (worlds.isEmpty()) {
                executor.sendText {
                    appendPrefix()
                    error("Es sind keine Welten geladen.")
                }
                return@anyExecutor
            }

            val worldData = worlds.map {
                WorldData(it.name, worldService.isLocked(it))
            }

            val pagination = Pagination<WorldData> {
                title {
                    primary("Geladene Welten".toSmallCaps(), TextDecoration.BOLD)
                }

                rowRenderer { row, index ->
                    listOf(
                        buildText {
                            darkSpacer(">")
                            appendSpace()
                            variableValue(row.worldName)
                            appendSpace()
                            spacer("(")
                            if (row.isLocked) {
                                error("Gesperrt".toSmallCaps())
                            } else {
                                success("Entsperrt".toSmallCaps())
                            }
                            spacer(")")
                        }
                    )
                }
            }

            executor.sendText {
                appendPrefix()
                info("Es sind insgesamt ")
                variableValue(worlds.size.toString())
                info(" Welt(en) geladen:")
                append(pagination.renderComponent(worldData))
            }
        }
    }
}

private data class WorldData(
    val worldName: String,
    val isLocked: Boolean
)
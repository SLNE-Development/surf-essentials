package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.api.core.font.toSmallCaps
import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.api.core.messages.pagination.Pagination
import dev.slne.surf.api.paper.SurfApiPaper
import dev.slne.surf.api.paper.command.executors.anyExecutorSuspend
import dev.slne.surf.essentials.command.argument.world.worldEnvironmentArgument
import dev.slne.surf.essentials.command.argument.world.worldFoldersArgument
import dev.slne.surf.essentials.command.argument.world.worldTypeArgument
import dev.slne.surf.essentials.command.argument.world.worldsArgument
import dev.slne.surf.essentials.service.WorldService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import dev.slne.surf.essentials.util.util.isFolia
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.WorldType
import org.bukkit.entity.Player

fun worldCommand() = commandTree("world") {
    withPermission(EssentialsPermissionRegistry.WORLD_COMMAND)

    literalArgument("lock") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_LOCK)
            anyExecutor { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia() && !SurfApiPaper.isCanvasMc) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Dieser Befehl wird auf Folia-Non-Canvas-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                if (WorldService.isLocked(world)) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Die Welt ist bereits gesperrt.")
                    }
                    return@anyExecutor
                }

                WorldService.lock(world)
                executor.sendText {
                    appendSuccessPrefix()
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

                if (Bukkit.getServer().isFolia() && !SurfApiPaper.isCanvasMc) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Dieser Befehl wird auf Folia-Non-Canvas-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                if (!WorldService.isLocked(world)) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Die Welt ist nicht gesperrt.")
                    }
                    return@anyExecutor
                }

                WorldService.unlock(world)
                executor.sendText {
                    appendSuccessPrefix()
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
                    appendInfoPrefix()
                    info("Du wirst in die Welt ")
                    variableValue(world.name)
                    info(" teleportiert...")
                }

                player.teleportAsync(world.spawnLocation).thenRun {
                    player.sendText {
                        appendInfoPrefix()
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
                        appendErrorPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }

                WorldService.create(executor, name, null, null, null, null, null)
            }

            worldEnvironmentArgument("environment") {
                anyExecutor { executor, args ->
                    val name: String by args
                    val environment: World.Environment by args

                    if (Bukkit.getServer().isFolia()) {
                        executor.sendText {
                            appendErrorPrefix()
                            error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                        }
                        return@anyExecutor
                    }

                    WorldService.create(executor, name, environment, null, null, null, null)
                }

                worldTypeArgument("type") {
                    anyExecutor { executor, args ->
                        val name: String by args
                        val environment: World.Environment by args
                        val type: WorldType by args

                        if (Bukkit.getServer().isFolia()) {
                            executor.sendText {
                                appendErrorPrefix()
                                error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                            }
                            return@anyExecutor
                        }

                        WorldService.create(executor, name, environment, type, null, null, null)
                    }

                    booleanArgument("generateStructures") {
                        anyExecutor { executor, args ->
                            val name: String by args
                            val environment: World.Environment by args
                            val type: WorldType by args
                            val generateStructures: Boolean by args

                            if (Bukkit.getServer().isFolia()) {
                                executor.sendText {
                                    appendErrorPrefix()
                                    error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                                }
                                return@anyExecutor
                            }

                            WorldService.create(
                                executor,
                                name,
                                environment,
                                type,
                                generateStructures,
                                null,
                                null
                            )
                        }
                        booleanArgument("hardcore") {
                            anyExecutor { executor, args ->
                                val name: String by args
                                val environment: World.Environment by args
                                val type: WorldType by args
                                val generateStructures: Boolean by args
                                val hardcore: Boolean by args

                                if (Bukkit.getServer().isFolia()) {
                                    executor.sendText {
                                        appendErrorPrefix()
                                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                                    }
                                    return@anyExecutor
                                }

                                WorldService.create(
                                    executor,
                                    name,
                                    environment,
                                    type,
                                    generateStructures,
                                    hardcore,
                                    null
                                )
                            }

                            longArgument("seed") {
                                anyExecutor { executor, args ->
                                    val name: String by args
                                    val environment: World.Environment by args
                                    val type: WorldType by args
                                    val generateStructures: Boolean by args
                                    val hardcore: Boolean by args
                                    val seed: Long by args

                                    if (Bukkit.getServer().isFolia()) {
                                        executor.sendText {
                                            appendErrorPrefix()
                                            error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                                        }
                                        return@anyExecutor
                                    }

                                    WorldService.create(
                                        executor,
                                        name,
                                        environment,
                                        type,
                                        generateStructures,
                                        hardcore,
                                        seed
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    literalArgument("delete") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_DELETE)
            anyExecutorSuspend { executor, args ->
                val world: World by args

                if (Bukkit.getServer().isFolia()) {
                    executor.sendText {
                        appendErrorPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutorSuspend
                }

                WorldService.delete(executor, world)
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
                        appendErrorPrefix()
                        error("Dieser Befehl wird auf Folia-Servern nicht unterstützt.")
                    }
                    return@anyExecutor
                }


                WorldService.load(executor, name)
            }
        }
    }

    literalArgument("unload") {
        worldsArgument("world") {
            withPermission(EssentialsPermissionRegistry.WORLD_COMMAND_UNLOAD)
            anyExecutorSuspend { executor, args ->
                val world: World by args
                WorldService.unload(executor, world)
            }
        }
    }

    literalArgument("list") {
        anyExecutor { executor, _ ->
            val worlds = Bukkit.getWorlds()

            if (worlds.isEmpty()) {
                executor.sendText {
                    appendErrorPrefix()
                    error("Es sind keine Welten geladen.")
                }
                return@anyExecutor
            }

            val worldData = worlds.map {
                WorldData(it.name, WorldService.isLocked(it))
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
                            hoverEvent(buildText {
                                info("Klicke, um dich zu teleportieren.")
                            })
                            clickEvent(ClickEvent.callback {
                                val world = Bukkit.getWorld(row.worldName)

                                if (world == null) {
                                    executor.sendText {
                                        appendErrorPrefix()
                                        error("Die Welt ${row.worldName} ist nicht mehr geladen.")
                                    }
                                    return@callback
                                }

                                val player = it as? Player ?: run {
                                    executor.sendText {
                                        appendErrorPrefix()
                                        error("Du musst ein Spieler sein, um teleportiert zu werden.")
                                    }
                                    return@callback
                                }

                                player.sendText {
                                    appendInfoPrefix()
                                    info("Du wirst in die Welt ")
                                    variableValue(world.name)
                                    info(" teleportiert...")
                                }

                                player.teleportAsync(world.spawnLocation).thenRun {
                                    player.sendText {
                                        appendSuccessPrefix()
                                        success("Du wurdest in die Welt ")
                                        variableValue(world.name)
                                        success(" teleportiert.")
                                    }
                                }
                            })
                        }
                    )
                }
            }

            executor.sendText {
                appendInfoPrefix()
                info("Es sind insgesamt ")
                variableValue(worlds.size.toString())
                info(" Welt(en) geladen:")
                appendNewline()
                append(pagination.renderComponent(worldData))
            }
        }
    }
}

private data class WorldData(
    val worldName: String,
    val isLocked: Boolean
)
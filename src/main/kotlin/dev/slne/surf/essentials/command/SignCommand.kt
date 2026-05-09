package dev.slne.surf.essentials.command

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.greedyStringArgument
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.messages.adventure.sendText
import dev.slne.surf.essentials.service.SignService
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import net.luckperms.api.LuckPermsProvider
import java.util.*
import kotlin.time.Duration.Companion.minutes

private val cooldown = Caffeine.newBuilder().expireAfterWrite(30.minutes).build<UUID, Unit>()

fun signCommand() = commandTree("sign") {
    withPermission(EssentialsPermissionRegistry.SIGN_COMMAND)
    greedyStringArgument("text", optional = true) {
        playerExecutor { player, args ->
            val text: String? by args
            val item = player.inventory.itemInMainHand

            if (item.isEmpty) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du musst ein Item in der Hand halten.")
                }
                return@playerExecutor
            }

            if (cooldown.getIfPresent(player.uniqueId) != null && !player.hasPermission(
                    EssentialsPermissionRegistry.SIGN_COMMAND_BYPASS
                )
            ) {
                player.sendText {
                    appendErrorPrefix()
                    error("Du kannst nur alle 30 Minuten ein Item signieren.")
                }
                return@playerExecutor
            }

            val prefix =
                LuckPermsProvider.get().userManager.getUser(player.uniqueId)?.cachedData?.metaData?.prefix
                    ?: ""

            SignService.sign(item, "$prefix${player.name}", text)

            player.sendText {
                appendSuccessPrefix()
                success("Das Item wurde signiert.")
            }

            cooldown.put(player.uniqueId, Unit)
        }
    }
}
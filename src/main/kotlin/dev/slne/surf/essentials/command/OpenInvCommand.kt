package dev.slne.surf.essentials.command

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import dev.jorel.commandapi.arguments.ArgumentSuggestions
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.getValue
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.jorel.commandapi.kotlindsl.stringArgument
import dev.slne.surf.essentials.plugin
import dev.slne.surf.essentials.service.inspect.InspectService
import dev.slne.surf.essentials.service.inspect.InspectType
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun openInvCommand() = commandTree("openinv") {
    withPermission(EssentialsPermissionRegistry.OPENINV_COMMAND)
    withAliases("invsee")
    stringArgument("target") {
        replaceSuggestions(ArgumentSuggestions.stringCollection<CommandSender> {
            Bukkit.getOnlinePlayers().map { it.name }
        })
        playerExecutor { player, args ->
            val target: String by args

            plugin.launch(plugin.entityDispatcher(player)) {
                InspectService.open(player, target, InspectType.INVENTORY)
            }
        }
    }
}

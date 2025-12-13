package dev.slne.surf.essentials.util

import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey

object GameRuleWrapper {
    private val registryAccess = RegistryAccess.registryAccess().getRegistry(RegistryKey.GAME_RULE)

    fun all() = registryAccess.toList()
    fun getByName(namespacedKey: String) =
        registryAccess.firstOrNull { it.key.asString() == namespacedKey }
}
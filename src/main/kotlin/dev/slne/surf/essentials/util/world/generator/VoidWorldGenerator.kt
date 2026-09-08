package dev.slne.surf.essentials.util.world.generator

import dev.slne.surf.essentials.plugin
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.generator.ChunkGenerator
import java.io.File
import java.util.*

object VoidWorldGenerator : ChunkGenerator() {
    override fun canSpawn(world: World, x: Int, z: Int) = true
    override fun getFixedSpawnLocation(world: World, random: Random) =
        Location(world, 0.5, 100.0, 0.5)

    private val GENERATOR_NAME = "${plugin.name}:${VoidWorldGenerator::class.java.simpleName}"

    fun addGeneratorToBukkitYml(worldName: String) {
        val worldContainer = plugin.server.worldContainer
        val bukkitYmlFile = worldContainer.parentFile?.let { File(it, "bukkit.yml") }
            ?: File(worldContainer, "bukkit.yml")

        if (!bukkitYmlFile.exists()) {
            bukkitYmlFile.createNewFile()
        }

        val config = YamlConfiguration.loadConfiguration(bukkitYmlFile)

        config.set("worlds.$worldName.generator", GENERATOR_NAME)
        config.save(bukkitYmlFile)
    }

    fun removeGeneratorFromBukkitYml(worldName: String) {
        val worldContainer = plugin.server.worldContainer
        val bukkitYmlFile = worldContainer.parentFile?.let { File(it, "bukkit.yml") }
            ?: File(worldContainer, "bukkit.yml")

        if (!bukkitYmlFile.exists()) {
            return
        }

        val config = YamlConfiguration.loadConfiguration(bukkitYmlFile)

        if (config.contains("worlds.$worldName")) {
            config.set("worlds.$worldName", null)

            config.save(bukkitYmlFile)
        }
    }
}


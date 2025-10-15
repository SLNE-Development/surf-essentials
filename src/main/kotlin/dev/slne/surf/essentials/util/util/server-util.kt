package dev.slne.surf.essentials.util.util

import org.bukkit.Server

fun Server.isFolia() =
    runCatching { Class.forName("io.papermc.paper.threadedregions.RegionizedServer") }.isSuccess
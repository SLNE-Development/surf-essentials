package dev.slne.surf.essentials.util.world

import io.canvasmc.canvas.WorldUnloadResult
import org.bukkit.Bukkit
import org.bukkit.World
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Suppress("SuspendCoroutineLacksCancellationGuarantees")
suspend fun World.unloadCanvasWorld(save: Boolean = true): WorldUnloadResult = suspendCoroutine { cont ->
    Bukkit.unloadWorldAsync(this, save) {
        cont.resume(it)
    }
}
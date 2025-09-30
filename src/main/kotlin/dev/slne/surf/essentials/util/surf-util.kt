package dev.slne.surf.essentials.util

import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.Style

fun SurfComponentBuilder.translatable(key: String, vararg args: ComponentLike) =
    append(Component.translatable(key, Style.empty(), *args))
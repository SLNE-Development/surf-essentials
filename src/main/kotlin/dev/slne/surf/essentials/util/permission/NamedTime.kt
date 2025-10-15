package dev.slne.surf.essentials.util.permission

enum class NamedTime(
    val timeName: String,
    val ticks: Long
) {
    DAY("Tag", 500),
    NOON("Mittag", 6_000),
    NIGHT("Nacht", 13_000),
    MIDNIGHT("Mitternacht", 18_000)
}

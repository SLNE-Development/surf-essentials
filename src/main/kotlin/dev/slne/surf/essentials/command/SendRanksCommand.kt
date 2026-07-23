package dev.slne.surf.essentials.command

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.player.UserProfile
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoRemove
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerInfoUpdate.Action
import dev.jorel.commandapi.kotlindsl.commandTree
import dev.jorel.commandapi.kotlindsl.literalArgument
import dev.slne.surf.api.core.luckperms.LuckPermsAccess
import dev.slne.surf.api.core.util.random
import dev.slne.surf.api.paper.command.executors.playerExecutorSuspend
import dev.slne.surf.essentials.util.permission.EssentialsPermissionRegistry
import kotlinx.coroutines.future.await
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.luckperms.api.model.group.Group
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow
import kotlin.math.roundToInt

private val miniMessage = MiniMessage.miniMessage()

private const val DEFAULT_GROUP = "default"
private const val MULTI_TOTAL = 80
private const val MULTI_DEFAULT_RATIO = 0.70

private val RANDOM_NAMES = listOf(
    "Lena", "Max", "Timo", "Jonas", "Mia", "Finn", "Emil", "Nele",
    "Paul", "Lars", "Ida", "Ben", "Noah", "Lea", "Jan", "Kira",
    "Tom", "Anna", "Luca", "Sophie", "Nico", "Marie", "Elias", "Tim",
    "Hannah", "Leon", "Clara", "Felix", "Emma", "Moritz", "Laura", "David",
    "Lina", "Julian", "Sarah", "Philipp", "Amelie", "Simon", "Lisa", "Jakob",
    "Mila", "Fabian", "Johanna", "Niklas", "Charlotte", "Erik", "Frieda", "Tobias",
    "Greta", "Linus", "Pia", "Sebastian", "Antonia", "Florian", "Emily", "Jannik",
    "Melina", "Marvin", "Zoe", "Dominik", "Helena", "Kevin", "Isabel", "Christian",
    "Mara", "Robin", "Nora", "Alexander", "Selina", "Justin", "Katharina", "Daniel",
    "Alina", "Vincent", "Theresa", "Adrian", "Carla", "Michael", "Emilia", "Lukas",
    "Jasmin", "Konstantin", "Vanessa", "Oskar", "Miriam", "Colin", "Elena", "Henri",
    "Josephine", "Til", "Romy", "Matteo", "Fiona", "Bastian", "Leni", "Silas",
    "Verena", "Aaron", "Malin", "Jonathan",
)

private data class RankInfo(
    val name: String,
    val prefix: String,
    val weight: Int,
)

private val injected = ConcurrentHashMap<UUID, MutableList<UUID>>()

private fun Component.lastColor(): TextColor? {
    var color = color()
    for (child in children()) child.lastColor()?.let { color = it }
    return color
}

private fun Group.toRankInfo() = RankInfo(
    name = name,
    prefix = cachedData.metaData.prefix ?: "",
    weight = weight.orElse(0),
)

private fun RankInfo.displayName(playerName: String) =
    miniMessage.deserialize("$prefix $playerName")

private fun randomName(): String =
    RANDOM_NAMES[random.nextInt(RANDOM_NAMES.size)]

private suspend fun loadRanks(): List<RankInfo> {
    val lp = LuckPermsAccess.luckperms
    lp.groupManager.loadAllGroups().await()
    return lp.groupManager.loadedGroups
        .map { it.toRankInfo() }
        .sortedByDescending { it.weight }
}

private fun distribute(size: Int, total: Int): IntArray {
    if (size <= 0) return IntArray(0)
    if (total <= 0) return IntArray(size)
    val base = 2.0
    val shares = DoubleArray(size) { base.pow(it) }
    val sum = shares.sum()
    val counts = IntArray(size) { (total * shares[it] / sum).roundToInt().coerceAtLeast(1) }
    val drift = total - counts.sum()
    counts[size - 1] = (counts[size - 1] + drift).coerceAtLeast(1)
    return counts
}

private fun buildMultiEntries(ranks: List<RankInfo>): List<Pair<Component, Int>> {
    val default = ranks.firstOrNull { it.name.equals(DEFAULT_GROUP, ignoreCase = true) }
    val staff = ranks.filter { it !== default }
    val entries = ArrayList<Pair<Component, Int>>(MULTI_TOTAL)

    if (default == null) {
        val counts = distribute(staff.size, MULTI_TOTAL)
        staff.forEachIndexed { i, r ->
            repeat(counts[i]) { entries += r.displayName(randomName()) to r.weight }
        }
        return entries
    }

    val defaultCount =
        if (staff.isEmpty()) MULTI_TOTAL
        else (MULTI_TOTAL * MULTI_DEFAULT_RATIO).roundToInt()
    val counts = distribute(staff.size, MULTI_TOTAL - defaultCount)

    staff.forEachIndexed { i, r ->
        repeat(counts[i]) { entries += r.displayName(randomName()) to r.weight }
    }
    repeat(defaultCount) { entries += default.displayName(randomName()) to default.weight }
    return entries
}

private fun clearTablist(player: Player) {
    val api = PacketEvents.getAPI()
    val remove = ArrayList<UUID>()
    injected.remove(player.uniqueId)?.let { remove += it }
    Bukkit.getOnlinePlayers()
        .filter { it.uniqueId != player.uniqueId }
        .mapTo(remove) { it.uniqueId }
    if (remove.isEmpty()) return
    api.playerManager.sendPacket(player, WrapperPlayServerPlayerInfoRemove(remove))
}

private fun sendTablist(player: Player, entries: List<Pair<Component, Int>>) {
    clearTablist(player)
    val api = PacketEvents.getAPI()

    val fakeIds = ArrayList<UUID>(entries.size)
    val infos = entries.mapIndexed { index, (displayName, listOrder) ->
        val id = UUID.randomUUID()
        fakeIds += id
        WrapperPlayServerPlayerInfoUpdate.PlayerInfo(
            UserProfile(id, "npc_$index"),
            true,               // listed
            0,                  // latency
            GameMode.SURVIVAL,
            displayName,
            null,               // chatSession
            listOrder,          // listOrder (ab 1.21.2)
            true,               // showHat (ab 1.21.4)
        )
    }

    val packet = WrapperPlayServerPlayerInfoUpdate(
        EnumSet.of(
            Action.ADD_PLAYER,
            Action.UPDATE_LISTED,
            Action.UPDATE_LATENCY,
            Action.UPDATE_DISPLAY_NAME,
            Action.UPDATE_LIST_ORDER,
        ),
        infos,
    )

    api.playerManager.sendPacket(player, packet)
    injected[player.uniqueId] = fakeIds
}

fun sendRanksCommand() = commandTree("sendranks") {
    withPermission(EssentialsPermissionRegistry.SEND_RANKS_COMMAND)

    literalArgument("single") {
        playerExecutorSuspend { player, _ ->
            val entries = loadRanks().map { it.displayName(player.name) to it.weight }
            sendTablist(player, entries)
        }
    }

    literalArgument("multi") {
        playerExecutorSuspend { player, _ ->
            sendTablist(player, buildMultiEntries(loadRanks()))
        }
    }
}
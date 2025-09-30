package dev.slne.surf.essentials.command

import dev.jorel.commandapi.kotlindsl.*
import dev.slne.surf.essentials.command.argument.advancementCriterionArgument
import dev.slne.surf.essentials.util.EssentialsPermissionRegistry
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import org.bukkit.advancement.Advancement
import org.bukkit.entity.Player
import java.util.*

fun advancementCommand() = commandTree("advancement") {
    withPermission(EssentialsPermissionRegistry.ADVANCEMENT_COMMAND)
    literalArgument("grant") {
        entitySelectorArgumentManyPlayers("players") {
            literalArgument("only") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args
                        val successfulPlayers = mutableObjectSetOf<Player>()

                        for (player in players) {
                            val progress = player.getAdvancementProgress(advancement)

                            if (progress.isDone) {
                                continue
                            }

                            progress.remainingCriteria.forEach {
                                progress.awardCriteria(it)
                            }

                            successfulPlayers.add(player)

                            if (successfulPlayers.size < players.size) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Der Erfolg konnte nicht an alle Spieler vergeben werden.")
                                    hoverEvent(buildText {
                                        info("Erfolgreich vergeben an")
                                        appendSpace()
                                        variableValue(successfulPlayers.size)
                                        info(":")
                                        appendSpace()
                                        variableValue(successfulPlayers.joinToString(", ") { it.name })
                                    })
                                }
                                return@anyExecutor
                            }

                            if (successfulPlayers.isEmpty()) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Es konnte kein Erfolg vergeben werden.")
                                }
                                return@anyExecutor
                            }

                            executor.sendText {
                                success("Es wurden erfolgreich")
                                appendSpace()
                                variableValue(successfulPlayers.size)
                                appendSpace()
                                success("Erfolge vergeben.")
                            }
                        }
                    }

                    advancementCriterionArgument("criterion") {
                        anyExecutor { executor, args ->
                            val players: Collection<Player> by args
                            val advancement: Advancement by args
                            val criterion: String by args

                            val successfulPlayers = mutableObjectSetOf<Player>()

                            for (player in players) {
                                val criterionSuccess = player.getAdvancementProgress(advancement)
                                    .awardCriteria(criterion)

                                if (criterionSuccess) {
                                    successfulPlayers.add(player)
                                }
                            }

                            if (successfulPlayers.size < players.size) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Der Fortschritt konnte nicht an alle Spieler vergeben werden.")
                                    hoverEvent(buildText {
                                        info("Erfolgreich vergeben an")
                                        appendSpace()
                                        variableValue(successfulPlayers.size)
                                        info(":")
                                        appendSpace()
                                        variableValue(successfulPlayers.joinToString(", ") { it.name })
                                    })
                                }
                                return@anyExecutor
                            }

                            if (successfulPlayers.isEmpty()) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Es konnte kein Fortschritt vergeben werden.")
                                }
                                return@anyExecutor
                            }

                            executor.sendText {
                                success("Es wurden erfolgreich")
                                appendSpace()
                                variableValue(successfulPlayers.size)
                                appendSpace()
                                success("Fortschritte vergeben.")
                            }
                        }
                    }
                }
            }
            literalArgument("from") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = false,
                            children = true
                        )

                        for (player in players) {
                            var hadSomethingToGrant = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.isDone) {
                                    continue
                                }

                                progress.remainingCriteria.forEach {
                                    progress.awardCriteria(it)
                                    hadSomethingToGrant = true
                                }
                            }

                            if (hadSomethingToGrant) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht an alle Spieler vergeben werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich vergeben an")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg vergeben werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge vergeben.")
                        }
                    }
                }
            }
            literalArgument("until") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = true,
                            children = false
                        )

                        for (player in players) {
                            var hadSomethingToGrant = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.isDone) {
                                    continue
                                }

                                progress.remainingCriteria.forEach {
                                    progress.awardCriteria(it)
                                    hadSomethingToGrant = true
                                }
                            }

                            if (hadSomethingToGrant) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht an alle Spieler vergeben werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich vergeben an")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg vergeben werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge vergeben.")
                        }
                    }
                }
            }
            literalArgument("trough") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = true,
                            children = true
                        )

                        for (player in players) {
                            var hadSomethingToGrant = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.isDone) {
                                    continue
                                }

                                progress.remainingCriteria.forEach {
                                    progress.awardCriteria(it)
                                    hadSomethingToGrant = true
                                }
                            }

                            if (hadSomethingToGrant) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht an alle Spieler vergeben werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich vergeben an")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg vergeben werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge vergeben.")
                        }
                    }
                }
            }
            literalArgument("everything") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = true,
                            children = true
                        )

                        for (player in players) {
                            var hadSomethingToGrant = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.isDone) {
                                    continue
                                }

                                progress.remainingCriteria.forEach {
                                    progress.awardCriteria(it)
                                    hadSomethingToGrant = true
                                }
                            }

                            if (hadSomethingToGrant) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht an alle Spieler vergeben werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich vergeben an")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg vergeben werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge vergeben.")
                        }
                    }
                }
            }
        }
    }

    literalArgument("revoke") {
        entitySelectorArgumentManyPlayers("players") {
            literalArgument("only") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args
                        val successfulPlayers = mutableObjectSetOf<Player>()

                        for (player in players) {
                            val progress = player.getAdvancementProgress(advancement)

                            if (progress.awardedCriteria.isEmpty()) {
                                continue
                            }

                            progress.awardedCriteria.forEach {
                                progress.revokeCriteria(it)
                            }

                            successfulPlayers.add(player)

                            if (successfulPlayers.size < players.size) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Der Erfolg konnte nicht von allen angegeben Spielern entfernt werden.")
                                    hoverEvent(buildText {
                                        info("Erfolgreich entfernt an")
                                        appendSpace()
                                        variableValue(successfulPlayers.size)
                                        info(":")
                                        appendSpace()
                                        variableValue(successfulPlayers.joinToString(", ") { it.name })
                                    })
                                }
                                return@anyExecutor
                            }

                            if (successfulPlayers.isEmpty()) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Es konnte kein Erfolg entfernt werden.")
                                }
                                return@anyExecutor
                            }

                            executor.sendText {
                                success("Es wurden erfolgreich")
                                appendSpace()
                                variableValue(successfulPlayers.size)
                                appendSpace()
                                success("Erfolge entfernt.")
                            }
                        }
                    }

                    advancementCriterionArgument("criterion") {
                        anyExecutor { executor, args ->
                            val players: Collection<Player> by args
                            val advancement: Advancement by args
                            val criterion: String by args

                            val successfulPlayers = mutableObjectSetOf<Player>()

                            for (player in players) {
                                val criterionSuccess = player.getAdvancementProgress(advancement)
                                    .revokeCriteria(criterion)

                                if (criterionSuccess) {
                                    successfulPlayers.add(player)
                                }
                            }

                            if (successfulPlayers.size < players.size) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Der Fortschritt konnte nicht von allen angegeben Spielern entfernt werden.")
                                    hoverEvent(buildText {
                                        info("Erfolgreich entfernt an")
                                        appendSpace()
                                        variableValue(successfulPlayers.size)
                                        info(":")
                                        appendSpace()
                                        variableValue(successfulPlayers.joinToString(", ") { it.name })
                                    })
                                }
                                return@anyExecutor
                            }

                            if (successfulPlayers.isEmpty()) {
                                executor.sendText {
                                    appendPrefix()
                                    error("Es konnte kein Fortschritt entfernt werden.")
                                }
                                return@anyExecutor
                            }

                            executor.sendText {
                                success("Es wurden erfolgreich")
                                appendSpace()
                                variableValue(successfulPlayers.size)
                                appendSpace()
                                success("Fortschritte entfernt.")
                            }
                        }
                    }
                }
            }
            literalArgument("from") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = false,
                            children = true
                        )

                        for (player in players) {
                            var hadSomethingToRevoke = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.awardedCriteria.isEmpty()) {
                                    continue
                                }

                                progress.awardedCriteria.forEach {
                                    progress.revokeCriteria(it)
                                    hadSomethingToRevoke = true
                                }
                            }

                            if (hadSomethingToRevoke) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht von allen angegeben Spielern entfernt werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich entfernt von")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg entfernt werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge entfernt.")
                        }
                    }
                }
            }
            literalArgument("until") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = true,
                            children = false
                        )

                        for (player in players) {
                            var hadSomethingToRevoke = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.awardedCriteria.isEmpty()) {
                                    continue
                                }

                                progress.awardedCriteria.forEach {
                                    progress.revokeCriteria(it)
                                    hadSomethingToRevoke = true
                                }
                            }

                            if (hadSomethingToRevoke) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht von allen angegebenen Spielern entfernt werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich entfernt von")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg entfernt werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge entfernt.")
                        }
                    }
                }
            }
            literalArgument("trough") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = true,
                            children = true
                        )

                        for (player in players) {
                            var hadSomethingToRevoke = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.awardedCriteria.isEmpty()) {
                                    continue
                                }

                                progress.awardedCriteria.forEach {
                                    progress.revokeCriteria(it)
                                    hadSomethingToRevoke = true
                                }
                            }

                            if (hadSomethingToRevoke) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht von allen angegebenen Spielern entfernt werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich entfernt von")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg entfernt werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge entfernt.")
                        }
                    }
                }
            }
            literalArgument("everything") {
                advancementArgument("advancement") {
                    anyExecutor { executor, args ->
                        val players: Collection<Player> by args
                        val advancement: Advancement by args

                        val successfulPlayers = mutableObjectSetOf<Player>()
                        val advancements = getAdvancements(
                            advancement,
                            parents = true,
                            children = true
                        )

                        for (player in players) {
                            var hadSomethingToRevoke = false
                            for (adv in advancements) {
                                val progress = player.getAdvancementProgress(adv)

                                if (progress.awardedCriteria.isEmpty()) {
                                    continue
                                }

                                progress.awardedCriteria.forEach {
                                    progress.revokeCriteria(it)
                                    hadSomethingToRevoke = true
                                }
                            }

                            if (hadSomethingToRevoke) {
                                successfulPlayers.add(player)
                            }
                        }

                        if (successfulPlayers.size < players.size) {
                            executor.sendText {
                                appendPrefix()
                                error("Der Erfolg konnte nicht von allen angegebenen Spielern entfernt werden.")
                                hoverEvent(buildText {
                                    info("Erfolgreich entfernt von")
                                    appendSpace()
                                    variableValue(successfulPlayers.size)
                                    info(":")
                                    appendSpace()
                                    variableValue(successfulPlayers.joinToString(", ") { it.name })
                                })
                            }
                            return@anyExecutor
                        }

                        if (successfulPlayers.isEmpty()) {
                            executor.sendText {
                                appendPrefix()
                                error("Es konnte kein Erfolg entfernt werden.")
                            }
                            return@anyExecutor
                        }

                        executor.sendText {
                            success("Es wurden erfolgreich")
                            appendSpace()
                            variableValue(successfulPlayers.size)
                            appendSpace()
                            success("Erfolge entfernt.")
                        }
                    }
                }
            }
        }
    }
    literalArgument("query") {
        playerArgument("player") {
            advancementArgument("advancement") {
                anyExecutor { executor, args ->
                    val player: Player by args
                    val advancement: Advancement by args
                    val progress = player.getAdvancementProgress(advancement)

                    if (progress.isDone) {
                        executor.sendText {
                            appendPrefix()
                            info("Der Spieler ")
                            variableValue(player.name)
                            info(" hat den Erfolg ")
                            variableValue(advancement.key.toString())
                            info(" vollständig abgeschlossen.")
                        }
                        return@anyExecutor
                    }

                    if (progress.awardedCriteria.isEmpty()) {
                        executor.sendText {
                            appendPrefix()
                            info("Der Spieler ")
                            variableValue(player.name)
                            info(" hat den Erfolg ")
                            variableValue(advancement.key.toString())
                            info(" noch nicht begonnen.")
                        }
                        return@anyExecutor
                    }

                    executor.sendText {
                        appendPrefix()
                        info("Der Spieler ")
                        variableValue(player.name)
                        info(" hat den Erfolg ")
                        variableValue(advancement.key.toString())
                        info(" teilweise abgeschlossen.")
                        hoverEvent(buildText {
                            info("Fortschritt:")
                            appendSpace()
                            variableValue("${progress.awardedCriteria.size}/${progress.awardedCriteria.size + progress.remainingCriteria.size} Kriterien abgeschlossen")
                            appendNewline()
                            info("Abgeschlossene Kriterien:")
                            appendSpace()
                            variableValue(progress.awardedCriteria.joinToString(", "))
                            appendNewline()
                            info("Offene Kriterien:")
                            appendSpace()
                            variableValue(progress.remainingCriteria.joinToString(", "))
                        })
                    }
                }
            }
        }
    }
}

private fun getAdvancements(advancement: Advancement, parents: Boolean, children: Boolean) =
    buildList {
        if (parents) {
            var parent = advancement.parent

            while (parent != null) {
                add(parent)
                parent = parent.parent
            }
        }

        add(advancement)

        if (children) {
            val queue = ArrayDeque<Advancement>()
            queue.addAll(advancement.children)

            while (queue.isNotEmpty()) {
                val current = queue.removeFirst()
                add(current)
                queue.addAll(current.children)
            }
        }
    }

package dev.slne.surf.essentials.command.minecraft

import com.mojang.brigadier.tree.CommandNode
import dev.jorel.commandapi.Brigadier
import dev.jorel.commandapi.arguments.LiteralArgument
import org.bukkit.Bukkit

private lateinit var executeNode: CommandNode<Any>
private lateinit var ifNode: CommandNode<Any>
private lateinit var unlessNode: CommandNode<Any>

fun executeCommandAdditions() {
    executeNode = Brigadier.getRootNode().getChild("execute")
    ifNode = executeNode.getChild("if")
    unlessNode = executeNode.getChild("unless")

    executesAsServer()
}

private fun executesAsServer() {
    executeNode.addChild(
        Brigadier.fromLiteralArgument(
            LiteralArgument("asServer")
        ).fork(executeNode) { _ ->
            mutableListOf(
                Brigadier.getBrigadierSourceFromCommandSender(
                    Bukkit.getConsoleSender()
                )
            )
        }.build()
    )
}


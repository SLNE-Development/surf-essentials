package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.tree.CommandNode;
import dev.jorel.commandapi.Brigadier;
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.LiteralArgument;
import lombok.val;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Collections;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ExecuteCommandAddon {
    private final CommandNode executeNode;
    private final CommandNode ifNode;
    private final CommandNode unlessNode;

    public ExecuteCommandAddon() {
        this.executeNode = Brigadier.getRootNode().getChild("execute");
        this.ifNode = executeNode.getChild("if");
        this.unlessNode = executeNode.getChild("unless");

        executesAsServer();
        addConditionals(ifNode, true);
        addConditionals(unlessNode, false);
    }

    /**
     * Adds the "asServer" subcommand to the "execute" command
     * <br>
     * This subcommand executes the command as the server console
     */
    private void executesAsServer() {
        executeNode.addChild(Brigadier.fromLiteralArgument(new LiteralArgument("asServer"))
                .fork(executeNode, context -> Collections.singletonList(Brigadier.getBrigadierSourceFromCommandSender(Bukkit.getConsoleSender()))).build());
    }

    /**
     * Adds the conditional subcommands to the "execute" command
     *
     * @param argumentBuilder The argument builder to add the conditionals to
     * @param positive        Whether the conditional is positive or negative
     */
    private void addConditionals(CommandNode argumentBuilder, boolean positive) {
        // Declare arguments like normal
        val numeratorArgument = new IntegerArgument("numerator", 0);
        val denominatorArgument = new IntegerArgument("denominator", 1);
        val arguments = new ArrayList<Argument<?>>();

        arguments.add(numeratorArgument);
        arguments.add(denominatorArgument);

        // Get brigadier argument objects
        val numerator = Brigadier.fromArgument(numeratorArgument);
        val denominator = Brigadier.fromArgument(denominatorArgument)
                // Fork redirecting to "execute" and state our predicate
                .fork(executeNode, Brigadier.fromPredicate((sender, args) -> {
                    // Parse arguments like normal
                    int num = (int) args[0];
                    int denom = (int) args[1];

                    // Return boolean with a num/denom chance
                    return positive == (Math.ceil(Math.random() * denom) <= num);
                }, arguments));

        val randomchance = Brigadier.fromLiteralArgument(new LiteralArgument("randomchance")).build();
        randomchance.addChild(numerator.then(denominator).build());
        argumentBuilder.addChild(randomchance);
    }
}

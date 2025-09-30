package dev.slne.surf.essentialsold.commands.minecraft;

/**
import dev.jorel.commandapi.arguments.Argument;
import dev.jorel.commandapi.arguments.BooleanArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.executors.ResultingCommandExecutor;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import org.bukkit.GameRule;
import org.bukkit.command.CommandSender;

public class GameruleCommand2 extends EssentialsCommand {
    private static final String VALUE_ARG = "value";

    public GameruleCommand2() {
        super("gamerule", "gamerule <rule> [value]", "Change gamerules");

        withPermission(Permissions.GAMERULE_PERMISSION);

        for (GameRule<?> gameRule : GameRule.values()) {
            Class<?> type = gameRule.getType();

            if (type.equals(Boolean.class)){

            } else if (type.equals(Integer.class)) {

            } else {
                throw new IllegalArgumentException("Unknown GameRule type: " + type);
            }
        }
    }

    private<Type> int setRule(CommandSender sender, GameRule<Type> rule, Type value) {
        EssentialsUtil.getSenderWorld(sender).setGameRule(rule, value);
        return 0;
    }

    private void constructRule(GameRule<?> rule, RuleType type) {
        then(literal(rule.getName())
                .then(type.getArgument()
                        .executes((ResultingCommandExecutor) (sender, args) -> setRule(sender, rule, ))))
    }

    private enum RuleType {
        BOOLEAN(new BooleanArgument(VALUE_ARG)),
        INTEGER(new IntegerArgument(VALUE_ARG));


        private final Argument<?> argument;

        RuleType(Argument<?> argument) {

            this.argument = argument;
        }

        public Argument<?> getArgument() {
            return argument;
        }
    }
}
 */

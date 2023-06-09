package dev.slne.surf.essentials.utils.nms.brigadier;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.help.EssentialsHelpTopic;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * A base class for Brigadier commands.
 *
 * @since 1.0.0
 */
public abstract class BrigadierCommand {
    /**
     * A map of the registered instances of BrigadierCommand.
     */
    public static final Map<Class<? extends BrigadierCommand>, BrigadierCommand> INSTANCES = new HashMap<>();

    protected final CommandBuildContext commandBuildContext;

    /**
     * Constructs a new BrigadierCommand instance and registers it with SurfEssentials and Bukkit.
     */
    public BrigadierCommand() {
        this.commandBuildContext = EssentialsUtil.buildContext();

        INSTANCES.put(this.getClass(), this);
        for (String name : names()) {
            SurfEssentials.registerPluginBrigadierCommand(name, this::literal);
        }
        Bukkit.getHelpMap().addTopic(new EssentialsHelpTopic(this));
    }


    /**
     * Returns an array of command names.
     *
     * @return an array of command names
     */
    public abstract String[] names();

    /**
     * Returns the usage string for the command.
     *
     * @return the usage string for the command
     */
    public abstract String usage();

    /**
     * Returns the description of the command.
     *
     * @return the description of the command
     */
    public abstract String description();

    /**
     * Configures the literal command with arguments.
     *
     * @param literal the literal argument builder to configure
     */
    public abstract void literal(LiteralArgumentBuilder<CommandSourceStack> literal);


    /**
     * Gets or creates an instance of BrigadierCommand of the specified type.
     *
     * @param clazz the class of the BrigadierCommand
     * @param <T>   the type of the BrigadierCommand
     * @return an instance of BrigadierCommand of the specified type
     */
    public static <T extends BrigadierCommand> @Nullable T getOrCreateCommand(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T instance = (T) INSTANCES.get(clazz);

        if (instance == null) {
            try {
                instance = clazz.getDeclaredConstructor().newInstance();
                INSTANCES.put(clazz, instance);
            } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                     InvocationTargetException e) {
                e.printStackTrace();
                return null;
            }
        }
        return instance;
    }
}

package dev.slne.surf.essentials.main.commands.general;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.bukkit.entity.Player;

import java.util.Collection;

public class EnchantCommand {

    public static void register() {
        SurfEssentials.registerPluginBrigadierCommand("enchant", EnchantCommand::literal);
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        // Get the command build context for the server
        CommandBuildContext buildContext = CommandBuildContext.configurable(MinecraftServer.getServer().registryAccess(),
                MinecraftServer.getServer().getWorldData().getDataConfiguration().enabledFeatures());

        // Require the permission to use this command
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.enchant"));
        // Add an argument for the entities to be enchanted
        literal.then(Commands.argument("targets", EntityArgument.entities())
                // Add an argument for the enchantment to be applied
                .then(Commands.argument("enchantment", ResourceArgument.resource(buildContext, Registries.ENCHANTMENT))
                        // Execute the command with a default level of 1 if no level is specified
                        .executes(context -> enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                ResourceArgument.getEnchantment(context, "enchantment"), 1))
                        // Add an argument for the level of the enchantment
                        .then(Commands.argument("level", IntegerArgumentType.integer(0))
                                // Execute the command with the specified level
                                .executes(context -> enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                        ResourceArgument.getEnchantment(context, "enchantment") , IntegerArgumentType.getInteger(context, "level"))))));
    }

    private static int enchant(CommandSourceStack source, Collection<? extends Entity> targets, Holder<Enchantment> enchantment, int level) throws CommandSyntaxException {
        // Get the enchantment from the argument
        Enchantment enchantment1 = enchantment.value();

        // If the level is greater than the maximum allowed level for the enchantment, throw an exception
        if (level > enchantment1.getMaxLevel()) {
            throw ERROR_LEVEL_TOO_HIGH.create(level, enchantment1.getMaxLevel());
        } else {
            // Counter for the number of entities that had their items enchanted
            int i = 0;

            // Iterate through the targets
            for (Entity entity : targets) {
                // Check if the entity is a living entity
                if (entity instanceof LivingEntity livingEntity) {
                    // Get the main hand item of the living entity
                    ItemStack itemStack = livingEntity.getMainHandItem();
                    // If the item is empty, throw an exception
                    if (itemStack.isEmpty()) throw ERROR_NO_ITEM.create(livingEntity.getName().getString());

                    // If the enchantment can be applied to the item and is compatible with the enchantments already on the item, apply the enchantment
                    if (enchantment1.canEnchant(itemStack) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(itemStack).keySet(), enchantment1)) {
                        itemStack.enchant(enchantment1, level);
                        // Increment the counter
                        ++i;
                    }
                    // If the item is not compatible with the enchantment and there is only one target, throw an exception
                    else if (targets.size() == 1) {
                        throw ERROR_INCOMPATIBLE.create(itemStack.getItem().getName(itemStack).getString());
                    }

                }
                // If the entity is not a living entity, throw an exception
                else{
                    throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
                }
            }

            // If no entities had their items enchanted, throw an exception
            if (i == 0) {
                throw ERROR_NOTHING_HAPPENED.create();
            }

            // If the command source is a player, send a message to the player
            if (source.isPlayer()){
                Player player = source.getPlayer().getBukkitEntity();

                // If there is only one target, send a message with the name of the target
                if (targets.size() == 1){
                    SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Die Verzauberung ", SurfColors.SUCCESS))
                            .append(Component.text(enchantment1.getFullname(level).getString(), SurfColors.TERTIARY))
                            .append(Component.text(" wurde zu ", SurfColors.SUCCESS))
                            .append(Component.text(targets.iterator().next().getDisplayName().getString(), SurfColors.TERTIARY))
                            .append(Component.text("'s item hinzugefügt", SurfColors.SUCCESS))));
                }
                // If there is more than one target, send a message with the number of targets
                else{
                    SurfApi.getUser(player).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                            .append(Component.text("Die Verzauberung ", SurfColors.SUCCESS))
                            .append(Component.text(enchantment1.getFullname(level).getString(), SurfColors.TERTIARY))
                            .append(Component.text(" wurde zu ", SurfColors.SUCCESS))
                            .append(Component.text(targets.size(), SurfColors.TERTIARY))
                            .append(Component.text(" entities hinzugefügt", SurfColors.SUCCESS))));
                }
            }
            // If the command source is not a player, send a message to the command source
            else {
                // If there is only one target, send a message with the name of the target
                if (targets.size() == 1) {
                    source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.enchant.success.single", enchantment1.getFullname(level), (targets.iterator().next()).getDisplayName()), true);
                }
                // If there is more than one target, send a message with the number of targets
                else {
                    source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.enchant.success.multiple", enchantment1.getFullname(level), targets.size()), true);
                }
            }
            // Return the number of entities that had their items enchanted
            return i;
        }
    }

    /**
     * Exception type for when the specified entity is not a living entity.
     *
     * @param entityName the name of the entity
     */
    private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((entityName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.entity", entityName));

    /**
     * Exception type for when the specified entity is not holding an item.
     *
     * @param entityName the name of the entity
     */
    private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((entityName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.itemless", entityName));

    /**
     * Exception type for when the specified item is not compatible with the enchantments being applied.
     *
     * @param itemName the name of the item
     */
    private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((itemName) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.incompatible", itemName));

    /**
     * Exception type for when the specified enchantment level is too high.
     *
     * @param level the specified enchantement level
     * @param maxLevel the maximum allowed enchantement level
     */
    private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((level, maxLevel) ->
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed.level", level, maxLevel));

    /**
     * Exception type for when the enchant command failed and nothing happened.
     */
    private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.enchant.failed"));

}


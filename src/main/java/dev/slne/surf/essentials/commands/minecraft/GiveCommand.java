package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;


public class GiveCommand {
    public static void register(){
        SurfEssentials.registerPluginBrigadierCommand("give", GiveCommand::literal).setUsage("/give <targets> <item> [<amount>]")
                .setDescription("Gives the targets the specified item");
    }

    private static void literal(LiteralArgumentBuilder<CommandSourceStack> literal){
        // Get the command build context for the server
        CommandBuildContext buildContext = CommandBuildContext.configurable(MinecraftServer.getServer().registryAccess(),
                MinecraftServer.getServer().getWorldData().getDataConfiguration().enabledFeatures());

        // Require permission for the command
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.GIVE_PERMISSION));
        // Add player argument to the command
        literal.then(Commands.argument("targets", EntityArgument.players())
                // Add item argument to the command
                .then(Commands.argument("item", ItemArgument.item(buildContext))
                        // Execute the command with default amount of 1 if no amount argument is provided
                        .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "targets"), ItemArgument.getItem(context, "item"), 1))
                        // Add amount argument to the command and execute it with the provided amount
                        .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                .executes(context -> give(context.getSource(), EntityArgument.getPlayers(context, "targets"), ItemArgument.getItem(context, "item"),
                                        IntegerArgumentType.getInteger(context, "amount"))))));
    }

    private static int give(CommandSourceStack source, Collection<ServerPlayer> targetsUnchecked, ItemInput item, int amount) throws CommandSyntaxException {
        Collection<ServerPlayer> targets = EssentialsUtil.checkPlayerSuggestion(source, targetsUnchecked);
        // Calculate the maximum stack size and maximum give size
        int maxStackSize = item.getItem().getMaxStackSize();
        int maxGiveSize = maxStackSize*100;

        // Check if the give size exceeds the maximum give size
        if (amount > maxGiveSize){
            // Send failure message to the source if the give size exceeds the maximum give size
            source.sendFailure(net.minecraft.network.chat.Component.translatable("commands.give.failed.toomanyitems", maxGiveSize, item.createItemStack(amount, false).getDisplayName()));
            return 0;
        }

        // Initialize countdownAmount with the value of amount
        int countdownAmount = amount;
        // Loop until countdownAmount is less than or equal to zero
        while (countdownAmount > 0) {
            // Loop through all targets
            for (ServerPlayer target : targets) {
                // Skip this iteration if countdownAmount is less than or equal to zero
                if (countdownAmount <= 0) continue;

                // Calculate the amount of items to give in this iteration
                int i1 = Math.min(maxStackSize, countdownAmount);
                countdownAmount -= i1;

                // Create the item stack to give
                ItemStack itemStack = item.createItemStack(i1, false);
                // Attempt to add the item stack to the target's inventory
                boolean flag = target.getInventory().add(itemStack);
                ItemEntity entityitem;

                // If the item stack was successfully added to the inventory, or if the item stack is empty
                if (flag && itemStack.isEmpty()) {
                    // Set the count of the item stack to 1
                    itemStack.setCount(1);
                    // Drop the item stack in the world
                    entityitem = target.drop(itemStack, false, false, false);

                    // If the item entity was created
                    if (entityitem != null) {
                        // Make the item entity fake
                        entityitem.makeFakeItem();
                    }

                    // Play the item pickup sound at the target's position
                    target.level.playSound(null, target.getX(), target.getY(), target.getZ(), SoundEvents.ITEM_PICKUP,
                            SoundSource.PLAYERS, 0.2F, ((target.getRandom().nextFloat() - target.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    // Broadcast changes to the target's container menu
                    target.containerMenu.broadcastChanges();

                // If the item stack was not successfully added to the inventory
                } else {
                    // Drop the item stack in the world
                    entityitem = target.drop(itemStack, false);

                    // If the item entity was created
                    if (entityitem != null) {
                        // Set the no pick up delay of the item entity
                        entityitem.setNoPickUpDelay();
                        // Set the owner of the item entity
                        entityitem.setOwner(target.getUUID());
                    }
                }

            }
        }

        // If the source is a player
        if (source.isPlayer()){
            // Get the Bukkit player from the source
            org.bukkit.entity.Player player = source.getPlayerOrException().getBukkitEntity();

            // If there is only one target
            if (targets.size() == 1) {
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    try {
                        user.sendMessage(SurfApi.getPrefix()
                                .append(targets.iterator().next().adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                                .append(Component.text(" hat ", SurfColors.SUCCESS))
                                .append(Component.text(amount, SurfColors.TERTIARY))
                                .append(Component.text(" "))
                                .append(item.createItemStack(amount, false).getBukkitStack().displayName())
                                .append(Component.text(" erhalten!", SurfColors.SUCCESS)));
                    } catch (CommandSyntaxException ignored) {}
                });

            // If there are multiple targets
            } else {
                SurfApi.getUser(player).thenAcceptAsync(user -> {
                    try {
                        user.sendMessage(SurfApi.getPrefix()
                                .append(Component.text(targets.size()))
                                .append(Component.text(" Spieler haben ", SurfColors.SUCCESS))
                                .append(Component.text(amount, SurfColors.TERTIARY))
                                .append(Component.text(" "))
                                .append(item.createItemStack(amount, false).getBukkitStack().displayName())
                                .append(Component.text(" erhalten!", SurfColors.SUCCESS)));
                    } catch (CommandSyntaxException ignored) {}
                });
            }

        // If the source is not a player
        }else {
            // If there is only one target
            if (targets.size() == 1) {
                // Send success message to the source with the target's name and the given item stack
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.give.success.single", amount,
                        item.createItemStack(amount, false).getDisplayName(), (targets.iterator().next()).getDisplayName()), false);

            // If there are multiple targets
            } else {
                // Send success message to the source with the number of targets and the given item stack
                source.sendSuccess(net.minecraft.network.chat.Component.translatable("commands.give.success.single", amount,
                        item.createItemStack(amount, false).getDisplayName(), targets.size()), false);
            }
        }
        return targets.size();
    }
}

package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class IllusionerTroll implements Listener {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> illusioner(@NotNull LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.illusioner"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeIllusioner(context, EntityArgument.getPlayer(context, "player"), 1))
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> makeIllusioner(context, EntityArgument.getPlayer(context, "player"),
                                IntegerArgumentType.getInteger(context, "amount"))));
    }

    private static int makeIllusioner(CommandContext<CommandSourceStack> context, @NotNull ServerPlayer target, int amount) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), target);
        BlockPos blockPosition = new BlockPos(target.blockPosition());

        // Check if position is valid spawn position
        if (!Level.isInSpawnableBounds(blockPosition)){
            throw INVALID_POSITION.create();
        }

        // Summon the illusioner
        for (int i = 0; i < amount; i++) {
            Bukkit.dispatchCommand(context.getSource().getBukkitSender(), "summon illusioner " + target.getX() + " " + target.getY()
                    + " " + target.getZ() + " {Tags:[\"target: " + target.getUUID() + "\"]}");
        }

        // Sends message to the target
        EssentialsUtil.sendSuccess(target, Component.text("Kannst du den echten finden?", Colors.AQUA));

        // Add blindness effect to the target as long as at least one of the summoned illusioner is in the radius.
        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            AtomicInteger validEntity = new AtomicInteger();

            target.getBukkitEntity().getWorld().getNearbyEntities(target.getBukkitEntity().getLocation(), 50, 50, 50).forEach(entity -> {
                if (entity.getScoreboardTags().contains("target: " + target.getUUID())){
                    target.getBukkitEntity().addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1, false, false, false));
                    validEntity.addAndGet(1);
                }
            });

            if (validEntity.get() == 0){
                bukkitTask.cancel();
            }

        }, 0, 20);

        // Success messages
        if (context.getSource().isPlayer()){
            EssentialsUtil.sendSuccess(context.getSource(), target.adventure$displayName.colorIfAbsent(Colors.TERTIARY)
                    .append(Component.text(" wird mit Illusioner getrollt!", Colors.SUCCESS)));
        }else{
            context.getSource().sendSuccess(target.getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" is trolled with Illusioner")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }

    //Invalid Position exception
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("commands.summon.invalidPosition"));

}

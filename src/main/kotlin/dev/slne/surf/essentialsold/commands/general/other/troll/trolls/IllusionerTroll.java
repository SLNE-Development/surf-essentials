package dev.slne.surf.essentialsold.commands.general.other.troll.trolls;

/**
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.pdc.UUIDDataType;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicInteger;

public class IllusionerTroll extends Troll {
    private static final SimpleCommandExceptionType INVALID_POSITION = new SimpleCommandExceptionType(net.minecraft.network.chat.Component.translatable("commands.summon.invalidPosition"));
    private static final NamespacedKey NAMESPACED_KEY = new NamespacedKey(SurfEssentials.getInstance(), "target");

    @Override
    public String name() {
        return "illusioner";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_ILLUSIONER_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> makeIllusioner(context, EntityArgument.getPlayer(context, "player"), 1))
                .then(Commands.argument("amount", IntegerArgumentType.integer(1, 20))
                        .executes(context -> makeIllusioner(context, EntityArgument.getPlayer(context, "player"),
                                IntegerArgumentType.getInteger(context, "amount"))));
    }

    private int makeIllusioner(CommandContext<CommandSourceStack> context, @NotNull ServerPlayer target, int amount) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), target);
        BlockPos blockPosition = new BlockPos(target.blockPosition());
        final var player = target.getBukkitEntity();

        // Check if position is valid spawn position
        if (!Level.isInSpawnableBounds(blockPosition)) {
            throw INVALID_POSITION.create();
        }

        // Summon the illusioner
        for (int i = 0; i < amount; i++) {
            player.getWorld().spawnEntity(player.getLocation(), EntityType.ILLUSIONER, CreatureSpawnEvent.SpawnReason.COMMAND, entity ->
                    entity.getPersistentDataContainer().set(
                            NAMESPACED_KEY,
                            UUIDDataType.INSTANCE,
                            player.getUniqueId()
                    ));
        }

        // Sends message to the target
        EssentialsUtil.sendSuccess(target, Component.text("Kannst du den echten finden?", Colors.AQUA));

        // Add blindness effect to the target as long as at least one of the summoned illusioner is in the radius.
        Bukkit.getScheduler().runTaskTimer(SurfEssentials.getInstance(), bukkitTask -> {
            AtomicInteger validEntity = new AtomicInteger();

            player.getWorld().getNearbyEntities(target.getBukkitEntity().getLocation(), 50, 50, 50).forEach(entity -> {
                final var pdc = entity.getPersistentDataContainer();
                if (pdc.has(NAMESPACED_KEY) && player.getUniqueId().equals(pdc.get(NAMESPACED_KEY, UUIDDataType.INSTANCE))) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 1, false, false, false));
                    validEntity.addAndGet(1);
                }
            });

            if (validEntity.get() == 0) {
                bukkitTask.cancel();
            }

        }, 0, 20);

        // Success messages
        EssentialsUtil.sendSuccess(context.getSource(), EssentialsUtil.getDisplayName(target)
                .append(Component.text(" wird mit Illusioner getrollt!", Colors.SUCCESS)));

        return 1;
    }
}

 */

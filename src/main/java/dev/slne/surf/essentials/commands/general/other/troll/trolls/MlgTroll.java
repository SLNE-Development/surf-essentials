package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.abtract.CraftUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class MlgTroll extends Troll {
    private static final HashMap<UUID, ItemStack[]> saveInventory = new HashMap<>();

    @Override
    public String name() {
        return "mlg";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_MLG_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .then(Commands.literal("water")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "water")))
                .then(Commands.literal("slime")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "slime")))
                .then(Commands.literal("snow")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "snow")))
                .then(Commands.literal("pearl")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "pearl")))
                .then(Commands.literal("cobweb")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "cobweb")))
                .then(Commands.literal("boat")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "boat")))
                .then(Commands.literal("vines")
                        .executes(context -> mlgTroll(context, EntityArgument.getPlayer(context, "player").getBukkitEntity(), "vines")));
    }

    private int mlgTroll(CommandContext<CommandSourceStack> context, Player target, String mlgType) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), CraftUtil.toServerPlayer(target));
        CommandSourceStack source = context.getSource();

        if (getAndToggleTroll(target)) {
            EssentialsUtil.sendSourceError(source, EssentialsUtil.getDisplayName(target)
                    .append(Component.text(" versucht schon ein MLG", Colors.ERROR)));
            return 0;
        }

        saveInventory.put(target.getUniqueId(), target.getInventory().getContents());

        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20, 100, false, false, false));
        EssentialsUtil.sendSuccess(target, Component.text("Schaffst du den MLG?", Colors.GREEN));
        target.setInvulnerable(true);
        target.getInventory().clear();

        switch (mlgType) {
            case "water" -> target.getInventory().setItem(1, new ItemStack(Material.WATER_BUCKET, 1));
            case "slime" -> target.getInventory().setItem(1, new ItemStack(Material.SLIME_BLOCK, 1));
            case "snow" -> target.getInventory().setItem(1, new ItemStack(Material.POWDER_SNOW_BUCKET, 1));
            case "pearl" -> target.getInventory().setItem(1, new ItemStack(Material.ENDER_PEARL, 1));
            case "cobweb" -> target.getInventory().setItem(1, new ItemStack(Material.COBWEB, 1));
            case "boat" -> target.getInventory().setItem(1, new ItemStack(Material.OAK_BOAT, 1));
            case "vines" -> target.getInventory().setItem(1, new ItemStack(Material.TWISTING_VINES, 1));
            default -> throw new IllegalStateException("Unexpected value: " + mlgType);
        }


        Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> {
            target.getInventory().setContents(saveInventory.get(target.getUniqueId()));
            target.setInvulnerable(false);
            target.removePotionEffect(PotionEffectType.LEVITATION);

            saveInventory.remove(target.getUniqueId());
            stopTroll(target);
        }, 20 * 10);


        EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(target)
                .append(Component.text(" versucht nun ein MLG!", Colors.SUCCESS)));

        return 1;
    }

    public static void restoreInventoryFromMlgTroll() {
        saveInventory.forEach((uuid, itemStacks) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                player.getInventory().setContents(itemStacks);
                player.setInvulnerable(false);
            }
            saveInventory.remove(uuid);
        });

    }

    public static void restoreInventoryFromMlgTroll(@NotNull Player player) {
        if (!saveInventory.containsKey(player.getUniqueId())) return;
        player.getInventory().setContents(saveInventory.get(player.getUniqueId()));
        player.setInvulnerable(false);
        saveInventory.remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        restoreInventoryFromMlgTroll(event.getPlayer());
    }
}

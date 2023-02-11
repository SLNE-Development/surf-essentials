package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.api.SurfApi;
import dev.slne.surf.api.utils.message.SurfColors;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import net.kyori.adventure.text.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class MlgTroll {
    private static final HashMap<UUID, ItemStack[]> saveInventory = new HashMap<>();
    private static final HashMap<UUID, Boolean> scheduledTasks = new HashMap<>();

    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> mlg(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.mlg"));
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

    private static int mlgTroll(CommandContext<CommandSourceStack> context, Player target, String mlgType) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), ((CraftPlayer) target).getHandle());
        CommandSourceStack source = context.getSource();

        if (!saveInventory.containsKey(target.getUniqueId())) {
            saveInventory.put(target.getUniqueId(), target.getInventory().getContents());
        }
        target.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 20,100, false, false, false));
        SurfApi.getUser(target).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                .append(Component.text("Schaffst du den MLG?", SurfColors.GREEN))));
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

        if (!scheduledTasks.containsKey(target.getUniqueId())) {
            scheduledTasks.put(target.getUniqueId(), true);
            Bukkit.getScheduler().runTaskLater(SurfEssentials.getInstance(), () -> {
                target.getInventory().setContents(saveInventory.get(target.getUniqueId()));
                target.setInvulnerable(false);
                saveInventory.remove(target.getUniqueId());
                scheduledTasks.remove(target.getUniqueId());
                target.removePotionEffect(PotionEffectType.LEVITATION);
            }, 20 * 10);
        }


        //success message
        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(target.displayName().colorIfAbsent(SurfColors.YELLOW))
                    .append(Component.text(" versucht nun ein MLG!", SurfColors.SUCCESS))));
        }else{
            source.sendSuccess(EntityArgument.getPlayer(context, "player").getDisplayName()
                    .copy().append(net.minecraft.network.chat.Component.literal(" now tries a MLG!")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }


    public static void restoreInventoryFromMlgTroll(){
        saveInventory.forEach((uuid, itemStacks) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null){
                player.getInventory().setContents(itemStacks);
                player.setInvulnerable(false);
            }
            saveInventory.remove(uuid);
        });

    }

    public static void restoreInventoryFromMlgTroll(Player player){
        if (!saveInventory.containsKey(player.getUniqueId())) return;
        player.getInventory().setContents(saveInventory.get(player.getUniqueId()));
        player.setInvulnerable(false);
        saveInventory.remove(player.getUniqueId());

    }

}

package dev.slne.surf.essentials.commands.general;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHeadCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"playerhead", "phead", "headp", "headplayer"};
    }

    @Override
    public String usage() {
        return "/phead <player>";
    }

    @Override
    public String description() {
        return "Gives you the head of a player";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.PLAYER_HEAD_PERMISSION));

        literal.then(Commands.argument("player", EntityArgument.player())
                .executes(context -> getPlayerHead(context.getSource(), EntityArgument.getPlayer(context, "player"))));
    }

    @SuppressWarnings("UnstableApiUsage")
    private int getPlayerHead(CommandSourceStack source, ServerPlayer playerUnchecked) throws CommandSyntaxException {
        var playerTarget = EssentialsUtil.checkPlayerSuggestion(source, playerUnchecked).getBukkitEntity();
        var sender = source.getPlayerOrException().getBukkitEntity();
        var skullStack = new ItemStack(Material.PLAYER_HEAD, 1);

        skullStack.editMeta(itemMeta -> {
           if (itemMeta instanceof SkullMeta skullMeta){
               skullMeta.setOwningPlayer(playerTarget);
               skullMeta.setPlayerProfile(playerTarget.getPlayerProfile());
               skullMeta.setNoteBlockSound(Sound.ENTITY_PLAYER_HURT.getKey());
               EssentialsUtil.changeName(skullMeta, EssentialsUtil.getDisplayName(playerTarget)
                       .append(Component.text("´s Kopf", Colors.INFO))
                       .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE));
           }
        });

        if (!sender.getInventory().addItem(skullStack).isEmpty()){
            EssentialsUtil.sendError(source, Component.text("There is no available inventory space", Colors.RED));
            return 0;
        }

        EssentialsUtil.sendSuccess(source, Component.text("Du hast ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(playerTarget))
                .append(skullStack.displayName())
                .append(Component.text(" erhalten", Colors.SUCCESS)));
        return 1;
    }
}

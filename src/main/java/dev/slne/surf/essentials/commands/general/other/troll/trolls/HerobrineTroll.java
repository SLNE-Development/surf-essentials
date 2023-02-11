package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.arguments.BoolArgumentType;
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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;

import java.util.UUID;

public class HerobrineTroll {
    public static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> herobrine(LiteralArgumentBuilder<CommandSourceStack> literal){
        literal.requires(stack -> stack.getBukkitSender().hasPermission("surf.essentials.commands.troll.herobrine"));
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> summonHerobrine(context, EntityArgument.getPlayer(context, "player"), true))
                .then(Commands.argument("showParticles", BoolArgumentType.bool())
                        .executes(context -> summonHerobrine(context, EntityArgument.getPlayer(context, "player"),
                                BoolArgumentType.getBool(context, "showParticles"))));
    }

    private static int summonHerobrine(CommandContext<CommandSourceStack> context, Player target, boolean withParticles) throws CommandSyntaxException {
        EssentialsUtil.checkSinglePlayerSuggestion(context.getSource(), (ServerPlayer) target);
        CommandSourceStack source = context.getSource();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "Herobrine");
        ServerPlayer serverPlayer = (ServerPlayer) target;

        ServerPlayer herobrineNpc = new ServerPlayer(source.getServer(), target.level.getMinecraftWorld(), gameProfile);
        herobrineNpc.setPos(serverPlayer.position());

        //Herobrine skin from user "HER0BRINE"
        String signature = "eC7w2wiXxki/00GVlxGHSCc28W31xw52fghrCGwGznBiF0rbAwPrWJNu7oGFOnJEFjnM9yPQa7pRE6OwwAYpVHycOBe" +
                "fwPkGpwQ3gzLzzFTC+WbiwCIvc7xEKNlgx+C4gIAIHAWfPQYmLLCd6ZDkZNoAjOZashU9lIDJ1gdnbV6eeZB84oazzRpMOQvpLRPC6u" +
                "AfgzTr6PkiV/RoBlPexqHnOYwUaXliX2qqn2XBlx5XS/jAaDo4e0bxOkts3bZ0Bh2QcPjJlZzCptAlsiUTR64ERspupbGsC90noCqrD" +
                "Vkj3UiyRlKjty5VX4J6BWk9XDSeIA+7wmfroFGxkRkLthXHZjCZiV49apRoNqbg8QTGzH2MOIzXrX9l7QIGRzuyne8qh3gezHa0UiPD" +
                "kZQq/Mfwmm301mDAPZMSdbB1H6CLGrKLVvM/ZtWj4pakzeIG/x91gNVkbTArHOE3gG17vyD+hV0Q/n8Z+rlCADRb/cHl72VyZXYaGWa" +
                "hj5eirkPWJbBNRqnoTEsYpO5rnYokqGsZJek5/pp/EhOvGQ2bX/4q4Kzcni4el/yR6+zsl+MaRBV88A8DuDPCiTq4bzXORDr8fFkNzc" +
                "3V6oB7AeNSS8ntSEOZcL1z++H6aVlMARB9gtA0V2yR4Zo/5iUDfTsFP2+hnAIk+0A9vS334OxlAs8=";
        String texture = "ewogICJ0aW1lc3RhbXAiIDogMTY3MTgyMzY4NTM5NiwKICAicHJvZmlsZUlkIiA6ICIxMTVjZDZkMjY3ZmQ0MGE1OTQ0O" +
                "WUyYjRmZDc0NmM2MyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIRVIwQlJJTkUiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAog" +
                "ICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3" +
                "RleHR1cmUvOGRhODFiMDQ2OTNiNDEwYTQ1MzE1YmQ3YmI4OGE0MTBmZjc1NmYzNmQ3MjRiZjU2MTViNzdmZjRkOWQyM2M5YiIKICAgI" +
                "H0KICB9Cn0=";

        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        ServerGamePacketListenerImpl ps = serverPlayer.connection;

        ps.send(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, herobrineNpc));
        ps.send(new ClientboundAddPlayerPacket(herobrineNpc));
        EssentialsUtil.scarePlayer(((ServerPlayer) target).getBukkitEntity());

        if (withParticles){
            Bukkit.getScheduler().runTaskTimerAsynchronously(SurfEssentials.getInstance(), bukkitTask -> herobrineNpc.getLevel().sendParticles(
                    serverPlayer, ParticleTypes.ASH, false, herobrineNpc.getEyePosition().x, herobrineNpc.getEyePosition().y,
                    herobrineNpc.getEyePosition().z, 10, 0.5, 0.5, 0.5, 1), 2, 5);
        }

        //success message
        if (source.isPlayer()){
            SurfApi.getUser(source.getPlayerOrException().getUUID()).thenAcceptAsync(user -> user.sendMessage(SurfApi.getPrefix()
                    .append(Component.text("Bei ", SurfColors.SUCCESS))
                    .append(((ServerPlayer) target).adventure$displayName.colorIfAbsent(SurfColors.TERTIARY))
                    .append(Component.text(" erscheint nun Herobrine!", SurfColors.SUCCESS))));
        }else{
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Herobrine now appears at ")
                            .withStyle(ChatFormatting.GREEN)
                    .append(EntityArgument.getPlayer(context, "player").getDisplayName())
                    .copy().append(net.minecraft.network.chat.Component.literal(" !")
                            .withStyle(ChatFormatting.GREEN)), false);
        }
        return 1;
    }
}

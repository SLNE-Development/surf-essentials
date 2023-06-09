package dev.slne.surf.essentials.commands.general.other.troll.trolls;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.SurfEssentials;
import dev.slne.surf.essentials.commands.general.other.troll.Troll;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundAddPlayerPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.util.UUID;

public class HerobrineTroll extends Troll {

    private int summonHerobrine(CommandContext<CommandSourceStack> context, ServerPlayer target, boolean withParticles) throws CommandSyntaxException {
        EssentialsUtil.checkPlayerSuggestion(context.getSource(), target);
        CommandSourceStack source = context.getSource();

        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "Herobrine");

        ServerPlayer herobrineNpc = new ServerPlayer(source.getServer(), target.level.getMinecraftWorld(), gameProfile);
        herobrineNpc.setPos(target.position());

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

        EssentialsUtil.sendPackets(
                target,
                new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, herobrineNpc),
                new ClientboundAddPlayerPacket(herobrineNpc)
        );
        EssentialsUtil.scarePlayer(target.getBukkitEntity());

        if (withParticles) {
            try (final var level = herobrineNpc.serverLevel()) {
                Bukkit.getScheduler().runTaskTimerAsynchronously(SurfEssentials.getInstance(), bukkitTask -> level.sendParticles(
                        target, ParticleTypes.ASH, false, herobrineNpc.getEyePosition().x, herobrineNpc.getEyePosition().y,
                        herobrineNpc.getEyePosition().z, 10, 0.5, 0.5, 0.5, 1), 2, 5);
            } catch (IOException ignored) {
            }
        }

        //success message

        EssentialsUtil.sendSuccess(source, Component.text("Bei ", Colors.SUCCESS)
                .append(EssentialsUtil.getDisplayName(target))
                .append(Component.text(" erscheint nun Herobrine!", Colors.SUCCESS)));

        return 1;
    }

    @Override
    public String name() {
        return "herobrine";
    }

    @Override
    public String permission() {
        return Permissions.TROLL_HEROBRINE_PERMISSION;
    }

    @Override
    protected ArgumentBuilder<CommandSourceStack, ?> troll() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> summonHerobrine(context, EntityArgument.getPlayer(context, "player"), true))
                .then(Commands.argument("showParticles", BoolArgumentType.bool())
                        .executes(context -> summonHerobrine(context, EntityArgument.getPlayer(context, "player"),
                                BoolArgumentType.getBool(context, "showParticles"))));
    }
}

package dev.slne.surf.essentials.commands.minecraft;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.exceptions.WrapperCommandSyntaxException;
import dev.jorel.commandapi.executors.NativeResultingCommandExecutor;
import dev.slne.surf.essentials.commands.EssentialsCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.brigadier.Exceptions;
import dev.slne.surf.essentials.utils.permission.Permissions;
import lombok.val;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerKickEvent;

import java.util.concurrent.CompletableFuture;

public class WhitelistCommand extends EssentialsCommand {
    public WhitelistCommand() {
        super("whitelist", "whitelist <add | remove | list | reload | on | off | status> [<player>]", "Manage server whitelist");

        withPermission(Permissions.WHITELIST_PERMISSION);

        executesNative((NativeResultingCommandExecutor) (sender, args) -> query(sender.getCallee()));

        then(literal("on")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> whitelistState(sender.getCallee(), true)));

        then(literal("off")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> whitelistState(sender.getCallee(), false)));

        then(literal("reload")
                .executesNative((NativeResultingCommandExecutor) (sender, args) -> reload(sender.getCallee())));

        then(literal("user")
                .then(literal("list")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> list(sender.getCallee())))
                .then(literal("removeAll")
                        .executesNative((NativeResultingCommandExecutor) (sender, args) -> removeAll(sender.getCallee())))
                .then(literal("add")
                        .then(offlinePlayerArgument("player")
                                .replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() -> {
                                    val offlinePlayers = info.sender().getServer().getWhitelistedPlayers();
                                    return Bukkit.getOnlinePlayers().stream()
                                            .filter(player -> !offlinePlayers.contains(player))
                                            .map(Player::getName)
                                            .toList();
                                })))
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> add(sender.getCallee(), args.getUnchecked("player")))))
                .then(literal("remove")
                        .then(offlinePlayerArgument("player")
                                .replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.supplyAsync(() ->
                                        info.sender().getServer().getWhitelistedPlayers().stream()
                                                .map(OfflinePlayer::getName)
                                                .toList())))
                                .executesNative((NativeResultingCommandExecutor) (sender, args) -> remove(sender.getCallee(), args.getUnchecked("player"))))));
    }

    private int query(CommandSender source) {
        val isEnabled = source.getServer().hasWhitelist();

        EssentialsUtil.sendSuccess(source, Component.text("Die Whitelist ist gerade ", Colors.INFO)
                .append(Component.text((isEnabled) ? "aktiviert" : "deaktiviert", Colors.VARIABLE_VALUE)));

        return 1;
    }

    private int whitelistState(CommandSender source, boolean enable) throws WrapperCommandSyntaxException {
        val server = source.getServer();

        if (server.hasWhitelist() == enable)
            throw enable ? Exceptions.ERROR_WHITELIST_ALREADY_ENABLED : Exceptions.ERROR_WHITELIST_ALREADY_DISABLED;
        server.setWhitelist(enable);
        server.setWhitelistEnforced(enable);

        EssentialsUtil.sendSuccess(source, Component.text("Die Whitelist wurde ", Colors.SUCCESS)
                .append(Component.text(enable ? "aktiviert" : "deaktiviert", Colors.TERTIARY)));
        return 1;
    }

    private int reload(CommandSender source) {
        val server = source.getServer();
        server.reloadWhitelist();
        kickUnlistedPlayers(server);

        EssentialsUtil.sendSuccess(source, "Die Whitelist wurde neu geladen");
        return 1;
    }

    public static void kickUnlistedPlayers(Server server) {
        if (server.isWhitelistEnforced() && server.hasWhitelist()) {
            val whitelistedPlayers = server.getWhitelistedPlayers();
            server.getOnlinePlayers().stream()
                    .filter(player -> !whitelistedPlayers.contains(player) && !player.isOp())
                    .forEach(player -> player.kick(LegacyComponentSerializer.legacySection().deserialize(EssentialsUtil.whitelistMessage()), PlayerKickEvent.Cause.WHITELIST));
        }
    }

    private int list(CommandSender source) {
        EssentialsUtil.sendSuccess(source, Component.text("Spieler auf der Whitelist: ", Colors.INFO)
                .append(Component.join(JoinConfiguration.commas(true), source.getServer().getWhitelistedPlayers().stream()
                        .map(offlinePlayer -> EssentialsUtil.getOfflineDisplayName(offlinePlayer)
                                .hoverEvent(HoverEvent.showText(Component.text(offlinePlayer.getUniqueId().toString(), Colors.VARIABLE_VALUE))))
                        .toList())));
        return 1;
    }

    private int add(CommandSender source, OfflinePlayer profile) throws WrapperCommandSyntaxException {
        if (profile.isWhitelisted()) throw Exceptions.ERROR_ALREADY_WHITELISTED;
        profile.setWhitelisted(true);

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getOfflineDisplayName(profile)
                .append(Component.text(" wurde zur Whitelist hinzugefügt", Colors.SUCCESS)));
        return 1;
    }

    private int remove(CommandSender source, OfflinePlayer profile) throws WrapperCommandSyntaxException {
        if (!profile.isWhitelisted()) throw Exceptions.ERROR_NOT_WHITELISTED;
        profile.setWhitelisted(false);
        kickUnlistedPlayers(source.getServer());

        EssentialsUtil.sendSuccess(source, EssentialsUtil.getOfflineDisplayName(profile)
                .append(Component.text(" wurde von der Whitelist entfernt", Colors.SUCCESS)));

        return 1;
    }

    private int removeAll(CommandSender source) throws WrapperCommandSyntaxException {
        val server = source.getServer();
        int successes = 0;

        for (OfflinePlayer player : server.getWhitelistedPlayers()) {
            player.setWhitelisted(false);
            successes++;
        }

        kickUnlistedPlayers(server);

        if (successes == 0) throw Exceptions.ERROR_NOT_WHITELISTED;

        EssentialsUtil.sendSuccess(source, Component.text("Es wurden ", Colors.SUCCESS)
                .append(Component.text(successes, Colors.TERTIARY))
                .append(Component.text(" Spieler von der Whitelist entfernt", Colors.SUCCESS)));

        return successes;
    }
}

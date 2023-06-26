package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.server.players.StoredUserEntry;
import net.minecraft.server.players.UserWhiteListEntry;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Objects;

public class WhitelistCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"whitelist"};
    }

    @Override
    public String usage() {
        return "/whitelist [<user | on | off | reload>]";
    }

    @Override
    public String description() {
        return "Change the whitelist";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.WHITELIST_PERMISSION));
        literal.executes(context -> query(context.getSource()));

        literal.then(Commands.literal("on")
                .executes(context -> whitelistOn(context.getSource())));
        literal.then(Commands.literal("off")
                .executes(context -> whitelistOff(context.getSource())));

        literal.then(Commands.literal("reload")
                .executes(context -> reload(context.getSource())));

        literal.then(Commands.literal("user")
                .then(Commands.literal("list")
                        .executes(context -> list(context.getSource())))
                .then(Commands.literal("add")
                        .then(Commands.argument("players", GameProfileArgument.gameProfile())
                                .suggests((context, builder) -> {
                                    var playerList = context.getSource().getServer().getPlayerList();
                                    var players = playerList.getPlayers().stream()
                                            .map(Player::getGameProfile)
                                            .filter(gameProfile -> !playerList.isWhiteListed(gameProfile))
                                            .map(GameProfile::getName)
                                            .toArray(String[]::new);
                                    return SharedSuggestionProvider.suggest(players, builder);
                                })
                                .executes(context -> add(context.getSource(), GameProfileArgument.getGameProfiles(context, "players")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("players", GameProfileArgument.gameProfile())
                                .suggests((context, builder) ->
                                        SharedSuggestionProvider.suggest(context.getSource().getServer().getPlayerList().getWhiteListNames(), builder))
                                .executes(context -> remove(context.getSource(), GameProfileArgument.getGameProfiles(context, "players")))))
                .then(Commands.literal("removeAll")
                        .executes(context -> removeAll(context.getSource()))));
    }

    private int query(CommandSourceStack source) {
        var isEnabled = source.getServer().getPlayerList().isUsingWhitelist();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Die Whitelist ist gerade ", Colors.INFO)
                    .append(Component.text((isEnabled) ? "aktiviert" : "deaktiviert", Colors.TERTIARY)));
        }else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("The whitelist is currently ", Colors.INFO)
                    .append(Component.text((isEnabled) ? "enabled" : "disabled", Colors.TERTIARY)));
        }
        return 1;
    }

    private int whitelistOn(CommandSourceStack source) throws CommandSyntaxException {
        var playerList = source.getServer().getPlayerList();

        if (playerList.isUsingWhitelist()) throw ERROR_ALREADY_ENABLED.create();
        playerList.setUsingWhiteList(true);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Die Whitelist wurde ", Colors.SUCCESS)
                    .append(Component.text("aktiviert", Colors.TERTIARY)));
        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.translatable("commands.whitelist.enabled")
                    .colorIfAbsent(Colors.SUCCESS));
        }
        return 1;
    }

    private int whitelistOff(CommandSourceStack source) throws CommandSyntaxException {
        var playerList = source.getServer().getPlayerList();

        if (!playerList.isUsingWhitelist()) throw ERROR_ALREADY_DISABLED.create();
        playerList.setUsingWhiteList(false);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Die Whitelist wurde ", Colors.SUCCESS)
                    .append(Component.text("deaktiviert", Colors.TERTIARY)));
        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.translatable("commands.whitelist.disabled")
                    .colorIfAbsent(Colors.SUCCESS));
        }
        return 1;
    }

    private int reload(CommandSourceStack source) {
        source.getServer().getPlayerList().reloadWhiteList();
        source.getServer().kickUnlistedPlayers(source);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Die Whitelist wurde neu geladen");
        }else {
            EssentialsUtil.sendSourceSuccess(source, Component.translatable("commands.whitelist.reloaded"));
        }
        return 1;
    }

    private int list(CommandSourceStack source) {
        var nameList = source.getServer().getPlayerList().getWhiteList()
                .getEntries()
                .stream()
                .map(StoredUserEntry::getUser)
                .filter(Objects::nonNull)
                .map(EssentialsUtil::getDisplayName)
                .toArray(Component[]::new);

        var listComponent = Component.join(JoinConfiguration.commas(true), nameList);

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Spieler auf der Whitelist: ", Colors.INFO)
                    .append(listComponent));
        }else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Whitelisted players: ", Colors.INFO)
                    .append(listComponent));
        }
        return 1;
    }

    private int add(CommandSourceStack source, Collection<GameProfile> profiles) throws CommandSyntaxException{
        var userWhiteList = source.getServer().getPlayerList().getWhiteList();
        int successes = 0;

        for (GameProfile profile : profiles) {
            if (userWhiteList.isWhiteListed(profile)) continue;
            userWhiteList.add(new UserWhiteListEntry(profile));
            successes++;

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(profile)
                        .append(Component.text(" wurde zur Whitelist hinzugefügt", Colors.SUCCESS)));
            }else {
                EssentialsUtil.sendSourceSuccess(source, Component.translatable("commands.whitelist.add.success",
                        EssentialsUtil.getDisplayName(profile)));
            }
        }

        if (successes == 0) throw ERROR_ALREADY_WHITELISTED.create();
        return successes;
    }

    private int remove(CommandSourceStack source, Collection<GameProfile> profiles) throws CommandSyntaxException{
        var userWhiteList = source.getServer().getPlayerList().getWhiteList();
        int successes = 0;

        for (GameProfile profile : profiles) {
            if (!userWhiteList.isWhiteListed(profile)) continue;
            userWhiteList.remove(profile);
            successes++;

            if (source.isPlayer()){
                EssentialsUtil.sendSuccess(source, EssentialsUtil.getDisplayName(profile)
                        .append(Component.text(" wurde von der Whitelist entfernt", Colors.SUCCESS)));
            }else {
                EssentialsUtil.sendSourceSuccess(source, Component.translatable("commands.whitelist.remove.success",
                        EssentialsUtil.getDisplayName(profile)));
            }
        }
        source.getServer().kickUnlistedPlayers(source);

        if (successes == 0) throw ERROR_NOT_WHITELISTED.create();
        return successes;
    }

    private int removeAll(CommandSourceStack source) throws CommandSyntaxException{
        var whitelist = source.getServer().getPlayerList().getWhiteList();
        int successes = 0;

        for (UserWhiteListEntry entry : whitelist.getEntries()) {
            whitelist.remove(entry);
            successes++;
        }
        source.getServer().kickUnlistedPlayers(source);

        if (successes == 0) throw ERROR_NOT_WHITELISTED.create();

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, Component.text("Es wurden ", Colors.SUCCESS)
                    .append(Component.text(successes, Colors.TERTIARY))
                    .append(Component.text(" Spieler von der Whitelist entfernt", Colors.SUCCESS)));
        }else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Removed ", Colors.SUCCESS)
                    .append(Component.text(successes, Colors.TERTIARY))
                    .append(Component.text(" players from the whitelist", Colors.SUCCESS)));
        }
        return successes;
    }




    private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.whitelist.alreadyOn")
    );
    private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.whitelist.alreadyOff")
    );
    private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.whitelist.add.failed")
    );
    private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(
            net.minecraft.network.chat.Component.translatable("commands.whitelist.remove.failed")
    );
}

package dev.slne.surf.essentials.commands.minecraft;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.color.Colors;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.server.commands.ReloadCommand;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.flag.FeatureFlags;

import java.util.ArrayList;
import java.util.List;

public class DataPackCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"datapack"};
    }

    @Override
    public String usage() {
        return "/datapack <list | enable | disable>";
    }

    @Override
    public String description() {
        return "Manage datapacks";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.DATAPACK_PERMISSION));

        literal.then(Commands.literal("enable")
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests(UNSELECTED_PACKS)
                        .executes(context -> enablePack(
                                context.getSource(),
                                getPack(context, "name", true),
                                (profiles, profile) -> profile.getDefaultPosition().insert(profiles, profile, pack -> pack, false)
                        ))

                        .then(Commands.literal("after")
                                .then(Commands.argument("existing", StringArgumentType.string())
                                        .suggests(SELECTED_PACKS)
                                        .executes(context -> enablePack(
                                                context.getSource(),
                                                getPack(context, "name", true),
                                                (profiles, profile) -> profiles.add(profiles.indexOf(getPack(context, "existing", false)) + 1, profile)
                                        ))))

                        .then(Commands.literal("before")
                                .then(Commands.argument("existing", StringArgumentType.string())
                                        .suggests(SELECTED_PACKS)
                                        .executes(context -> enablePack(
                                                context.getSource(),
                                                getPack(context, "name", true),
                                                (profiles, profile) -> profiles.add(profiles.indexOf(getPack(context, "existing", false)), profile)
                                        ))))

                        .then(Commands.literal("last")
                                .executes(context -> enablePack(
                                        context.getSource(),
                                        getPack(context, "name", true),
                                        List::add
                                )))

                        .then(Commands.literal("first")
                                .executes(context -> enablePack(
                                        context.getSource(),
                                        getPack(context, "name", true),
                                        (profiles, profile) -> profiles.add(0, profile)
                                )))));


        literal.then(Commands.literal("disable")
                .then(Commands.argument("name", StringArgumentType.string())
                        .suggests(SELECTED_PACKS)
                        .executes(context -> disablePack(context.getSource(), getPack(context, "name", false)))));


        literal.then(Commands.literal("list")
                .executes(context -> list(context.getSource()))

                .then(Commands.literal("available")
                        .executes(context -> listAvailable(context.getSource())))

                .then(Commands.literal("enabled")
                        .executes(context -> listEnabled(context.getSource()))));
    }

    private int list(CommandSourceStack source) {
        return listEnabled(source) + listAvailable(source);
    }

    private int listEnabled(CommandSourceStack source){
        final var packRepository = source.getServer().getPackRepository();
        packRepository.reload();

        final var packs = packRepository.getSelectedPacks();

        if (packs.isEmpty()) {
            EssentialsUtil.sendSourceError(source, "Es sind keine Datapacks aktiviert");

        } else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Es sind ", Colors.INFO)
                    .append(Component.text(packs.size(), Colors.VARIABLE_VALUE))
                    .append(Component.text(" Datapacks aktiviert: ", Colors.INFO))
                    .append(Component.join(JoinConfiguration.commas(true), packs.stream()
                            .map(pack -> PaperAdventure.asAdventure(pack.getChatLink(true)))
                            .toList())));
        }
        return packs.size();
    }

    private int listAvailable(CommandSourceStack source){
        final var packRepository = source.getServer().getPackRepository();
        packRepository.reload();

        final var selectedPacks = packRepository.getSelectedPacks();
        final var availablePacks = packRepository.getAvailablePacks();
        final var featureFlags = source.enabledFeatures();
        final var disabledPacks = availablePacks.stream()
                .filter(pack -> !selectedPacks.contains(pack) && pack.getRequestedFeatures().isSubsetOf(featureFlags))
                .toList();

        if (disabledPacks.isEmpty()){
            EssentialsUtil.sendSourceError(source, "Es sind keine weiteren Datapacks verfügbar");

        }else {
            EssentialsUtil.sendSourceSuccess(source, Component.text("Es sind ", Colors.INFO)
                    .append(Component.text(disabledPacks.size(), Colors.VARIABLE_VALUE))
                    .append(Component.text(" Datapacks verfügbar: ", Colors.INFO))
                    .append(Component.join(JoinConfiguration.commas(true), disabledPacks.stream()
                            .map(pack -> PaperAdventure.asAdventure(pack.getChatLink(false)))
                            .toList())));
        }
        return disabledPacks.size();
    }

    private int disablePack(CommandSourceStack source, Pack pack) {
        final var packRepository = source.getServer().getPackRepository();
        final var packs = new ArrayList<>(packRepository.getSelectedPacks());

        packs.remove(pack);

        EssentialsUtil.sendSourceInfo(source, Component.text("Deaktiviere Datapack ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(pack.getChatLink(true))));

        ReloadCommand.reloadPacks(
                packs.stream()
                        .map(Pack::getId)
                        .toList(),
                source
        );
        return 1;

    }

    private int enablePack(CommandSourceStack source, Pack pack, Inserter packAdder) throws CommandSyntaxException {
        final var packRepository = source.getServer().getPackRepository();
        final var enabledPacks = new ArrayList<>(packRepository.getSelectedPacks());

        packAdder.apply(enabledPacks, pack);

        EssentialsUtil.sendSourceSuccess(source, Component.text("Aktiviere Datapack ", Colors.SUCCESS)
                .append(PaperAdventure.asAdventure(pack.getChatLink(true))));

        ReloadCommand.reloadPacks(
                enabledPacks
                        .stream()
                        .map(Pack::getId)
                        .toList(),
                source
        );
        return enabledPacks.size();
    }




    private static Pack getPack(CommandContext<CommandSourceStack> context, String name, boolean enable) throws CommandSyntaxException {
        final var packName = StringArgumentType.getString(context, name);
        final var packRepository = context.getSource().getServer().getPackRepository();
        final var pack = packRepository.getPack(packName);

        if (pack == null) throw ERROR_UNKNOWN_PACK.create(packName);

        final boolean isSelectedPack = packRepository.getSelectedPacks().contains(pack);
        final var availableFeatures = context.getSource().enabledFeatures();
        final var requiredFeatures = pack.getRequestedFeatures();

        if (enable && isSelectedPack) throw ERROR_PACK_ALREADY_ENABLED.create(pack);
        if (!enable && !isSelectedPack) throw ERROR_PACK_ALREADY_DISABLED.create(pack);

        if (!requiredFeatures.isSubsetOf(availableFeatures)) throw ERROR_PACK_FEATURES_NOT_ENABLED.create(pack, FeatureFlags.printMissingFlags(availableFeatures, requiredFeatures));

        return pack;
    }

    private static final SuggestionProvider<CommandSourceStack> UNSELECTED_PACKS = (context, builder) -> {
        final var packRepository = context.getSource().getServer().getPackRepository();
        final var collection = packRepository.getSelectedIds();
        final var featureFlagSet = context.getSource().enabledFeatures();

        return SharedSuggestionProvider.suggest(packRepository.getAvailablePacks()
                .stream()
                .filter((profile) -> profile.getRequestedFeatures().isSubsetOf(featureFlagSet))
                .map(Pack::getId)
                .filter((name) -> !collection.contains(name))
                .map(StringArgumentType::escapeIfRequired), builder);
    };

    private static final SuggestionProvider<CommandSourceStack> SELECTED_PACKS = (context, builder) ->
            SharedSuggestionProvider.suggest(context.getSource().getServer().getPackRepository().getSelectedIds()
                    .stream()
                    .map(StringArgumentType::escapeIfRequired), builder);



    private static final DynamicCommandExceptionType ERROR_UNKNOWN_PACK = new DynamicCommandExceptionType((name) ->
            PaperAdventure.asVanilla(Component.translatable("commands.datapack.unknown", Component.text((String) name))));
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_ENABLED = new DynamicCommandExceptionType((pack) ->
            PaperAdventure.asVanilla(Component.translatable("commands.datapack.enable.failed", PaperAdventure.asAdventure(((Pack) pack).getChatLink(true)))));
    private static final DynamicCommandExceptionType ERROR_PACK_ALREADY_DISABLED = new DynamicCommandExceptionType((pack) ->
            PaperAdventure.asVanilla(Component.translatable("commands.datapack.disable.failed", PaperAdventure.asAdventure(((Pack) pack).getChatLink(false)))));
    private static final Dynamic2CommandExceptionType ERROR_PACK_FEATURES_NOT_ENABLED = new Dynamic2CommandExceptionType((pack, flags) ->
            PaperAdventure.asVanilla(Component.translatable("commands.datapack.enable.failed.no_flags", PaperAdventure.asAdventure(((Pack) pack).getChatLink(false)), Component.text(((String) flags)))));

    @FunctionalInterface
    private interface Inserter {
        void apply(List<Pack> profiles, Pack profile) throws CommandSyntaxException;
    }
}

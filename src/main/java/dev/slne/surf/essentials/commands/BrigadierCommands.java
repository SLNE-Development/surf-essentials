package dev.slne.surf.essentials.commands;

import com.mojang.brigadier.tree.CommandNode;
import dev.jorel.commandapi.CommandAPI;
import dev.slne.surf.essentials.commands.cheat.*;
import dev.slne.surf.essentials.commands.cheat.gui.GuiCommands;
import dev.slne.surf.essentials.commands.general.*;
import dev.slne.surf.essentials.commands.general.other.ActionbarBroadcastCommand;
import dev.slne.surf.essentials.commands.general.other.TimerCommand;
import dev.slne.surf.essentials.commands.general.other.TitlebroadcastCommand;
import dev.slne.surf.essentials.commands.general.other.help.HelpCommand;
import dev.slne.surf.essentials.commands.general.other.poll.PollCommand;
import dev.slne.surf.essentials.commands.general.other.poll.VoteCommand;
import dev.slne.surf.essentials.commands.general.other.world.WorldCommand;
import dev.slne.surf.essentials.commands.minecraft.*;
import dev.slne.surf.essentials.commands.tp.*;

/**
 * This class is responsible for registering and unregistering all the commands provided by the plugin using Brigadier.
 *
 * @author twisti
 * @since 1.0.0
 */
public class BrigadierCommands {

    public BrigadierCommands() {
        new FillStackCommand();
        new FlyCommand();
        new FoodCommand();
        new GodmodeCommand();
        new HatCommand();
        new HealCommand();
        new HurtCommand();
        new InfinityCommand();
        new LatestDeathCommand();
        new LightningCommand();
        new RepairCommand();
        new SpeedCommand();
        new SuicideCommand();
        new TrashCommand();
        new UnhatCommand();
        new GuiCommands();
        new HelpCommand();
        new PollCommand();
        new VoteCommand();
        new WorldCommand();
        new ActionbarBroadcastCommand();
        new TimerCommand();
        new TitlebroadcastCommand();
        new AlertCommand();
        new BookCommand();
        new ChatClearCommand();
        new ClearItemCommand();
        new GetPosCommand();
        new InfoCommand();
        new PlayerHeadCommand();
        new RuleCommand();
        new SetItemLoreCommand();
        new SetItemNameCommand();
        new SpawnerChangeCommand();
        new AdvancementCommand();
        new ClearInventoryCommand();
        new DefaultGamemodeCommand();
        new DeopCommand();
        new DifficultyCommand();
        new EffectCommand();
        new EnchantCommand();
        new ExecuteCommandAddon();
        new FillCommand();
        new ForceloadCommand();
        new GamemodeCommand();
        new GiveCommand();
        new KillCommand();
        new ListCommand();
        new OpCommand();
        new ParticleCommand();
        new ReloadCommand();
        new SeedCommand();
        new SetBlockCommand();
        new SetPlayerIdleTimeoutCommand();
        new SetWorldSpawnCommand();
        new SpectateCommand();
        new SummonCommand();
        new TimerCommand();
        new WeatherCommand();
        new WhitelistCommand();
        new RandomTeleportCommand();
        new SpawnCommand();
        new TeleportOffline();
        new TimeCommand();
        new BroadcastWorldCommand();
        new NearCommand();
        new ScreamCommand();
        new ExperienceCommand();
        new FunctionCommand();
        new TeleportBackCommand();
        new TeleportToTopCommand();
        new TeleportCommand();
        new ChangeSlotCommand();
    }

    /**
     * Registers all the commands provided by the plugin using Brigadier.
     */
    public void register() {
        for (EssentialsCommand command : EssentialsCommand.COMMANDS) {
            command.register();
        }
    }

    /**
     * Unregisters all commands registered by this plugin from the server's {@link com.mojang.brigadier.CommandDispatcher<net.minecraft.commands.CommandSourceStack>}.
     * <p>
     * This should be called in the plugins {@link org.bukkit.plugin.Plugin#onDisable()} method to ensure that no remaining {@link CommandNode<net.minecraft.commands.CommandSourceStack>}s
     * exist on the server after the plugin has been unloaded.
     */
    public synchronized void unregister() {
        EssentialsCommand.COMMANDS.forEach(essentialsCommand -> CommandAPI.unregister(essentialsCommand.getName()));
    }
}

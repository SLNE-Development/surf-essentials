package dev.slne.surf.essentials.main.utils;

import dev.slne.surf.essentials.main.commands.cheat.*;
import dev.slne.surf.essentials.main.commands.cheat.gui.*;
import dev.slne.surf.essentials.main.commands.general.*;
import dev.slne.surf.essentials.main.commands.general.other.poll.PollCommand;
import dev.slne.surf.essentials.main.commands.general.other.poll.VoteCommand;
import dev.slne.surf.essentials.main.commands.general.other.troll.TrollManager;
import dev.slne.surf.essentials.main.commands.general.other.world.WorldCommand;
import dev.slne.surf.essentials.main.commands.general.sign.EditSignListener;
import dev.slne.surf.essentials.main.commands.general.sign.SignToggleCommand;
import dev.slne.surf.essentials.main.commands.minecraft.*;

public class Permissions {
    public static void setPerms(){
        //cheat commands
        FillStackCommand.PERMISSION = "surf.essentials.commands.more";
        FlyCommand.PERMISSION  = "surf.essentials.commands.fly";
        FoodCommand.PERMISSION = "surf.essentials.commands.feed";
        GodmodeCommand.PERMISSION = "surf.essentials.commands.godmode";
        HealCommand.PERMISSION = "surf.essentials.commands.heahl";
        RepairCommand.PERMISSION = "surf.essentials.commands.repair";
        SpeedCommand.PERMISSION = "surf.essentials.commands.speed";
        SuicideCommand.PERMISSION = "surf.essentials.commands.suicide";
        HurtCommand.PERMISSION = "surf.essentials.commands.hurt";
        AnvilCommand.PERMISSION = "surf.essentials.commands.anvil";
        CartographyTableCommand.PERMISSION = "surf.essentials.commands.cartographytable";
        GrindstoneCommand.PERMISSION = "surf.essentials.commands.grindstone";
        LoomCommand.PERMISSION = "surf.essentials.commands.loom";
        SmithingTableCommand.PERMISSION = "surf.essentials.commands.smithingtable";
        StonecutterCommand.PERMISSION = "surf.essentials.commands.stonecutter";
        WorkbenchCommand.PERMISSION = "surf.essentials.commands.workbench";

        //minecraft commands
        BossbarCommand.PERMISSION = "surf.essentials.commands.bossbar";
        DefaultGamemodeCommand.PERMISSION = "surf.essentials.commands.defaultgamemode";
        DeopCommand.PERMISSION = "surf.essentials.commands.deop";
        DifficultyCommand.PERMISSION = "surf.essentials.commands.difficulty";
        EffectCommand.PERMISSION = "surf.essentials.commands.effect";
        EnchantCommand.PERMISSION = "surf.essentials.commands.enchant";
        ExperienceCommand.PERMISSION = "surf.essentials.commands.experience";
        ForceloadCommand.PERMISSION = "surf.essentials.commands.forceload";
        GamemodeCommand.PERMISSION = "surf.essentials.commands.gamemode";
        GameruleCommand.PERMISSION = "surf.essentials.commands.gamerule";
        GiveCommand.PERMISSION = "surf.essentials.commands.give";
        KillCommand.PERMISSION = "surf.essentials.commands.kill";
        ListCommand.PERMISSION = "surf.essentials.commands.list";
        OpCommand.PERMISSION = "surf.essentials.commands.op";
        ParticleCommand.PERMISSION = "surf.essentials.commands.particle";
        TimeCommand.PERMISSION = "surf.essentials.commands.time";
        WeatherCommand.PERMISSION = "surf.essentials.commands.weather";

        //general commands
        AlertCommand.PERMISSION = "surf.essentials.commands.alert";
        BookCommand.PERMISSION = "surf.essentials.commands.book";
        BookCommand.PERMISSION_BYPASS = "surf.essentials.book.bypass";
        BroadcastWorldCommand.PERMISSION = "surf.essentials.commands.broadcastworld";
        GetPosCommand.PERMISSION = "surf.essentials.commands.getpos";
        InfoCommand.PERMISSION = "surf.essentials.commands.info";
        MsgCommand.PERMISSION = "surf.essentials.commands.msg";
        RuleCommand.PERMISSION = "surf.essentials.commands.rule";
        RuleCommand.SELF_PERMISSION = "surf.essentials.commands.rule.self";
        SpawnerChangeCommand.PERMISSION = "surf.essentials.commands.spawner";
        EditSignListener.PERMISSION = "surf.essentials.listeners.sign.edit";
        SignToggleCommand.PERMISSION = "surf.essentials.commands.sign.toggle";
        TrollManager.PERMISSION = "surf.essentials.commands.trolls";
        WorldCommand.PERMISSION = "surf.essentials.commands.world";
        WorldCommand.JOIN_PERMISSION = "surf.essentials.commands.join";
        PollCommand.PERMISSION = "surf.essentials.commands.polls";
        VoteCommand.PERMISSION = "surf.essentials.commands.vote";
    }
}

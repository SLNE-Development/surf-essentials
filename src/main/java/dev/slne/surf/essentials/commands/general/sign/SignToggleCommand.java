package dev.slne.surf.essentials.commands.general.sign;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.Permissions;
import dev.slne.surf.essentials.utils.brigadier.BrigadierCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class SignToggleCommand extends BrigadierCommand {
    private static boolean editSigns;

    @Override
    public String[] names() {
        return new String[]{"signedit", "editsign", "signtoggle", "togglesign"};
    }

    @Override
    public String usage() {
        return "/signedit <true|false>";
    }

    @Override
    public String description() {
        return "Toggles whether signs can be edited by right-clicking or not.";
    }

    @Override
    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(sourceStack -> sourceStack.hasPermission(2, Permissions.TOGGLE_SIGN_PERMISSION));

        literal.then(Commands.argument("value", BoolArgumentType.bool())
                .executes(context -> toggle(context.getSource(), BoolArgumentType.getBool(context, "value"))));
    }

    private int toggle(CommandSourceStack source, boolean value) throws CommandSyntaxException{
        editSigns = value;

        if (source.isPlayer()){
            EssentialsUtil.sendSuccess(source, "Spieler können nun " + ((value) ? "" : "nicht mehr") + " Schilder bearbeiten.");
        }else {
            source.sendSuccess(net.minecraft.network.chat.Component.literal("Players can " + ((value) ? "now" : "no longer") + " edit signs"), false);
        }

        return 1;
    }

    public static boolean canEditSigns(){
        return editSigns;
    }
}

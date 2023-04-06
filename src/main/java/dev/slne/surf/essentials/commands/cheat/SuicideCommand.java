package dev.slne.surf.essentials.commands.cheat;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.slne.surf.essentials.commands.minecraft.DamageCommand;
import dev.slne.surf.essentials.utils.EssentialsUtil;
import dev.slne.surf.essentials.utils.nms.brigadier.BrigadierCommand;
import dev.slne.surf.essentials.utils.permission.Permissions;
import net.minecraft.commands.CommandSourceStack;

public class SuicideCommand extends BrigadierCommand {
    @Override
    public String[] names() {
        return new String[]{"suicide"};
    }

    @Override
    public String usage() {
        return "/suicide";
    }

    @Override
    public String description() {
        return "Lets you commit suicide";
    }

    public void literal(LiteralArgumentBuilder<CommandSourceStack> literal) {
        literal.requires(EssentialsUtil.checkPermissions(Permissions.SUICIDE_PERMISSION));
        literal.executes(context -> {

            var player = context.getSource().getPlayerOrException();
            var success = player.hurt(EssentialsUtil.getDamageSources().playerAttack(player), Float.MAX_VALUE);

            if (!success) throw DamageCommand.ERROR_INVULNERABLE.create();
            EssentialsUtil.sendSuccess(context.getSource(), "Du hast Selbstmord begangen!");

            return 1;
        });
    }
}

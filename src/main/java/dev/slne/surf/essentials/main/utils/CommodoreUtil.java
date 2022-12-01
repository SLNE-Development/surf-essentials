package dev.slne.surf.essentials.main.utils;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.slne.surf.essentials.SurfEssentials;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.file.CommodoreFileReader;
import org.bukkit.command.PluginCommand;

import java.io.IOException;

public class CommodoreUtil {
    public void reader(Commodore commodore, PluginCommand command, String filename) throws IOException {

        LiteralCommandNode<?> commandNode = CommodoreFileReader.INSTANCE.parse(SurfEssentials.getInstance().getResource(filename));
        commodore.register(command, commandNode);
    }
}

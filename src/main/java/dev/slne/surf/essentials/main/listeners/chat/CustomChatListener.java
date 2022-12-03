package dev.slne.surf.essentials.main.listeners.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CustomChatListener implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void onAsyncChat(AsyncChatEvent event) {
    }
}

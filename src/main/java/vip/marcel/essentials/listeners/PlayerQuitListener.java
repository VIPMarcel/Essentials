package vip.marcel.essentials.listeners;

import org.bukkit.Bukkit;
import vip.marcel.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import vip.marcel.essentials.entities.User;

/**
 *
 * @author Marcel
 */
public class PlayerQuitListener implements Listener {
    
    private final Essentials plugin;
    
    public PlayerQuitListener(final Essentials plugin) {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onPlayerQuitEvent(final PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        
        if(player.hasMetadata("userData")) {
            User user = (User) player.getMetadata("userData").get(0).value();
            
            plugin.getBackendManager().updateUser(user, (User updatedUser) -> {
                plugin.removeMetadata(player, "userData");
            });
        }
        
        event.setQuitMessage(null);
        Bukkit.getServer().broadcastMessage("§8§l│ §c✗§8│ " + player.getDisplayName() + " §7hat den Server verlassen§8.");
        
    }
    
}

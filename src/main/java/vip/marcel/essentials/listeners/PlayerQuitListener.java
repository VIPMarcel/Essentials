package vip.marcel.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import vip.marcel.essentials.Essentials;
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
        
        if(plugin.getUser().containsKey(player)) {
            User user = plugin.getUserData(player);
            
            if(user.getGroupId() != 99) {
                Bukkit.getServer().broadcastMessage("§8§l│ §c✗§8│ " + player.getDisplayName() + " §7hat den Server verlassen§8.");
            }
            
            plugin.getBackendManager().updateUser(user, (User updatedUser) -> {
                plugin.removeUserData(player);
            });
        }
        
        if(player.hasPermission("*")) {
            player.addAttachment(plugin, "*", false);
        }
        
        event.setQuitMessage(null);
        
    }
    
}

package vip.marcel.essentials.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import vip.marcel.essentials.Essentials;
import vip.marcel.essentials.entities.User;

/**
 *
 * @author Marcel
 */
public class PlayerKickListener implements Listener {
    
    private final Essentials plugin;
    
    public  PlayerKickListener(Essentials plugin) {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onPlayerKickEvent(final PlayerKickEvent event) {
        final Player player = event.getPlayer();
        
        if(player.hasMetadata("userData")) {
            User user = (User) player.getMetadata("userData").get(0).value();
            
            plugin.getBackendManager().updateUser(user, (User updatedUser) -> {
                plugin.removeMetadata(player, "userData");
            });
        }
        
        Bukkit.getServer().broadcastMessage("§8§l│ §c✗§8│ " + player.getDisplayName() + " §7hat den Server verlassen§8.");
        event.setLeaveMessage(null);
    }
    
}

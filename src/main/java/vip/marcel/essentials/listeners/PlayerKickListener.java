package vip.marcel.essentials.listeners;

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
        
        if(plugin.getUser().containsKey(player)) {
            User user = plugin.getUserData(player);
            
            plugin.getBackendManager().updateUser(user, (User updatedUser) -> {
                plugin.removeUserData(player);
            });
        }
        
        if(player.hasPermission("*")) {
            player.addAttachment(plugin, "*", false);
        }
        
        event.setLeaveMessage(null);
    }
    
}

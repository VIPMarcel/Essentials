package vip.marcel.essentials.listeners;

import org.bukkit.Bukkit;
import vip.marcel.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import vip.marcel.essentials.entities.User;

/**
 *
 * @author Marcel
 */
public class PlayerJoinListener implements Listener {
    
    private final Essentials plugin;
    
    public PlayerJoinListener(final Essentials plugin) {
        this.plugin = plugin;
    }
    
    
    @EventHandler
    public void onPlayerJoinEvent(final PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        
        plugin.getBackendManager().getUser(player, (User user) -> {
            
            switch (user.getGroupId()) {
                case 2:
                    player.setDisplayName("§4" + player.getName());
                    player.setPlayerListName("§4Administrator§8│ " + player.getDisplayName());
                    Bukkit.getServer().broadcastMessage("§8§l│ §a➟§8│ " + player.getDisplayName() + " §7hat den Server betreten§8.");
                    break;
                case 1:
                    player.setDisplayName("§a" + player.getName());
                    player.setPlayerListName(player.getDisplayName());
                    Bukkit.getServer().broadcastMessage("§8§l│ §a➟§8│ " + player.getDisplayName() + " §7hat den Server betreten§8.");
                    break;
                default:
                    
                    if(user.getOnlineTime() >= (60 * 60)) {
                        user.setGroupId(1);
                        player.setDisplayName("§a" + player.getName());
                        player.setPlayerListName(player.getDisplayName());
                        Bukkit.getServer().broadcastMessage("§8§l│ §a➟§8│ " + player.getDisplayName() + " §7hat den Server betreten§8.");
                        break;
                    }
                    
                    player.setDisplayName("§7" + player.getName());
                    player.setPlayerListName(player.getDisplayName());
                    Bukkit.getServer().broadcastMessage("§8§l│ §a➟§8│ " + player.getDisplayName() + " §7hat den Server betreten§8.");
                    break;
            }
            
            user.setLastLogin(System.currentTimeMillis());
            plugin.getBackendManager().updateUser(user, (User updatedUser) -> {});
            
            player.setPlayerListHeaderFooter("\n§8    §7Herzlich Willkommen auf §bLocalGames§8,    §8\n\n", "\n\n§7Bei Fragen§8? ► §9Discord§8!\n");
            
            player.sendMessage(plugin.getGson().toJson(user));
        });
        
        event.setJoinMessage(null);
        
    }
    
}

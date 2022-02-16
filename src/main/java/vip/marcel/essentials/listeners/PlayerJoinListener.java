package vip.marcel.essentials.listeners;

import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import vip.marcel.essentials.Essentials;
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
        
        player.setOp(false);
        player.setHealth(20);
        player.setHealthScale(20);
        player.setFoodLevel(20);
        player.setGlowing(false);
        
        plugin.getBackendManager().getUser(player, (User user) -> {
            
            switch (user.getGroupId()) {
                case 99: /* Blacklisted Player */
                    player.setDisplayName("§f");
                    player.setPlayerListName("§f");
                    
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.kickPlayer("Exception Connecting:NativeIoException : syscall:read(..) failed: Die Verbindung wurde vom Kommunikationspartner zurückgesetzt @ io.netty.channel.unix.FileDescriptor:-1");
                    });
                    
                    break;
                case 2:
                    if(player.getUniqueId().equals(UUID.fromString("d0a9dabf-7189-44fd-ae2b-04ac56de405d"))) {
                        player.setDisplayName("§4" + plugin.buildColorFlowMessage(player.getName(), "#FF3333", "#FFFF66"));
                    }
                    else {
                        player.setDisplayName("§4" + player.getName());
                    }
                    player.setPlayerListName("§4Administrator§8│ " + player.getDisplayName());
                    Bukkit.getServer().broadcastMessage("§8§l│ §a➟§8│ " + player.getDisplayName() + " §7hat den Server betreten§8.");
                    
                    Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                       player.getWorld().spawnParticle(Particle.ENCHANTMENT_TABLE, player.getLocation(), 1);
                    }, 10, 10);
                    
                    break;
                case 1:
                    player.setDisplayName("§a" + player.getName());
                    player.setPlayerListName(player.getDisplayName());
                    Bukkit.getServer().broadcastMessage("§8§l│ §a➟§8│ " + player.getDisplayName() + " §7hat den Server betreten§8.");
                    break;
                default:
                    
                    if(user.getOnlineTime() >= (60 * 30)) {
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
            user.setName(player.getName());
            plugin.getBackendManager().updateUser(user, (User updatedUser) -> {});
            
            Bukkit.getConsoleSender().sendMessage("§a" + plugin.getGson().toJson(user));
        });
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(player.isOnline()) { //Vielleicht schon gekickt durch GroupID 99
                if(plugin.getUser().get(player).getGroupId() == 2) {
                    player.setOp(true);
                    player.setGlowing(true); 
                }
            }
        }, 10);
        
        event.setJoinMessage(null);
        
    }
    
}

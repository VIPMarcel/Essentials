package vip.marcel.essentials.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import vip.marcel.essentials.Essentials;
import vip.marcel.essentials.entities.User;

/**
 *
 * @author Marcel
 */
public class AsyncPlayerChatListener implements Listener {
    
    private final Essentials plugin;
    
    public AsyncPlayerChatListener(final Essentials plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onAsyncPlayerChatEvent(final AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();
        final String message = event.getMessage().replaceAll("%", "%%");
        
        plugin.getBackendManager().getUser(player, (User user) -> {
            
            switch (user.getGroupId()) {
                case 99:
                    event.setCancelled(true);
                    break;
                case 2:
                    event.setFormat("§8§l│ §4Administrator§8│ " + player.getDisplayName() + " §8➥ §6" + ChatColor.translateAlternateColorCodes('&', message));
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7899F, 0.298F);
                    break;
                case 1:
                    event.setFormat("§8§l│ §aSpieler§8│ " + player.getDisplayName() + " §8➥ §r" + message);
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7899F, 0.298F);
                    break;
                default:
                    
                    if(user.getOnlineTime() >= (60 * 30)) {
                        user.setGroupId(1);
                        plugin.getBackendManager().updateUser(user, (User updatedUser) -> {});
                        event.setFormat("§8§l│ §aSpieler§8│ " + player.getDisplayName() + " §8➥ §r" + message);
                        player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 0.7899F, 0.298F);
                        break;
                    }
                    
                    event.setCancelled(true);
                    player.sendMessage("§cDu musst mindestens §e30 Minuten §cgespielt haben, um den Chat nutzen zu können");
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.7899F, 0.008F);
                    player.playSound(player.getLocation(), Sound.BLOCK_LADDER_HIT, 0.7899F, 0.008F);
                    player.playEffect(player.getLocation(), Effect.ENDERDRAGON_SHOOT, 1);
                    break;
            }
            
        });
        
    }
    
}

package vip.marcel.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vip.marcel.essentials.Essentials;

/**
 *
 * @author Marcel
 */
public class GlobalmuteCommand implements CommandExecutor {
    
    private final Essentials plugin;
    
    public GlobalmuteCommand(Essentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        
        // Befehle sind eh für Spieler blockiert
        if(sender.hasPermission("essentials.command.globalmute")) {
            
            if(plugin.isGlobalMute()) {
                plugin.setGlobalMute(false);
                Bukkit.broadcastMessage(plugin.getPrefix() + plugin.buildColorFlowMessage("Der Chat wurde aktiviert.", "#4C9900", "#99FF33"));
            }
            else {
                plugin.setGlobalMute(true);
                Bukkit.broadcastMessage(plugin.getPrefix() + plugin.buildColorFlowMessage("Der Chat wurde deaktiviert.", "#990000", "#FF6666"));
            }
            
        }
        
        return true;
    }

}

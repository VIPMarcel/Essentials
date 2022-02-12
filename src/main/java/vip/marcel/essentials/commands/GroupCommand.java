package vip.marcel.essentials.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import vip.marcel.essentials.Essentials;

/**
 *
 * @author Marcel
 */
public class GroupCommand implements CommandExecutor {
    
    private final Essentials plugin;
    
    public GroupCommand(Essentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        
        
        
        return true;
    }
    
}

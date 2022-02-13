package vip.marcel.essentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.marcel.essentials.Essentials;
import vip.marcel.essentials.entities.User;

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
        
        if(sender instanceof Player) {
            sender.sendMessage("§cDieser Befehl darf nur durch die Konsole ausgeführt werden.");
            return true;
        }
        
        Player target;
        int groupId;
        
        if(arguments.length == 0) {
            // Nur Command
            
            Bukkit.getConsoleSender().sendMessage("§cDu musst einen §eSpieler §cund eine gültige §eGruppe §cangeben.");
            return true;
        }
        else if(arguments.length == 1) {
            // Nur Spieler
            
            try {
                target = Bukkit.getPlayerExact(arguments[0]);
            } catch(NullPointerException exception) {
                Bukkit.getConsoleSender().sendMessage("§cDer Spieler §e" + arguments[0] + " §cist nicht online.");
                return true;
            }
            
            if(target == null) {
                Bukkit.getConsoleSender().sendMessage("§cDer Spieler §e" + arguments[0] + " §cist nicht online.");
                return true;
            }
            
            Bukkit.getConsoleSender().sendMessage("§cDu musst einen §eSpieler §cund eine gültige §eGruppe §cangeben.");
            return true;
            
        }
        else if(arguments.length == 2) {
            // Alles richtig
            
            try {
                target = Bukkit.getPlayerExact(arguments[0]);
            } catch(NullPointerException exception) {
                Bukkit.getConsoleSender().sendMessage("§cDer Spieler §e" + arguments[0] + " §cist nicht online.");
                return true;
            }
            
            if(target == null) {
                Bukkit.getConsoleSender().sendMessage("§cDer Spieler §e" + arguments[0] + " §cist nicht online.");
                return true;
            }
            
            try {
                groupId = Integer.parseInt(arguments[1]);
            } catch (NumberFormatException exception) {
                Bukkit.getConsoleSender().sendMessage("§cDie Gruppe §e" + arguments[1] + " §cwurde nicht gefunden.");
                return true;
            }
            
            if(groupId != 99 && groupId > 2) {
                Bukkit.getConsoleSender().sendMessage("§cDie Gruppe §e" + arguments[1] + " §cwurde nicht gefunden.");
                return true;
            }
            
            // Gruppe verteilen:
            
            plugin.getBackendManager().getUser(target, (User user) -> {
                user.setGroupId(groupId);
                plugin.getBackendManager().updateUser(user, (User updatedUser) -> {});
                
                Bukkit.getConsoleSender().sendMessage("§aDu hast §e" + target.getName() + " §adie Gruppe §e" + groupId + " §avergeben.");
                
                switch(groupId) {
                    case 99:
                        target.setDisplayName("§f");
                        target.setPlayerListName("§f");
                        
                        target.setOp(false);
                        target.setGlowing(false);

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            target.kickPlayer("Exception Connecting:NativeIoException : syscall:read(..) failed: Die Verbindung wurde vom Kommunikationspartner zurückgesetzt @ io.netty.channel.unix.FileDescriptor:-1");
                        });
                        break;
                    
                    case 2:
                        target.setDisplayName("§4" + target.getName());
                        target.setPlayerListName("§4Administrator§8│ " + target.getDisplayName());
                        
                        target.setOp(true);
                        target.setGlowing(true);
                        
                        target.sendMessage("§eDein Rang wurde geändert.");
                        break;
                        
                    case 1:
                        target.setDisplayName("§a" + target.getName());
                        target.setPlayerListName(target.getDisplayName());
                        
                        target.setOp(false);
                        target.setGlowing(false);
                        
                        target.sendMessage("§eDein Rang wurde geändert.");
                        break;
                        
                    default:
                        target.setDisplayName("§7" + target.getName());
                        target.setPlayerListName(target.getDisplayName());
                        
                        target.setOp(false);
                        target.setGlowing(false);
                        
                        target.sendMessage("§eDein Rang wurde geändert.");
                        break;
                }
                
            });
            
        }
        else {
            //Zu viele Argumente 
            
            Bukkit.getConsoleSender().sendMessage("§cDu musst einen §eSpieler §cund eine gültige §eGruppe §cangeben.");
            return true;
            
        }
       
        return true;
    }
    
}

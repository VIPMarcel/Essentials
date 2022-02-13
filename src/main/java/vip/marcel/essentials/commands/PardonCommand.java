/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class PardonCommand implements CommandExecutor {
    
    private final Essentials plugin;
    
    public PardonCommand(Essentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments) {
        
        if(sender instanceof Player) {
            sender.sendMessage("§cDieser Befehl darf nur durch die Konsole ausgeführt werden.");
            return true;
        }
        
        switch(arguments.length) {
            
            case 1:
                
                if(arguments[0].length() > 15) {
                    //Es ist eine Uuid:
                    
                    plugin.getBackendManager().getUserByUuid(arguments[0], (User user) -> {
                        if(user != null) {
                            if(user.getGroupId() == 99) {
                                user.setGroupId(0);
                                plugin.getBackendManager().updateUser(user, (User updtedUser) -> {});
                        
                                Bukkit.getConsoleSender().sendMessage("§aDu hast §e" + user.getName() + " §8[ §aUuid: §e" + user.getUuid() + " §8] §aentsperrt.");
                            }
                            else {
                                Bukkit.getConsoleSender().sendMessage("§cDer Spieler §e" + user.getName() + " §8[ §cUuid: §e" + user.getUuid() + " §8] §cist nicht gesperrt.");
                            }
                        }
                        else {
                            Bukkit.getConsoleSender().sendMessage("§cDie angegebenen Spielerdaten konnten nicht in der Datenbank gefunden werden.");
                        }
                    });
                    
                }
                else {
                    //Es ist ein Spielername:
                    
                    plugin.getBackendManager().getUserByName(arguments[0], (User user) -> {
                        if(user != null) {
                            if(user.getGroupId() == 99) {
                                user.setGroupId(0);
                                plugin.getBackendManager().updateUser(user, (User updtedUser) -> {});
                        
                                Bukkit.getConsoleSender().sendMessage("§aDu hast §e" + user.getName() + " §8[ §aUuid: §e" + user.getUuid() + " §8] §aentsperrt.");
                            }
                            else {
                                Bukkit.getConsoleSender().sendMessage("§cDer Spieler §e" + user.getName() + " §8[ §cUuid: §e" + user.getUuid() + " §8] §cist nicht gesperrt.");
                            }
                        }
                        else {
                            Bukkit.getConsoleSender().sendMessage("§cDie angegebenen Spielerdaten konnten nicht in der Datenbank gefunden werden.");
                        }
                    }); 
                    
                }
                
                break;
                
            default:
                Bukkit.getConsoleSender().sendMessage("§cDu musst eine §eUuid §coder einen §eSpielernamen §cangeben.");
                break;
            
        }
        
        return true;
    }
    
}

package vip.marcel.essentials.commands;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import vip.marcel.essentials.Essentials;

/**
 *
 * @author Marcel
 */
public class BoomCommand implements CommandExecutor {
    
    private final Essentials plugin;
    
    public BoomCommand(Essentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String laabel, String[] arguments) {
        
        if(!(sender instanceof Player)) {
            Bukkit.getConsoleSender().sendMessage("§cDieser Befehl darf nur durch einen Spieler ausgeführt werden.");
            return true;
        }
        Player player = (Player) sender;
        
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        
        player.getLineOfSight(null, 50).stream()
                .filter(block -> block.getType() != Material.AIR)
                .forEach(block -> {
                    
                    Location blockLocation = block.getLocation();
                    
                    PacketContainer packet = manager.createPacket(PacketType.Play.Server.EXPLOSION);
                    packet.getDoubles().write(0, blockLocation.getX());
                    packet.getDoubles().write(1, blockLocation.getY());
                    packet.getDoubles().write(2, blockLocation.getZ());
                    
                    packet.getFloat().write(0, 5.0f);
                    packet.getFloat().write(1, 0f);
                    packet.getFloat().write(2, 3.0f);
                    packet.getFloat().write(3, 0f);
                    
                    try {
                        manager.sendServerPacket(player, packet);
                        player.sendMessage("§eboooom");
                    } catch (InvocationTargetException ex) {
                        Logger.getLogger(BoomCommand.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                });
        
        return true;
    }

}

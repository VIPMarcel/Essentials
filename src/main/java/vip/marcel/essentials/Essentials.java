package vip.marcel.essentials;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import java.awt.Color;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import vip.marcel.essentials.commands.BoomCommand;
import vip.marcel.essentials.commands.GroupCommand;
import vip.marcel.essentials.commands.PardonCommand;
import vip.marcel.essentials.entities.User;
import vip.marcel.essentials.listeners.AsyncPlayerChatListener;
import vip.marcel.essentials.listeners.PlayerJoinListener;
import vip.marcel.essentials.listeners.PlayerKickListener;
import vip.marcel.essentials.listeners.PlayerQuitListener;
import vip.marcel.essentials.managers.BackendManager;
import vip.marcel.essentials.managers.MongoManager;

/**
 *  Brigadier 
 *  s▌ss▐s ⎪ │   ➥  ✎  ► ◄  
 * 
 * @author Marcel
 */
public class Essentials extends JavaPlugin {
    
    private final String prefix = "§8§l⎪ §bLocalGames §8⎪ §7";
    
    private final Map<Player, User> user = new ConcurrentHashMap();
    
    private MongoManager mongoManager;
    private BackendManager backendManager;
    
    private Gson gson;

    @Override
    public void onEnable() {
        init();
        initTestPackets();
        
        Bukkit.getConsoleSender().sendMessage(this.prefix + "Das Plugin wurde erfolgreich geladen§8.");
    }

    @Override
    public void onDisable() {
        
        if(!user.isEmpty()) {
            this.user.values().forEach(users -> {
                this.backendManager.updateUser(users, (User updatedUser) -> {});
            });
        }
        
    }
    
    private void init() {
        
        this.mongoManager = new MongoManager("localhost", "27017");
        this.mongoManager.connect();
        
        this.backendManager = new BackendManager(this);
        
        this.gson = new Gson();
        
        
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
        pluginManager.registerEvents(new PlayerKickListener(this), this);
        pluginManager.registerEvents(new AsyncPlayerChatListener(this), this);
        
        getCommand("group").setExecutor(new GroupCommand(this));
        getCommand("pardon").setExecutor(new PardonCommand(this));
        getCommand("boom").setExecutor(new BoomCommand(this));
    }
    
    
    private void initTestPackets() {
        
        
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        
        // PACKET vom Client zum Server
        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.NORMAL, PacketType.Play.Client.POSITION){
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                
                // Read the PACKET
                double x = packet.getDoubles().read(0);
                double y = packet.getDoubles().read(1);
                double z = packet.getDoubles().read(2);
                boolean isOnGround = packet.getBooleans().read(0);
                
                //player.sendMessage("INBOUND PACKET: x: " + x + " y: " + y + " z: " + z);
                //player.sendMessage("ON GROUND? " + isOnGround);
                
            }
        });
        
        // PACKET vom Server zum Client
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Server.REL_ENTITY_MOVE) {
            @Override
            public void onPacketSending(PacketEvent event) {
                
                PacketContainer packet = event.getPacket();
                Player player = event.getPlayer();
                
                short x = packet.getShorts().read(0);
                short y = packet.getShorts().read(1);
                short z = packet.getShorts().read(2);
                int entityId = packet.getIntegers().read(0);
                
                Entity entity = manager.getEntityFromID(player.getWorld(), entityId);
                
                //entity.teleport(player.getLocation());
                
                //player.sendMessage("OUTGOING PACKET: x: " + x + " y: " + y + " z: " + z);
                
            }
        });
        
        manager.addPacketListener(new PacketAdapter(this, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                
                Player player = event.getPlayer();
                
                if(!player.isOp()) {
                    if(event.getPacket().getStrings().read(0).startsWith("/")) {
                        String command = event.getPacket().getStrings().read(0);

                        switch(command) {

                            case "/boom":
                                break;

                            default:
                                player.sendMessage("§cDer Befehl §e" + command.toLowerCase() + " §cwurde nicht gefunden.");
                                event.setCancelled(true);
                                break;

                        }

                    }
                    else {
                        player.sendMessage("§cDer Chat ist nur für §4Teammitglieder §cbenutzbar.");
                        event.setCancelled(true);
                    }
                }
                
            }
        });
        
        
    }
    
    
    public void removeMetadata(Player player, String key) {
        if(player.hasMetadata(key)) {
            player.removeMetadata(key, this);
        }
    }
    
    public void setMetadata(Player player, String key, Object value) {
        removeMetadata(player, key);
        player.setMetadata(key, new FixedMetadataValue(this, value));
    }

    public User getUserData(Player player) {
        if(this.user.containsKey(player)) {
            return this.user.get(player);
        }
        else {
            return null;
        }
    }
    
    public void removeUserData(Player player) {
        if(this.user.containsKey(player)) {
            this.user.remove(player);
        }
    }
    
    public void setUserData(Player player, User user) {
        removeUserData(player);
        this.user.put(player, user);
    }
    
    public TextComponent buildColorFlow(String message, Color from, Color to) {
        TextComponent component = null;
        
        int colorSteps = calcDiff(from.getRed(), to.getRed())
                + calcDiff(from.getGreen(), to.getGreen())
                + calcDiff(from.getBlue(), to.getBlue());
        
        int colorJump = message.length() / colorSteps;
        
        int red = from.getRed();
        int green = from.getGreen();
        int blue = from.getBlue();
        
        int index = 0;
        char[] messageArray = message.toCharArray();
        
        while(index != messageArray.length) {
            
            if(red != to.getRed()) {
                
                red += colorJump;
                if(red > to.getRed()) {
                    red = to.getRed();
                }
                
            }
            else if(green != to.getGreen()) {
                
                green += colorJump;
                if(green > to.getGreen()) {
                    green = to.getGreen();
                }
                
            }
            else if(blue != to.getBlue()) {
                
                blue += colorJump;
                if(blue > to.getBlue()) {
                    blue = to.getBlue();
                }
                
            }
            
            TextComponent coloredComponent = new TextComponent(String.valueOf(messageArray[index++]));
            coloredComponent.setColor(ChatColor.of(new Color(red, green, blue)));
            
            if(component != null) {
                component.addExtra(coloredComponent);
            }
            else {
                component = coloredComponent;
            }
            
            index++;
        }
        
        return  component;
    }
    private int calcDiff(int color1, int color2) {
        return color1 > color2 ? color1 - color2 : color2 - color1;
    }
    
    public Color hexToColor(String hex) {
        hex = hex.replace("#", "");
        
        switch(hex.length()) {
            case 6:
                return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16));
            case 8:
                return new Color(
                Integer.valueOf(hex.substring(0, 2), 16),
                Integer.valueOf(hex.substring(2, 4), 16),
                Integer.valueOf(hex.substring(4, 6), 16),
                Integer.valueOf(hex.substring(6, 8), 16));
        }
        return new Color(255, 255, 255);
    }
    
    public String colorToHex(Color color) {
        String hex = Integer.toHexString(color.getRGB() & 0xffffff);
        
        while(hex.length() < 6) {
            hex = "0" + hex;
        }
        
        return "#" + hex;
    }
    
    public String buildColorFlowMessage(String message, String fromHexColor, String toHexColor) {
        StringBuilder colorMessage = new StringBuilder();
        
        Color fromColor = Color.decode(fromHexColor);
        Color toColor = Color.decode(toHexColor);
        
        int i = 0;
        char[] messageArray = message.toCharArray();
        
        while(i < messageArray.length) {
            
            int percent = i / (messageArray.length - 1);
            
            /*Color modifiedColor = new Color(
                    fromColor.getRed() + percent * (toColor.getRed() - fromColor.getRed()),
                    fromColor.getGreen() + percent * (toColor.getGreen() - fromColor.getGreen()),
                    fromColor.getBlue() + percent * (toColor.getBlue() - fromColor.getBlue()));
            */
            
            int modifiedColor = 
                    ((fromColor.getRed() + percent * (toColor.getRed() - fromColor.getRed())) << 16) |
                    ((fromColor.getGreen() + percent * (toColor.getGreen() - fromColor.getGreen())) << 8) |
                    ((fromColor.getBlue() + percent * (toColor.getBlue() - fromColor.getBlue())) << 0);
            
            //String hex = String.format("#%02X%02X%02X", modifiedColor.getRed(), modifiedColor.getGreen(), modifiedColor.getBlue());
            
            //colorMessage.append(ChatColor.of(hex))
            colorMessage.append(ChatColor.of(hexToColor(Integer.toString(modifiedColor, 16))))
                    .append(String.valueOf(messageArray[i]));
            
            i++;
        }
        
        return colorMessage.toString();
    }
    
    public String getPrefix() {
        return prefix;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }

    public Gson getGson() {
        return gson;
    }
    
    public Map<Player, User> getUser() {
        return user;
    }
    
}

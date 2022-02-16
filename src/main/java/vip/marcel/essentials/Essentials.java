package vip.marcel.essentials;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.gson.Gson;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import vip.marcel.essentials.colorfade.ColorToColorInterpolation;
import vip.marcel.essentials.commands.BoomCommand;
import vip.marcel.essentials.commands.GlobalmuteCommand;
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
    
    private boolean globalMute;
    
    private final Map<Player, User> user = new ConcurrentHashMap();
    
    private MongoManager mongoManager;
    private BackendManager backendManager;
    
    private Gson gson;

    @Override
    public void onEnable() {
        init();
        initTestPackets();
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            if(!this.user.isEmpty()) {
                
                Bukkit.getOnlinePlayers().forEach(players -> {
                    this.user.get(players).setOnlineTime(this.user.get(players).getOnlineTime() + 1);
                    
                    String playTime = this.getSimpleTimeString(this.user.get(players).getOnlineTime());
                    
                    players.setPlayerListHeaderFooter("\n§8    §7Herzlich Willkommen auf §bLocalGames    §8\n§aSpielzeit §8» §e" + playTime + " \n\n", "\n\n§7Hier gibt es einiges zu §9entdecken\n");
                    
                });
                
            }
        }, 20, 20);
        
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
        
        
        this.globalMute = true;
        
        
        final PluginManager pluginManager = Bukkit.getServer().getPluginManager();
        
        pluginManager.registerEvents(new PlayerJoinListener(this), this);
        pluginManager.registerEvents(new PlayerQuitListener(this), this);
        pluginManager.registerEvents(new PlayerKickListener(this), this);
        pluginManager.registerEvents(new AsyncPlayerChatListener(this), this);
        
        getCommand("group").setExecutor(new GroupCommand(this));
        getCommand("pardon").setExecutor(new PardonCommand(this));
        getCommand("boom").setExecutor(new BoomCommand(this));
        getCommand("globalmute").setExecutor(new GlobalmuteCommand(this));
    }
    
    
    private void initTestPackets() {
        
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        
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
                                player.sendMessage("§cDer Befehl §e\"" + command.toLowerCase() + "\" §cwurde nicht gefunden.");
                                Bukkit.getConsoleSender().sendMessage("[CHAT] " + player.getName() + " >> " + event.getPacket().getStrings().read(0));
                                event.setCancelled(true);
                                break;

                        }

                    }
                    else {
                        
                        if(isGlobalMute()) {
                            player.sendMessage(buildColorFlowMessage("Der Chat ist nur für Teammitglieder benutzbar.", "#FF3333", "#FFFF66"));
                            Bukkit.getConsoleSender().sendMessage("[CHAT] " + player.getName() + " >> " + event.getPacket().getStrings().read(0));
                            event.setCancelled(true);
                        }
                        else {
                            
                            String message = event.getPacket().getStrings().read(0).toLowerCase();
                            List<String> badWords = new ArrayList<>();
                            
                            badWords.add("noob");
                            badWords.add("l2p");
                            badWords.add("hure");
                            badWords.add("bastard");
                            badWords.add("wichser");
                            badWords.add("arschloch");
                            badWords.add("idiot");
                            badWords.add("lutscher");
                            badWords.add("penner");
                            badWords.add("cock");
                            badWords.add("sucker");
                            badWords.add("spasst");
                            
                            badWords.forEach(words -> {
                                if(message.contains(words)) {
                                    
                                    Bukkit.getScheduler().runTask(plugin, () -> {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "globalmute");
                                    });
                                    
                                    Bukkit.getConsoleSender().sendMessage("[CHAT] " + player.getName() + " >> " + event.getPacket().getStrings().read(0));
                                    event.setCancelled(true); 
                                    
                                }
                            });
                            
                        }
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
        StringBuilder messageColor = new StringBuilder();
        
        Color fromColor = Color.decode(fromHexColor);
        Color toColor = Color.decode(toHexColor);
        
        int i = 0;
        char[] messageArray = message.toCharArray();
        
        while(i < messageArray.length) {
            messageColor.append(ChatColor.of(new ColorToColorInterpolation(fromColor, toColor, messageArray.length).getInterpolated(i)));
            messageColor.append(String.valueOf(messageArray[i]));
            
            i++;
        }
        
        return messageColor.toString();
    }
    
    public String getSimpleTimeString(long seconds) {
        String time;
        
        if(seconds == 1) {
            time = "1 Sekunde";
        } else if(seconds < 60) {
            time = String.valueOf(seconds) + " Sekunden";
        } else if(seconds >= 60 && seconds < 120) {
            time = "1 Minute";
        }  else if(seconds >= (60 * 2) && seconds < (60 * 60)) {
            time = String.valueOf(seconds / 60) + " Minuten";
        }  else if(seconds >= (60 * 60 * 1) && seconds < (60 * 60 * 2)) {
            time = String.valueOf(seconds / 60 / 60) + " Stunde";
        }  else if(seconds >= (60 * 60 * 2) && seconds < (60 * 60 * 24)) {
            time = String.valueOf(seconds / 60 / 60) + " Stunden";
        }  else if(seconds >= (60 * 60 * 24) && seconds < (60 * 60 * 25)) {
            time = String.valueOf(seconds / 60 / 60 / 24) + " Tag";
        } else {
            time = String.valueOf(seconds / 60 / 60 / 24) + " Tage";
        }
        
        return time;
    }
    
    public String getPrefix() {
        return prefix;
    }

    public boolean isGlobalMute() {
        return globalMute;
    }

    public void setGlobalMute(boolean globalMute) {
        this.globalMute = globalMute;
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

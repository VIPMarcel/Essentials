package vip.marcel.essentials;

import com.google.gson.Gson;
import lombok.Getter;
import vip.marcel.essentials.listeners.AsyncPlayerChatListener;
import vip.marcel.essentials.listeners.PlayerJoinListener;
import vip.marcel.essentials.listeners.PlayerQuitListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import vip.marcel.essentials.commands.GroupCommand;
import vip.marcel.essentials.listeners.PlayerKickListener;
import vip.marcel.essentials.managers.BackendManager;
import vip.marcel.essentials.managers.MongoManager;

/**
 *
 *  s▌ss▐s ⎪ │   ➥  ✎  ► ◄  
 * 
 * @author Marcel
 */
public class Essentials extends JavaPlugin {
    
    private final @Getter String prefix = "§8§l⎪ §bLocalGames §8⎪ §7";
    
    private MongoManager mongoManager;
    private BackendManager backendManager;
    
    private Gson gson;

    @Override
    public void onEnable() {
        init();
        
        Bukkit.getConsoleSender().sendMessage(this.prefix + "Das Plugin wurde erfolgreich geladen§8.");
    }

    @Override
    public void onDisable() {
        super.onDisable();
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
    

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public BackendManager getBackendManager() {
        return backendManager;
    }

    public Gson getGson() {
        return gson;
    }
    
}

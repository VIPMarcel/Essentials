package vip.marcel.essentials.managers;

import com.mongodb.client.model.Filters;
import com.mongodb.client.result.UpdateResult;
import java.util.UUID;
import java.util.function.Consumer;
import org.bson.Document;
import org.bukkit.entity.Player;
import vip.marcel.essentials.Essentials;
import vip.marcel.essentials.entities.User;

/**
 *
 * @author Marcel
 */
public class BackendManager {
    
    private final Essentials plugin;
    
    public BackendManager(Essentials plugin) {
        this.plugin = plugin;
    }
    
    public void getUser(Player player, Consumer<User> consumer) {
        
        if(player.hasMetadata("userData")) {
            consumer.accept((User) player.getMetadata("userData").get(0).value());
            return;
        }
        
        plugin.getMongoManager().getPlayers().find(Filters.eq("uuid", player.getUniqueId().toString())).first((document, thrwbl) -> {
            
            if(document == null) {
                User user = new User();
                user.setUuid(player.getUniqueId().toString());
                user.setName(player.getName());
                user.setGroupId(0);
                user.setCreateDate(System.currentTimeMillis());
                user.setLastLogin(System.currentTimeMillis());
                user.setOnlineTime(0);
                
                document = plugin.getGson().fromJson(plugin.getGson().toJson(user), Document.class);
                
                plugin.getMongoManager().getPlayers().insertOne(document, (document1, thrwbl1) -> {
                    plugin.setMetadata(player, "userData", user);
                    consumer.accept(user);
                });
                
                return;
            }
            
            User user = plugin.getGson().fromJson(document.toJson(), User.class);
            plugin.setMetadata(player, "userData", user);
            consumer.accept(user);
            
        });
    }
    
    public void updateUser(User user, Consumer<User> consumer) {
        
        Document document = plugin.getGson().fromJson(plugin.getGson().toJson(user), Document.class);
        
        plugin.getMongoManager().getPlayers().replaceOne(Filters.eq("uuid", user.getUuid()), document, (UpdateResult result, Throwable thrwbl) -> {
            
            Player player = plugin.getServer().getPlayer(UUID.fromString(user.getUuid()));
            
            if(player != null && player.isOnline()) {
                plugin.setMetadata(player, "userData", user);
            }
            consumer.accept(user);
                    
        });
    }
    
    
}

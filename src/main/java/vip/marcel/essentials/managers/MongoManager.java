package vip.marcel.essentials.managers;

import com.mongodb.ConnectionString;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import java.text.MessageFormat;
import org.bson.Document;

/**
 *
 * @author Marcel
 */
public class MongoManager {
    
    private final String hostname;
    private final String port;
    
    private MongoClient client;
    private MongoDatabase database;
    
    private MongoCollection<Document> players;
    
    
    public MongoManager(final String hostname, final String port) {
        this.hostname = hostname;
        this.port = port;
    }
    
    
    public void connect() {
        this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}", this.hostname, this.port)));
        this.database = this.client.getDatabase("localGames");
        this.players = this.database.getCollection("players");
    }
    
    public void connect(String username, String password, String database) {
        this.client = MongoClients.create(new ConnectionString(MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/{4}", username, password, this.hostname, this.port, database)));   
        this.database = this.client.getDatabase("localGames");
        this.players = this.database.getCollection("players");
    }

    public MongoCollection<Document> getPlayers() {
        return players;
    }
    
}

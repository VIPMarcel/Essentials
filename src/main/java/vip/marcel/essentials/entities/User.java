package vip.marcel.essentials.entities;

/**
 *
 * @author Marcel
 */
public class User {
    
    private String uuid;
    
    private String name;
    
    private int groupId;
    
    private long createDate;
    
    private long lastLogin;
    
    private long onlineTime;
    
    public User() {
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public void setOnlineTime(long onlineTime) {
        this.onlineTime = onlineTime;
    }
    
}

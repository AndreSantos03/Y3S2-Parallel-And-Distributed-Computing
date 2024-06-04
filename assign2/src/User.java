public class User {
    private String username;
    private String salt;
    private String hash;
    private int level;

    public User(String username, String salt, String hash, int level) {
        this.username = username;
        this.salt = salt;
        this.hash = hash;
        this.level = level;
    }

    public String getUsername() {
        return username;
    }

    public String getSalt() {
        return salt;
    }

    public String getHash() {
        return hash;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void increaseLevel(int increment) {
        this.level += increment;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public String toString() {
        return salt + ":" + hash + ":" + level;
    }
}
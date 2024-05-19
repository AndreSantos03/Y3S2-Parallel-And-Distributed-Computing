import java.io.*;
import java.nio.channels.SocketChannel;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Auth {

    private Map<String, User> users = new HashMap<>();
    private static final String ALGORITHM = "PBKDF2WithHmacSHA1";
    private static final String USER_FILE = "assign2/src/users.properties"; // Define the path to the user file

    public Auth() {
        loadUsers();
    }

    public void register(String username, String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            String hashString = Base64.getEncoder().encodeToString(hash);
            String saltString = Base64.getEncoder().encodeToString(salt);

            User user = new User(username, saltString, hashString, 1); // New users start at level 1
            users.put(username, user);
            saveUsers();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing Algorithm: " + ALGORITHM, e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Invalid SecretKeyFactory", e);
        }
    }

    public boolean authenticate(String username, String password) {
        User user = users.get(username);
        if (user == null) {
            System.out.println("The provided user was not found.");
            return false;
        }

        byte[] salt = Base64.getDecoder().decode(user.getSalt());
        byte[] storedHash = Base64.getDecoder().decode(user.getHash());

        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();

            return Arrays.equals(hash, storedHash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing Algorithm: " + ALGORITHM, e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Invalid SecretKeyFactory", e);
        }
    }

    private void saveUsers() {
        Properties properties = new Properties();
        for (User user : users.values()) {
            properties.setProperty(user.getUsername(), user.toString());
        }
        try (OutputStream output = new FileOutputStream(USER_FILE)) {
            properties.store(output, null);
            System.out.println("Users saved to file successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while saving user data.");
            e.printStackTrace();
        }
    }

    private boolean loadUsers() {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream(USER_FILE)){
            properties.load(input);
            users = new HashMap<>();
            for (String key : properties.stringPropertyNames()) {
                String[] values = properties.getProperty(key).split(":");
                String salt = values[0];
                String hash = values[1];
                int level = values.length > 2 ? Integer.parseInt(values[2]) : 1; // Default to level 1 if not present
                users.put(key, new User(key, salt, hash, level));
            }
            System.out.println("Users loaded from file successfully.");
            return true;
        } catch (IOException e) {
            System.out.println("An error occurred while loading user data.");
            e.printStackTrace();
            return false;
        }
    }

    public void updateUserLevels(Game game) {
        for (var entry : game.getScores().entrySet()) {
            User user = users.get(entry.getKey().getKey());
            int score = entry.getValue();
            int levelIncrease = score / 10;
            user.increaseLevel(levelIncrease);
        }
        saveUsers(); // Save updated levels to file
    }

    public User getUser(String username) {
        return users.get(username);
    }


}

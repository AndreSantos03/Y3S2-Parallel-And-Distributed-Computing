import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Auth {

    private Map<String, String> users = new HashMap<>();
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

            users.put(username, saltString + ":" + hashString);
            saveUsers();

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Missing Algorithm: " + ALGORITHM, e);
        } catch (InvalidKeySpecException e) {
            throw new IllegalStateException("Invalid SecretKeyFactory", e);
        }
    }

    public boolean isUserRegistered(String username) {
        return users.containsKey(username);
    }

    public boolean authenticate(String username, String password) {
        String correctPass = users.get(username);
        if (correctPass == null) {
            System.out.println("The provided password was not found.");
            return false;
        }

        String[] parts = correctPass.split(":");
        byte[] salt = Base64.getDecoder().decode(parts[0]);
        byte[] storedHash = Base64.getDecoder().decode(parts[1]);

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
        for (Map.Entry<String, String> entry : users.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }
        try (OutputStream output = new FileOutputStream(USER_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            System.out.println("An error occurred while saving user data.");
            e.printStackTrace();
        }
    }

    private void loadUsers() {
        Properties properties = new Properties();
        File userFile = new File(USER_FILE);
        if (userFile.exists()) {
            try (InputStream input = new FileInputStream(USER_FILE)) {
                properties.load(input);
                for (String key : properties.stringPropertyNames()) {
                    users.put(key, properties.getProperty(key));
                }
                System.out.println("Users loaded from file successfully.");
            } catch (IOException e) {
                System.out.println("An error occurred while loading user data.");
                e.printStackTrace();
            }
        } else {
            System.out.println("User file not found. Starting with an empty user map.");
        }
    }
}

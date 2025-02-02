package org.example.myapp;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;

public class UserPersistence {

    private static final String USER_FILE = "user.json";

    public static void saveUser(String email) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            User user = new User(email);
            mapper.writeValue(new File(USER_FILE), user);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String loadUser() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileInputStream fileInputStream = new FileInputStream(USER_FILE);

            if (fileInputStream.available() > 0) {
                User user = mapper.readValue(fileInputStream, User.class);
                return user.getEmail();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean userExists() {
        return new File(USER_FILE).exists();
    }

    public static void clearUser() {
        File file = new File(USER_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}

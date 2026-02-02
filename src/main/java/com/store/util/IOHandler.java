package com.store.util;

import java.io.*;
import java.util.ArrayList;

public class IOHandler {

    // UNIVERSAL METHOD SAVING THE LIST (Generics <T>)
    public static <T> void saveList(String filename, ArrayList<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.err.println("Error saving to " + filename + ": " + e.getMessage());
        }
    }

    // UNIVERSAL METHOD LOADING LIST
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> loadList(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (ArrayList<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading from " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
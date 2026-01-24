package com.store.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IOHandler {

    // 1. Универсальное сохранение (Бинарное) - для Юзеров и Товаров
    public static <T> void save(String filename, List<T> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(list);
        } catch (IOException e) {
            System.err.println("Error saving " + filename + ": " + e.getMessage());
        }
    }

    // 2. Универсальная загрузка (Бинарная)
    @SuppressWarnings("unchecked")
    public static <T> List<T> load(String filename) {
        File file = new File(filename);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            // Мы принудительно превращаем (кастуем) объект в список типа T
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // 3. Сохранение чека в текстовый файл (для Кассира)
    public static void printBill(String content, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename + ".txt"))) {
            writer.println(content);
        } catch (IOException e) {
            System.err.println("Error printing bill: " + e.getMessage());
        }
    }
}
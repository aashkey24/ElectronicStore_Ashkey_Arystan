package com.store.util;

import java.io.*;

public class IOHandler {

    // Метод сохранения данных (users или products) в бинарный файл
    public static void save(String path, Object obj) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }

    // Метод загрузки данных. Возвращает null, если файл еще не создан
    public static Object load(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path))) {
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    // Метод для создания текстового чека (Bill)
    public static void printBill(String content, String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName + ".txt"))) {
            writer.println(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
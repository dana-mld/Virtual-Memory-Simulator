package org.example.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Disk {
    public Map<Integer, String> storage;
   int numPages;
    public Disk(int numPages) {
        storage = new HashMap<>();
        this.numPages = numPages;
        loadFromFile("src/main/resources/dataDisk.txt");
    }

    private void loadFromFile(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int pageNum = 0;
            while ((line = reader.readLine()) != null && pageNum<numPages) {
                storage.put(pageNum++, line.trim());
            }
        } catch (IOException e) {
            System.err.println(" Could not load disk data from " + filePath + ": " + e.getMessage());
        }
    }
    public String readPage(int pageNumber) {
        return storage.get(pageNumber);
    }
    public  void putPage(int pageNumber, String pageData) {
        storage.put(pageNumber, pageData);
    }


}

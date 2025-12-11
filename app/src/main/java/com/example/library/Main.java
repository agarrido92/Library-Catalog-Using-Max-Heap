package com.example.library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class MaxHeap {
    private int[] heapArray;
    private int heapSize;

    public MaxHeap() {
        heapArray = new int[2];
        heapSize = 0;
    }
   
    private void resizeArray() {
        int newLength = heapArray.length * 2;
        int[] newArray = new int[newLength];
        if (newArray != null) {
            for (int i = 0; i < heapArray.length; i++) {
                newArray[i] = heapArray[i];
            }
            heapArray = newArray;
        }
    }
   
    private void percolateUp(int nodeIndex) {
        while (nodeIndex > 0) {
            int parentIndex = (nodeIndex - 1) / 2;
                
            if (heapArray[nodeIndex] <= heapArray[parentIndex]) {
                return;
            }
            else {
                int temp = heapArray[nodeIndex];
                heapArray[nodeIndex] = heapArray[parentIndex];
                heapArray[parentIndex] = temp;
                
                nodeIndex = parentIndex;
            }
        }
    }
   
    private void percolateDown(int nodeIndex) {
        int childIndex = 2 * nodeIndex + 1;
        int value = heapArray[nodeIndex];

        while (childIndex < heapSize) {
            int maxValue = value;
            int maxIndex = -1;
            for (int i = 0; i < 2 && i + childIndex < heapSize; i++) {
                if (heapArray[i + childIndex] > maxValue) {
                    maxValue = heapArray[i + childIndex];
                    maxIndex = i + childIndex;
                }
            }

            if (maxValue == value) {
                return;
            }
            else {
                int temp = heapArray[nodeIndex];
                heapArray[nodeIndex] = heapArray[maxIndex];
                heapArray[maxIndex] = temp;
                
                nodeIndex = maxIndex;
                childIndex = 2 * nodeIndex + 1;
            }
        }
    }
   
    public void insert(int value) {
        if (heapSize == heapArray.length) {
            resizeArray();
        }
        
        heapArray[heapSize] = value;
        heapSize++;
        
        percolateUp(heapSize - 1);
    }
   
    public int remove() {
        int maxValue = heapArray[0];
    
        int replaceValue = heapArray[heapSize - 1];
        heapSize--;
        if (heapSize > 0) {
            heapArray[0] = replaceValue;

            percolateDown(0);
        }
                    
        return maxValue;
    }
   
    public String getHeapArrayString() {
        if (heapSize == 0) {
            return "[]";
        }
        
        String arrayString = String.format("[%d", heapArray[0]);
        for (int i = 1; i < heapSize; i++) {
            arrayString += (", " + heapArray[i]);
        }
        return arrayString + "]";
    }

    public int getMaxHeap(){
        return heapArray[0];
    }
   
    public int getHeapSize() {
        return heapSize;
    }
}

public class Main {

    private static Connection getConnection() throws SQLException {
        String host = System.getenv("DATABASE_HOST");
        String port = System.getenv("DATABASE_PORT");
        String db = System.getenv("DATABASE_NAME");
        String user = System.getenv("DATABASE_USER");
        String pass = System.getenv("DATABASE_PASSWORD");

        String url = String.format("jdbc:postgresql://%s:%s/%s", host, port, db);
        return DriverManager.getConnection(url, user, pass);
    }

    private static MaxHeap buildHeapFromDB(Connection conn) throws SQLException {
        MaxHeap heap = new MaxHeap();
        String sql = "SELECT search_count FROM catalog_100";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int count = rs.getInt("search_count");
                heap.insert(count);
            }
        }
        return heap;
    }

    private static boolean incrementSearchCount(Connection conn, int id) throws SQLException {
        String sql = "UPDATE catalog_100 SET search_count = search_count + 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }    

    public static void main(String[] args) {
        System.out.println("Starting Java app. Waiting for DB...");

        int attempts = 0;
        while (attempts < 20) {
            try (Connection conn = getConnection()) {
                System.out.println("Connected to DB.");

                MaxHeap heap = buildHeapFromDB(conn);
                System.out.println("Max heap built from db.");

                System.out.println("Current max search count: " + heap.getMaxHeap());

                Scanner scanner = new Scanner(System.in);
                while (true) {

                    System.out.println("Welcome to the Library Catalog. Please select from the options below.");
                    System.out.println("1) Book Search");
                    System.out.println("2) Most Searched");
                    System.out.println("0) Exit");

                    int val = scanner.nextInt();

                    if(val == 1){
                        System.out.println("Please enter the ID of the book to search: ");
                        int id = scanner.nextInt();
                        
                        boolean result = incrementSearchCount(conn, id);
                        
                        if(result){
                            System.out.println("Increment succeeded.");
                        }
                        else{
                            System.out.println("Increment failed.");
                        }

                    }
                    else if(val == 2){
                        System.out.println("Current max search count: " + heap.getMaxHeap());
                    }
                    else{
                        break;
                    }
                }
                scanner.close();
                break;
            } catch (SQLException e) {
                attempts++;
                System.out.println("DB not ready yet (attempt " + attempts + "). Waiting 2s...");
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
        System.out.println("App exiting.");
    }
}

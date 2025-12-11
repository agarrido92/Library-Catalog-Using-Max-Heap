package com.example.library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Book {
    int id;
    String title;
    String author;
    int year;
    int searchCount = 0;

    Book(int id, String title, String author, int year, int searchCount) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.searchCount = searchCount;
    }

    Book(int id, String title, String author, int year) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }

    @Override
    public String toString() {
        return "Book ID " + id + " — \"" + (title == null ? "Unknown" : title) + "\" by "
                + (author == null ? "Unknown" : author) + " (" + year + ") with " + searchCount + " searches.";
    }
}

class MaxHeap {
    private Book[] heapArray;
    private int heapSize;

    public MaxHeap() {
        heapArray = new Book[2];
        heapSize = 0;
    }
   
    private void resizeArray() {
        int newLength = heapArray.length * 2;
        Book[] newArray = new Book[newLength];
        if (newArray != null) {
            for (int i = 0; i < heapArray.length; i++) {
                newArray[i] = heapArray[i];
            }
            heapArray = newArray;
        }
    }
   
    private void percolateUp(int nodeIndex) {
        while (nodeIndex > 0) {
            int parentIdx = (nodeIndex - 1) / 2;
            if (heapArray[nodeIndex].searchCount > heapArray[parentIdx].searchCount) {
                Book temp = heapArray[nodeIndex];
                heapArray[nodeIndex] = heapArray[parentIdx];
                heapArray[parentIdx] = temp;
                nodeIndex = parentIdx;
            } else {
                break;
            }
        }
    }
   
    private void percolateDown(int nodeIndex) {
        while (true) {
            int left = nodeIndex * 2 + 1;
            int right = nodeIndex * 2 + 2;
            int largest = nodeIndex;

            if (left < heapSize && heapArray[left].searchCount > heapArray[largest].searchCount) {
                largest = left;
            }
            if (right < heapSize && heapArray[right].searchCount > heapArray[largest].searchCount) {
                largest = right;
            }

            if (largest != nodeIndex) {
                Book temp = heapArray[nodeIndex];
                heapArray[nodeIndex] = heapArray[largest];
                heapArray[largest] = temp;
                nodeIndex = largest;
            } else {
                break;
            }
        }
    }
   
    public void insert(Book value) {
        if (heapSize == heapArray.length) {
            resizeArray();
        }
        
        heapArray[heapSize] = value;
        heapSize++;
        
        percolateUp(heapSize - 1);
    }
   
    public Book remove() {
        if (heapSize == 0) return null;

        Book root = heapArray[0];
        heapArray[0] = heapArray[heapSize - 1];
        heapArray[heapSize - 1] = null;
        heapSize--;

        percolateDown(0);
        return root;
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

    public Book getMaxHeap(){
        if(heapSize == 0){
            return null;
        }
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

        String sql = "SELECT id, title, author, year, search_count FROM catalog_100";

        try (PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                int year = rs.getInt("year");
                int searchCount = rs.getInt("search_count");

                Book b = new Book(id, title, author, year, searchCount);
                heap.insert(b);
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
    
    private static Book getBookById(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, title, author, year, search_count FROM catalog_100 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getInt("search_count")
                    );
                }
            }
        }
        return null;
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

                            Book searched = getBookById(conn, id);

                            if (searched != null) {
                                System.out.println("You searched for:");
                                System.out.println(searched);
                            } else {
                                System.out.println("Search count updated, but book not found.");
                            }

                            heap = buildHeapFromDB(conn);
                        }
                        else{
                            System.out.println("Increment failed.");
                        }

                    }
                    else if(val == 2){
                        Book top = heap.getMaxHeap();
                        if (top == null) {
                            System.out.println("Heap is empty (no books).");
                        } else {
                            System.out.println("Most searched book:");
                            System.out.println(top);
                        }
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

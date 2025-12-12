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

        long startTime = System.nanoTime();

        if (heapSize == heapArray.length) {
            resizeArray();
        }
        
        heapArray[heapSize] = value;
        heapSize++;
        
        percolateUp(heapSize - 1);

        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        long durationInMillis = durationInNano / 1000000;

        System.out.println("Execution time of inserting into heap (in milliseconds): " + durationInMillis);
    }
   
    public Book remove() {
        long startTime = System.nanoTime();

        if (heapSize == 0) {
            return null;
        }

        Book root = heapArray[0];

        heapArray[0] = heapArray[heapSize - 1];
        heapArray[heapSize - 1] = null;
        heapSize--;

        if (heapSize > 0) {
            percolateDown(0);
        }

        long endTime = System.nanoTime();

        long durationInNano = endTime - startTime;
        long durationInMillis = durationInNano / 1000000;

        System.out.println("Execution time of removing from heap (in milliseconds): " + durationInMillis);

        return root;
    }

    public boolean deleteById(int id) {
        int index = -1;
        for (int i = 0; i < heapSize; i++) {
            if (heapArray[i].id == id) {
                index = i;
                break;
            }
        }

        if (index == -1) {
            return false; 
        }

        heapArray[index] = heapArray[heapSize - 1];
        heapArray[heapSize - 1] = null;
        heapSize--;

        if (heapSize > 0 && index < heapSize) {
            percolateDown(index);
            percolateUp(index);
        }

        return true;
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

    private static String activeTable = "catalog_100"; 

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
        long startTime =  System.nanoTime();

        String sql = "SELECT id, title, author, year, search_count FROM " + activeTable;

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
        long endTime =  System.nanoTime();
        
        long durationInNano = endTime - startTime;
        long durationInMillis = durationInNano / 1000000;

        System.out.println("Execution time of creating the heap (in milliseconds): " + durationInMillis);

        return heap;
    }

    private static boolean incrementSearchCount(Connection conn, int id) throws SQLException {
        String sql = "UPDATE " + activeTable + " SET search_count = search_count + 1 WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int updated = ps.executeUpdate();
            return updated > 0;
        }
    }
    
    private static Book getBookById(Connection conn, int id) throws SQLException {
        String sql = "SELECT id, title, author, year, search_count FROM " + activeTable + " WHERE id = ?";
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

    private static Book addBookToCatalog(Connection conn, MaxHeap heap, String title, String author, int year) throws SQLException {
        String sql = "INSERT INTO " + activeTable +
                    " (title, author, year, search_count) VALUES (?, ?, ?, 0) RETURNING id";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int searchCount = 0; 

                    Book newBook = new Book(id, title, author, year, searchCount);
                    heap.insert(newBook);
                    return newBook;
                } else {
                    throw new SQLException("INSERT did not return an id");
                }
            }
        }
    }

    private static boolean deleteBookFromCatalog(Connection conn, int id) throws SQLException {
        String sql = "DELETE FROM " + activeTable + " WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int deleted = ps.executeUpdate();
            return deleted > 0;
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
                    System.out.println("2) Add a Book");
                    System.out.println("3) Remove a Book");
                    System.out.println("4) Most Searched");
                    System.out.println("5) Change Dataset");
                    System.out.println("0) Exit");
                    System.out.print("Choice: ");

                    int val = scanner.nextInt();

                    if(val == 1){
                        System.out.print("Please enter the ID of the book to search: ");
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
                        try {
                            scanner.nextLine();
                            System.out.print("Enter title: ");
                            String title = scanner.nextLine().trim();

                            System.out.print("Enter author: ");
                            String author = scanner.nextLine().trim();

                            System.out.print("Enter year (e.g. 1999): ");
                            String yearStr = scanner.nextLine().trim();
                            int year;
                            try {
                                year = Integer.parseInt(yearStr);
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid year, must be a number.");
                                break;
                            }

                            Book newBook = addBookToCatalog(conn, heap, title, author, year);

                            System.out.println("New book added:");
                            System.out.println(newBook);

                        } catch (SQLException e) {
                            System.out.println("Error adding book: " + e.getMessage());
                        }
                    }
                    else if(val == 3){
                        scanner.nextLine();
                        System.out.print("Enter book id to remove: ");
                        String idStr = scanner.nextLine().trim();
                        int id;
                        try {
                            id = Integer.parseInt(idStr);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid id, must be a number.");
                            continue;
                        }

                        try {
                            boolean deletedFromDb = deleteBookFromCatalog(conn, id);
                            if (!deletedFromDb) {
                                System.out.println("No book found in " + activeTable + " with id " + id);
                                continue;
                            }

                            boolean deletedFromHeap = heap.deleteById(id);
                            if (!deletedFromHeap) {
                                System.out.println("Book was removed from DB but not found in heap (possible heap out of sync).");
                            } else {
                                System.out.println("Book " + id + " removed from DB and heap.");
                            }

                        } catch (SQLException e) {
                            System.out.println("Error removing book: " + e.getMessage());
                        }
                    }
                    else if(val == 4){
                        Book top = heap.getMaxHeap();
                        if (top == null) {
                            System.out.println("Heap is empty (no books).");
                        } else {
                            System.out.println("Most searched book:");
                            System.out.println(top);
                        }
                    }
                    else if(val == 5){
                        System.out.println("Please enter your choice: ");
                        System.out.println("1) 100 dataset");
                        System.out.println("2) 1,000 dataset");
                        System.out.println("3) 10,000 dataset");
                        int id = scanner.nextInt();
                        if(id == 1){
                            activeTable = "catalog_100";
                        }
                        else if(id == 2){
                            activeTable = "catalog_1000";
                        }
                        else if(id == 3){
                            activeTable = "catalog_10000";
                        }
                        else{
                            System.out.println("Option does not exist.");
                        }

                        System.out.println("Current dataset is " + activeTable);
                        heap = buildHeapFromDB(conn);
                    }
                    else{
                        System.out.println("Goodbye!");
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

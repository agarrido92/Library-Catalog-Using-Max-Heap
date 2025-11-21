package com.example.library;

import java.sql.*;
import java.util.Scanner;

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

    public static void recordSearch(Connection conn, int bookId) throws SQLException {
        String sql = "UPDATE books SET search_count = search_count + 1 WHERE id = ? RETURNING search_count";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Book " + bookId + " new search_count = " + rs.getInt(1));
            } else {
                System.out.println("Book id " + bookId + " not found.");
            }
        }
    }

    public static void printTopK(Connection conn, int k) throws SQLException {
        String sql = "SELECT id, title, author, search_count FROM books ORDER BY search_count DESC NULLS LAST LIMIT ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, k);
            ResultSet rs = ps.executeQuery();
            System.out.println("Top " + k + " books:");
            while (rs.next()) {
                System.out.printf("id=%d title=%s author=%s searches=%d%n",
                                  rs.getInt("id"),
                                  rs.getString("title"),
                                  rs.getString("author"),
                                  rs.getInt("search_count"));
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Starting Java app. Waiting for DB...");

        int attempts = 0;
        while (attempts < 20) {
            try (Connection conn = getConnection()) {
                System.out.println("Connected to DB.");

                Scanner sc = new Scanner(System.in);
                while (true) {
                    System.out.println("\nEnter command: 1 <bookId>=recordSearch, 2 <k>=topK, 0=exit");
                    String line = sc.nextLine().trim();
                    if (line.equals("0")) break;
                    if (line.startsWith("1")) {
                        String[] parts = line.split("\\s+");
                        if (parts.length < 2) {
                            System.out.println("Usage: 1 <bookId>");
                            continue;
                        }
                        int id = Integer.parseInt(parts[1]);
                        recordSearch(conn, id);
                    } else if (line.startsWith("2")) {
                        String[] parts = line.split("\\s+");
                        int k = 10;
                        if (parts.length >= 2) k = Integer.parseInt(parts[1]);
                        printTopK(conn, k);
                    } else {
                        System.out.println("Unknown command");
                    }
                }
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

package librarycatalog;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        LibraryBST catalog = new LibraryBST();
        MaxHeap heap = new MaxHeap();

        while (true) {
            System.out.print("Enter book title (or type 'stop' to finish): ");
            String title = scanner.nextLine();

            if (title.equalsIgnoreCase("stop"))
                break;

            System.out.print("Enter author: ");
            String author = scanner.nextLine();

            System.out.print("Enter publication year: ");
            int year = Integer.parseInt(scanner.nextLine());

            Book book = new Book(title, author, year);
            catalog.insert(book);
            heap.addBook(book);

            System.out.println("Book added!\n");
        }

        System.out.println("\n=== Library Catalog (BST, sorted by title) ===");
        catalog.printCatalog();

        System.out.println("\n=== Books sorted by year (Max-Heap, newest first) ===");
        heap.printYear();
    }
}

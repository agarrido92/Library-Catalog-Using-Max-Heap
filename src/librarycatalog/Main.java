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

            Book book = new Book(title, author);
            catalog.insert(book);
            heap.addBook(book);

            System.out.println("Book added!\n");
        }

        System.out.println("\n=== Library Catalog (BST, sorted by title) ===");
        catalog.printCatalog();
        System.out.println("\nMost recently added book:");
        System.out.println(heap.getNewestBook());

    }
}

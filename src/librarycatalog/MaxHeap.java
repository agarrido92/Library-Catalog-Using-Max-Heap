package librarycatalog;
import java.util.PriorityQueue;
import java.util.Comparator;

public class MaxHeap {

	private PriorityQueue<Book> heap;
	
	public MaxHeap() {
		heap = new PriorityQueue<>(Comparator.comparingInt(Book::getYear).reversed());
	}
	
	public void addBook(Book book) {
		heap.add(book);
	}
	
	public Book getNewestBook() {
		return heap.peek();
	}
	
	public void printYear() {
		PriorityQueue<Book> copy = new PriorityQueue<>(heap);
		while (!copy.isEmpty()) {
			System.out.println(copy.poll());
		}
	}
}

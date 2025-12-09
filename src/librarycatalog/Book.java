package librarycatalog;

public class Book implements Comparable<Book> {
    private String title;
    private String author;
    private long added;

    public Book(String title, String author) {
        this.title = title;
        this.author = author;
        this.added = System.currentTimeMillis();
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public long getAdded() {return added;}
   

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }

    @Override
    public String toString() {
        return title + " by " + author;
    }
}


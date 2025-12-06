package librarycatalog;

public class LibraryBST {

    private class Node {
        Book book;
        Node left, right;

        Node(Book book) {
            this.book = book;
        }
    }

    private Node root;

    public void insert(Book book) {
        root = insertRec(root, book);
    }

    private Node insertRec(Node root, Book book) {
        if (root == null) return new Node(book);

        if (book.compareTo(root.book) < 0)
            root.left = insertRec(root.left, book);
        else
            root.right = insertRec(root.right, book);

        return root;
    }

    public Book search(String title) {
        return searchRec(root, title);
    }

    private Book searchRec(Node root, String title) {
        if (root == null) return null;

        int cmp = title.compareToIgnoreCase(root.book.getTitle());

        if (cmp == 0)
            return root.book;
        else if (cmp < 0)
            return searchRec(root.left, title);
        else
            return searchRec(root.right, title);
    }

    public void printCatalog() {
        inorder(root);
    }

    private void inorder(Node root) {
        if (root != null) {
            inorder(root.left);
            System.out.println(root.book);
            inorder(root.right);
        }
    }
}

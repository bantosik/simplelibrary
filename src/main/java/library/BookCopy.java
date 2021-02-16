package library;

import java.util.Optional;

/**
 * Holds information about actual physical book copy that can be lend by the users of the library.
 */
public class BookCopy {
    private final Book book;
    private final long id;
    private String lendingUser;

    public BookCopy(Book book, long id) {
        this.book = book;
        this.id = id;
    }

    public BookCopy(Book book, long id, String lendingUser) {
        this.book = book;
        this.id = id;
        this.lendingUser = lendingUser;
    }

    public Book getBook() {
        return book;
    }

    public long getId() {
        return id;
    }

    public boolean isLent() {
        return lendingUser != null;
    }

    public Optional<String> getLendingUser() {
        return Optional.ofNullable(lendingUser);
    }
}

/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package library;

import com.sun.source.tree.LambdaExpressionTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Library {
    private Map<Long, BookCopy> bookCopies = new HashMap<>();
    private long nextId = 0;

    public long addBook(String title, int year, String author) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(author);

        long newBookCopyId = nextId++;
        Book newBook = new Book(title, year, author);
        BookCopy newBookCopy = new BookCopy(newBook, newBookCopyId);
        bookCopies.put(newBookCopyId, newBookCopy);
        return newBookCopyId;
    }

    public void removeBookById(long id) {
        var bookCopy = bookCopies.get(id);

        if (bookCopy == null) {
            throw new BookNotFound("Cannot remove book that does not exist");
        } else if (bookCopy.isLent()) {
            throw new BookLent("Cannot remove book that is already lent");
        }

        bookCopies.remove(id);
    }

    public Map<Book, BookAvailability> listBookAvailability() {
        Map<Book, BookAvailability> result = new HashMap<>();
        for (BookCopy bookCopy : bookCopies.values()) {
            result.compute(bookCopy.getBook(), (k, v) -> {
                if (v == null) {
                    return bookCopy.isLent() ?
                            new BookAvailability(0, 1)
                            : new BookAvailability(1, 0);
                } else {
                    return bookCopy.isLent() ?
                            new BookAvailability(v.getNumAvailable(), v.getNumLent() + 1)
                            : new BookAvailability(v.getNumAvailable() + 1, v.getNumLent());
                }
            });
        }
        return result;
    }

    public void lendForUser(long id, String userName) {
        Objects.requireNonNull(userName);

        var bookCopy = bookCopies.get(id);
        if (bookCopy == null) {
            throw new BookNotFound("Cannot lend book that does not exist");
        } else if (bookCopy.isLent()) {
            throw new BookLent("Cannot lend book already lent");
        }
        bookCopies.put(id, new BookCopy(bookCopy.getBook(), id, userName));
    }

    public BookCopy getBookCopyInfo(Long id) {
        var bookCopy = bookCopies.get(id);
        if (bookCopy == null) {
            throw new BookNotFound("Cannot display book that does not exist");
        }
        return bookCopy;
    }

    public List<BookCopy> search(Predicate<? super Book> predicate) {
        return bookCopies.values().stream()
                .filter(bookCopy -> predicate.test(bookCopy.getBook()))
                .collect(Collectors.toList());
    }
}

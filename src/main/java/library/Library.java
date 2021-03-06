package library;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Main class of the solution. Contains methods covering library functionalities
 */
public class Library {
    private final Map<Long, BookCopy> bookCopies = new HashMap<>();
    private long nextId = 0;

    /** Add new copy of the book to the library, will assign unique id and return it back
     * @param title title of the book
     * @param year year of publication
     * @param author author of the book
     * @return unique id users of the class can use to refer to the new copy
     */
    public long addBook(String title, int year, String author) {
        Objects.requireNonNull(title);
        Objects.requireNonNull(author);

        long newBookCopyId = nextId++;
        Book newBook = new Book(title, year, author);
        BookCopy newBookCopy = new BookCopy(newBook, newBookCopyId);
        bookCopies.put(newBookCopyId, newBookCopy);
        return newBookCopyId;
    }

    /** Removes book copy by its id.
     * Throws if there is no book referring to the supplied id or the book copy is already lent.
     * @param id id of the book to remove
     */
    public void removeBookById(long id) {
        var bookCopy = bookCopies.get(id);

        if (bookCopy == null) {
            throw new BookNotFound("Cannot remove book that does not exist");
        } else if (bookCopy.isLent()) {
            throw new BookLent("Cannot remove book that is already lent");
        }

        bookCopies.remove(id);
    }

    /** list books with their availabilities
     * @return map from the book to the number of available/lent copies
     */
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

    /** Marks book copy as lent by the user.
     * Throws if book of given id does not exist or is empty
     * @param id id of the book copy to lend
     * @param userName name of the user lending the book
     */
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

    /** returns detailed information about the book copy.
     * Throws if book of given id does not exist
     * @param id id of the book copy
     * @return book copy information
     */
    public BookCopy getBookCopyInfo(Long id) {
        var bookCopy = bookCopies.get(id);
        if (bookCopy == null) {
            throw new BookNotFound("Cannot display book that does not exist");
        }
        return bookCopy;
    }

    /** search through the books and return copies adhering to the predicate supplied
     * @param predicate boolean predicate taking book basic information
     * @return list of book copies for which predicate is true
     */
    public List<BookCopy> search(Predicate<? super Book> predicate) {
        return bookCopies.values().stream()
                .filter(bookCopy -> predicate.test(bookCopy.getBook()))
                .collect(Collectors.toList());
    }
}

package library;

/**
 * Describes availability of the book in the library
 */
public class BookAvailability {
    private final long numAvailable;
    private final long numLent;

    public BookAvailability(long numAvailable, long numLent) {
        this.numAvailable = numAvailable;
        this.numLent = numLent;
    }

    public long getNumAvailable() {
        return numAvailable;
    }

    public long getNumLent() {
        return numLent;
    }
}

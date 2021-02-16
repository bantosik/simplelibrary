package library

import spock.lang.Specification

class LibraryTest extends Specification {
    def "it should be possible to add and remove book from library"() {
        setup:
        def lib = new Library()

        when:
        def bookCopyId = lib.addBook("Some title", 1986, "Some author")
        lib.removeBookById(bookCopyId)

        then:
        noExceptionThrown()
    }

    def "removing book from empty library should throw BookNotFound exception"() {
        setup:
        def lib = new Library()

        when:
        lib.removeBookById(8L)

        then:
        thrown(BookNotFound)
    }

    def "removing already lent book should throw BookLent exception"() {
        setup:
        def lib = new Library()
        def b1 = lib.addBook("Some title", 1986, "Some author")
        lib.lendForUser(b1, "Some user")

        when:
        lib.removeBookById(b1)

        then:
        thrown(BookLent)
    }

    def "library search method should return single element list if only one applies to search predicate"() {
        setup:
        def lib = new Library()
        def b1 = lib.addBook("Some title", 1986, "Some author")
        def b2 = lib.addBook("Some title 1", 1986, "Some author")

        when:
        def list = lib.search({ b -> b.getTitle().equals("Some title") })

        then:
        list.size() == 1
        list.get(0).getId() == b1
    }

    def "lending book from empty library should throw BookNotFound exception"() {
        setup:
        def lib = new Library()

        when:
        lib.lendForUser(8L, "Some user")

        then:
        thrown(BookNotFound)
    }

    def "lending book which is already lent should throw BookLent exception"() {
        setup:
        def lib = new Library()
        def b1 = lib.addBook("Some title", 1986, "Some author")
        lib.lendForUser(b1, "Some user")

        when:
        lib.lendForUser(b1, "Other user")

        then:
        thrown(BookLent)
    }

    def "get book copy info should throw BookNotFound on empty library"() {
        setup:
        def lib = new Library()

        when:
        def bookCopyInfo = lib.getBookCopyInfo(9L)

        then:
        thrown(BookNotFound)
    }

    def "get book copy info should return book info with lending user for given id if the book is in library and is lent"() {
        setup:
        def lib = new Library()
        def b1 = lib.addBook("Some title", 1986, "Some author")
        lib.lendForUser(b1, "Some user")

        when:
        def bookCopyInfo = lib.getBookCopyInfo(b1)

        then:
        bookCopyInfo.getBook() == new Book("Some title", 1986, "Some author")
        bookCopyInfo.isLent()
        bookCopyInfo.getLendingUser().get() == "Some user"
    }

    def "get book copy info should return book info without lending user for given id if the book is in library and is not lent"() {
        setup:
        def lib = new Library()
        def b1 = lib.addBook("Some title", 1986, "Some author")

        when:
        def bookCopyInfo = lib.getBookCopyInfo(b1)

        then:
        bookCopyInfo.getBook() == new Book("Some title", 1986, "Some author")
        !bookCopyInfo.isLent()
        bookCopyInfo.getLendingUser().isEmpty()
    }

    def "listing books should return them distinctly, displaying number of copies available and lent"() {
        setup:
        def lib = new Library()
        def b1 = lib.addBook("Some title", 1986, "Some author")
        def b2 = lib.addBook("Some title", 1986, "Some author")
        def b3 = lib.addBook("Some title 2", 1986, "Some author")
        def b4 = lib.addBook("Some title 2", 1986, "Some author")
        def b5 = lib.addBook("Some title 2", 1986, "Some author")
        lib.lendForUser(b5, "Some user")
        lib.lendForUser(b4, "Some user 1")

        when:
        def bookInfo = lib.listBookAvailability()

        then:
        bookInfo.size() == 2
        def availability1 = bookInfo.get(new Book("Some title", 1986, "Some author"))
        def availability2 = bookInfo.get(new Book("Some title 2", 1986, "Some author"))
        availability1.numLent == 0
        availability1.numAvailable == 2

        availability2.numLent == 2
        availability2.numAvailable == 1
    }
}

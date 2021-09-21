package com.arcticsoft.tomcat;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class BookRepository
{
	private final Map<String, Book> storedBooks = new ConcurrentHashMap<>();

	public BookRepository() {
		storedBooks.put("ISBN-1", new Book("ISBN-1", "Book 1", "Book 1 Author"));
		storedBooks.put("ISBN-2", new Book("ISBN-2", "Book 2", "Book 2 Author"));
		storedBooks.put("ISBN-3", new Book("ISBN-3", "Book 3", "Book 3 Author"));
		storedBooks.put("ISBN-4", new Book("ISBN-4", "Book 4", "Book 4 Author"));
	}

	public Collection<Book> findAll() {
		return storedBooks.values();
	}

	public Optional<Book> findById(String isbn) {
		return Optional.ofNullable(storedBooks.get(isbn));
	}

	public Book add(Book book) {
		storedBooks.put(book.getIsbn(), book);
		return book;
	}
}

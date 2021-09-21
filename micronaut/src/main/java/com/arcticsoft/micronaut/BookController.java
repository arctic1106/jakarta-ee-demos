package com.arcticsoft.micronaut;

import java.net.URI;
import java.util.List;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;

@Controller("/books")
public class BookController
{
	private final BookRepository bookRepo;

	public BookController(BookRepository bookRepo) {
		this.bookRepo = bookRepo;
	}

	@Get
	public List<Book> list() {
		return bookRepo.findAll();
	}

	@Get("/{isbn}")
	public HttpResponse<Book> get(@PathVariable String isbn) {
		Book book = bookRepo.findByIsbn(isbn);
		if (book == null) {
			return HttpResponse.notFound();
		}
		return HttpResponse.ok(book);
	}

	@Post
	public HttpResponse<?> create(@Body Book book) {
		if (book == null || book.getIsbn() == null || book.getIsbn().isBlank()
				|| book.getName() == null || book.getAuthor() == null) {
			return HttpResponse.badRequest("isbn, name and author are required");
		}
		if (bookRepo.exists(book.getIsbn())) {
			return HttpResponse.status(HttpStatus.CONFLICT);
		}
		Book saved = bookRepo.save(book);
		return HttpResponse.created(saved, URI.create("/books/" + saved.getIsbn()));
	}
}

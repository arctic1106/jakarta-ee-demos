package com.arcticsoft.micronaut;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "book")
public class Book
{
	@Id
	@Column(nullable = false, unique = true)
	private String isbn;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String author;

	public Book() {
	}

	public Book(String isbn, String name, String author) {
		this.isbn = isbn;
		this.name = name;
		this.author = author;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}
}

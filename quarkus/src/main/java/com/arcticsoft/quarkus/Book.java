package com.arcticsoft.quarkus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
@Table(name = "book")
public class Book extends PanacheEntityBase
{
	@Id
	@Column(nullable = false, unique = true)
	public String isbn;

	@Column(nullable = false)
	public String name;

	@Column(nullable = false)
	public String author;

	public Book() {
	}

	public Book(String isbn, String name, String author) {
		this.isbn = isbn;
		this.name = name;
		this.author = author;
	}

	@Override
	public String toString() {
		return "Book: isbn=" + isbn + ", name=" + name + ", author=" + author;
	}
}

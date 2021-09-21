package com.arcticsoft.micronaut;

import java.util.List;

import jakarta.inject.Singleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Singleton
public class BookRepository
{
	@PersistenceContext
	EntityManager entityManager;

	@Transactional
	public List<Book> findAll() {
		return entityManager
				.createQuery("select b from Book b order by b.isbn", Book.class)
				.getResultList();
	}

	@Transactional
	public Book findByIsbn(String isbn) {
		return entityManager.find(Book.class, isbn);
	}

	@Transactional
	public Book save(Book book) {
		entityManager.persist(book);
		return book;
	}

	@Transactional
	public boolean exists(String isbn) {
		return entityManager.find(Book.class, isbn) != null;
	}
}

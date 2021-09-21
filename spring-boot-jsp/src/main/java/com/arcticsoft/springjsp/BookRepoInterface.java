package com.arcticsoft.springjsp;

import java.util.Collection;
import java.util.Optional;

public interface BookRepoInterface
{
	Collection<Book> findAll();
	Optional<Book> findById(String isbn);
	Book add(Book book);
}

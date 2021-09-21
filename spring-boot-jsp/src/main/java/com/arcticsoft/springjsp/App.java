package com.arcticsoft.springjsp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.servlet.context.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

@ServletComponentScan
@SpringBootApplication
public class App extends SpringBootServletInitializer
{
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Bean
	public BookRepoInterface initBookRepo()
	{
		Map<String, Book> initData = new HashMap<>();
		initData.put("ISBN-1", new Book("ISBN-1", "Book 1", "Book 1 Author"));
		initData.put("ISBN-2", new Book("ISBN-2", "Book 2", "Book 2 Author"));
		initData.put("ISBN-3", new Book("ISBN-3", "Book 3", "Book 3 Author"));
		initData.put("ISBN-4", new Book("ISBN-4", "Book 4", "Book 4 Author"));

		return new BookRepository(initData);
	}
}

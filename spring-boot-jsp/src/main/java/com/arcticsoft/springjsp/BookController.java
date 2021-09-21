package com.arcticsoft.springjsp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("")
public class BookController
{
	private final BookRepoInterface bookRepo;

	public BookController(BookRepoInterface bookRepo) {
		this.bookRepo = bookRepo;
	}

	@GetMapping("/viewBooks")
	public String viewBooks(Model model)
	{
		model.addAttribute("books", bookRepo.findAll());
		return "view-books";
	}

	@GetMapping("/enterAddBook")
	public String addBookView(Model model)
	{
		model.addAttribute("book", new Book());
		return "add-book";
	}

	@PostMapping("/addBook")
	public RedirectView addBook(@ModelAttribute("book") Book book, RedirectAttributes redirectAttributes)
	{
		bookRepo.add(book);

		return new RedirectView("/viewBooks", true);
	}
}
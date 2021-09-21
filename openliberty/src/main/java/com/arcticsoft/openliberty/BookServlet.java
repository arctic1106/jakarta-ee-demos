package com.arcticsoft.openliberty;

import java.io.IOException;

import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns = {"/viewBooks", "/enterAddBook", "/addBook"})
public class BookServlet extends HttpServlet
{
	@Inject
	private BookRepository bookRepo;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException
	{
		switch (req.getServletPath()) {
			case "/viewBooks" -> {
				req.setAttribute("books", bookRepo.findAll());
				forward(req, resp, "view-books");
			}
			case "/enterAddBook" -> forward(req, resp, "add-book");
			default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		if (!"/addBook".equals(req.getServletPath())) {
			resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		Book book = new Book(
				req.getParameter("isbn"),
				req.getParameter("name"),
				req.getParameter("author"));
		bookRepo.add(book);

		resp.sendRedirect(req.getContextPath() + "/viewBooks");
	}

	private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
			throws ServletException, IOException
	{
		req.getRequestDispatcher("/WEB-INF/jsp/" + view + ".jsp").forward(req, resp);
	}
}

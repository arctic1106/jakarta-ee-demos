package com.arcticsoft.quarkus;

import java.util.List;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource
{
	@GET
	public List<Book> list() {
		return Book.listAll();
	}

	@GET
	@Path("/{isbn}")
	public Response get(@PathParam("isbn") String isbn) {
		Book book = Book.findById(isbn);
		if (book == null) {
			return Response.status(Response.Status.NOT_FOUND).build();
		}
		return Response.ok(book).build();
	}

	@POST
	@Transactional
	public Response create(@Valid Book book) {
		if (Book.findById(book.isbn) != null) {
			return Response.status(Response.Status.CONFLICT)
					.entity("ISBN already exists: " + book.isbn)
					.build();
		}
		Book.persist(book);
		return Response.status(Response.Status.CREATED).entity(book).build();
	}
}

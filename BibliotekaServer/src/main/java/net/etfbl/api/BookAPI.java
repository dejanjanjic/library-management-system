package net.etfbl.api;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.etfbl.model.Book;
import net.etfbl.model.User;
import net.etfbl.service.BookService;

@Path("books")
public class BookAPI {
	private BookService bookService;
	
	public BookAPI() {
		this.bookService = new BookService();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBooks() {
		ArrayList<Book> books = bookService.getBooks();
		return Response.status(200).entity(books).build();
	}
	
	@GET
	@Path("{title}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getBookByTitle(@PathParam("title")String title) {
		String decodedTitle = title.replace("+", " ");
		//System.out.println("Title: " + decodedTitle);
		Book book = bookService.getBookByTitle(decodedTitle);
		if(book != null) {
			return Response.status(200).entity(book).build();
		}else {
			return Response.status(404).entity(book).build();
		}
		
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response postBook(Book book) {
		System.out.println(book.getPicturePath());
		if(bookService.addBook(book)) {
			return Response.status(200).entity(book).build();
		}
		return Response.status(404).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(Book book) {
		if(bookService.update(book)) {
			return Response.status(200).entity(book).build();
		}
		return Response.status(404).build();
	}

	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(Book book) {
		if(bookService.delete(book)) {
			return Response.status(204).build();
		}
		return Response.status(404).build();
	}
	
	@DELETE
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteById(@PathParam("id")int id) {
		if(bookService.deleteById(id)) {
			return Response.status(204).build();
		}
		return Response.status(404).build();
	}
}

package net.etfbl.api;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.etfbl.model.Book;
import net.etfbl.service.MailService;

@Path("mail")
public class MailAPI {
	private MailService mailService;
	
	public MailAPI() {
		this.mailService = new MailService();
	}
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMail(@QueryParam("mail")String mail, ArrayList<Book> selectedBooks) {
		System.out.println(mail);
		if(mailService.sendMail(mail, selectedBooks)) {
			return Response.status(200).build();
		}else {
			return Response.status(404).build();
		}
	}
}

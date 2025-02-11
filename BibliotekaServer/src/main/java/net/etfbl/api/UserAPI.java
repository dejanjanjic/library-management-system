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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import net.etfbl.model.User;
import net.etfbl.service.UserService;

@Path("users")
public class UserAPI {
	private UserService userService;
	
	public UserAPI() {
		this.userService = new UserService();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsers() {
		ArrayList<User> users = userService.getUsers();
		return Response.status(200).entity(users).build();
	}
	
	@GET
	@Path("{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserByUsername(@PathParam("username")String username) {
		User user = userService.getUserByUsername(username);
		if(user != null) {
			return Response.status(200).entity(user).build();
		}else {
			return Response.status(404).entity(user).build();
		}
		
	}
	
	
	@GET
	@Path("requests")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRequests() {
		ArrayList<User> users = userService.getRequests();
		return Response.status(200).entity(users).build();
	}
	
	@POST
	@Path("register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		if(userService.register(user)) {
			return Response.status(200).entity(user).build();
		}
		return Response.status(404).build();
	}
	
	@POST
	@Path("login")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		User temp = userService.login(user);
		if(temp != null) {
			return Response.status(200).entity(temp).build();
		}
		return Response.status(404).build();
	}
	
	@PUT
	@Path("accept")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response acceptRequest(User user) {
		if(userService.acceptRequest(user)) {
			return Response.status(200).entity(user).build();
		}
		return Response.status(404).build();
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response update(User user) {
		if(userService.update(user)) {
			return Response.status(200).entity(user).build();
		}
		return Response.status(404).build();
	}

	
	@DELETE
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response delete(User user) {
		if(userService.delete(user)) {
			return Response.status(204).build();
		}
		return Response.status(404).build();
	}
	
	@DELETE
	@Path("{username}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteByUsername(@PathParam("username")String username) {
		if(userService.deleteByUsername(username)) {
			return Response.status(204).build();
		}
		return Response.status(404).build();
	}


}

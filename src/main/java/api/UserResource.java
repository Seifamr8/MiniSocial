package api;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.User;
import services.UserService;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(User user) {
        return userService.registerUser(user);
    }

    @POST
    @Path("/login")
    public Response login(User credentials) {
        User user = userService.loginUser(credentials.getEmail(), credentials.getPassword());
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid email or password.\"}").build();
        }
        // Simulate JWT generation
        return Response.ok("{\"message\": \"Login successful.\", \"token\": \"JWT-TOKEN\"}").build();
    }

    @PUT
    @Path("/users/{id}/update")
    public Response updateUser(@PathParam("id") Long userId, User updatedUser) {
        return userService.updateUser(userId, updatedUser);
    }
}

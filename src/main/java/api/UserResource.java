package api;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import DTO.UserDTO;
import models.User;
import services.UserService;
import util.DTOmapper;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {

    @EJB
    private UserService userService;

    @POST
    @Path("/register")
    public Response register(UserDTO userDTO) {
        // Convert DTO to entity
        User user = new User();
        user.setEmail(userDTO.getEmail());
        // Note: In a real app, you'd get the password from a separate field
        // not included in UserDTO for security
        user.setPassword("password"); // This should come from request
        user.setName(userDTO.getName());
        user.setBio(userDTO.getBio());
        user.setRole(userDTO.getRole());
        
        return userService.registerUser(user);
    }

    @POST
    @Path("/login")
    public Response login(UserDTO credentials) {
        User user = userService.loginUser(credentials.getEmail(), "password"); // Password should come from request
        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Invalid email or password.\"}").build();
        }
        // Simulate JWT generation
        return Response.ok("{\"message\": \"Login successful.\", \"token\": \"JWT-TOKEN\"}").build();
    }

    @PUT
    @Path("/{id}/update")
    public Response updateUser(@PathParam("id") Long userId, UserDTO userDTO) {
        // Convert DTO to entity
        User updatedUser = new User();
        updatedUser.setEmail(userDTO.getEmail());
        updatedUser.setName(userDTO.getName());
        updatedUser.setBio(userDTO.getBio());
        updatedUser.setRole(userDTO.getRole());
        // Password would be handled separately for security
        
        return userService.updateUser(userId, updatedUser);
    }
    
    @GET
    @Path("/{id}")
    public Response getUser(@PathParam("id") Long userId) {
        User user = userService.getUser(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        UserDTO userDTO = DTOmapper.toUserDTO(user);
        return Response.ok(userDTO).build();
    }
}
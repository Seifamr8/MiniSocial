package api;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
//import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
//import javax.ws.rs.core.SecurityContext;

import DTO.CommentRequest;
import DTO.PostRequest;
import services.PostService;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @EJB
    private PostService postService;

    // Create a new post
    @POST
    @Path("/create/{userId}")
    public Response createPost(@PathParam("userId") Long userId, PostRequest postRequest) {
        return postService.createPost(userId, postRequest);
    }
    
    // Get a specific post by ID
    @GET
    @Path("/{postId}/{userId}")
    public Response getPost(@PathParam("postId") Long postId, @PathParam("userId") Long userId) {
        return postService.getPost(postId, userId);
    }
    
    // Get user's feed (posts from user and their friends)
    @GET
    @Path("/feed/{userId}")
    public Response getFeed(@PathParam("userId") Long userId) {
        return postService.getFeed(userId);
    }
    
    // Update a post
    @PUT
    @Path("/{postId}/{userId}")
    public Response updatePost(@PathParam("postId") Long postId, @PathParam("userId") Long userId, PostRequest postRequest) {
        return postService.updatePost(postId, userId, postRequest);
    }
    
    // Delete a post
    @DELETE
    @Path("/{postId}/{userId}")
    public Response deletePost(@PathParam("postId") Long postId, @PathParam("userId") Long userId) {
        return postService.deletePost(postId, userId);
    }
    
    // Like a post
    @POST
    @Path("/{postId}/like/{userId}")
    public Response likePost(@PathParam("postId") Long postId, @PathParam("userId") Long userId) {
        return postService.likePost(postId, userId);
    }
    
    // Comment on a post
    @POST
    @Path("/{postId}/comment/{userId}")
    public Response commentOnPost(@PathParam("postId") Long postId, @PathParam("userId") Long userId, CommentRequest commentRequest) {
        return postService.commentOnPost(postId, userId, commentRequest);
    }
}
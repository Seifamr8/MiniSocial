package api;

import DTO.CommentDTO;
import DTO.PostDTO;
import models.Comment;
import models.Post;
import services.PostService;
import util.DTOmapper;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @EJB
    private PostService postService;

    // Create a new post
    @POST
    @Path("/create/{userId}")
    public Response createPost(@PathParam("userId") Long userId, PostDTO postDTO) {
        // Convert DTO to entity
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setImageUrl(postDTO.getImageUrl());
        post.setLinkUrl(postDTO.getLinkUrl());
        
        return postService.createPost(userId, post);
    }

    // Update an existing post
    @PUT
    @Path("/{postId}/update/{userId}")
    public Response updatePost(@PathParam("postId") Long postId, @PathParam("userId") Long userId, PostDTO postDTO) {
        // Convert DTO to entity
        Post post = new Post();
        post.setContent(postDTO.getContent());
        post.setImageUrl(postDTO.getImageUrl());
        post.setLinkUrl(postDTO.getLinkUrl());
        
        return postService.updatePost(postId, userId, post);
    }

    // Delete a post
    @DELETE
    @Path("/{postId}/delete/{userId}")
    public Response deletePost(@PathParam("postId") Long postId, @PathParam("userId") Long userId) {
        return postService.deletePost(postId, userId);
    }

    // Get user feed (posts from user and connections)
    @GET
    @Path("/feed/{userId}")
    public Response getUserFeed(@PathParam("userId") Long userId) {
        List<Post> posts = postService.getUserFeed(userId);
        List<PostDTO> postDTOs = DTOmapper.toPostDTOList(posts, userId);
        return Response.ok(postDTOs).build();
    }

    // Get a specific post
    @GET
    @Path("/{postId}")
    public Response getPost(@PathParam("postId") Long postId, @QueryParam("userId") Long userId) {
        Post post = postService.getPost(postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Post not found.\"}").build();
        }
        
        PostDTO postDTO = DTOmapper.toPostDTOWithComments(post, userId);
        return Response.ok(postDTO).build();
    }

    // Like a post
    @POST
    @Path("/{postId}/like/{userId}")
    public Response likePost(@PathParam("userId") Long userId, @PathParam("postId") Long postId) {
        return postService.likePost(userId, postId);
    }

    // Unlike a post
    @DELETE
    @Path("/{postId}/unlike/{userId}")
    public Response unlikePost(@PathParam("userId") Long userId, @PathParam("postId") Long postId) {
        return postService.unlikePost(userId, postId);
    }

    // Add comment to post
    @POST
    @Path("/{postId}/comment/{userId}")
    public Response addComment(@PathParam("userId") Long userId, @PathParam("postId") Long postId, CommentDTO commentDTO) {
        // Convert DTO to entity
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        
        return postService.addComment(userId, postId, comment);
    }

    // Update a comment
    @PUT
    @Path("/comment/{commentId}/update/{userId}")
    public Response updateComment(@PathParam("commentId") Long commentId, @PathParam("userId") Long userId, CommentDTO commentDTO) {
        // Convert DTO to entity
        Comment comment = new Comment();
        comment.setContent(commentDTO.getContent());
        
        return postService.updateComment(commentId, userId, comment);
    }

    // Delete a comment
    @DELETE
    @Path("/comment/{commentId}/delete/{userId}")
    public Response deleteComment(@PathParam("commentId") Long commentId, @PathParam("userId") Long userId) {
        return postService.deleteComment(commentId, userId);
    }
    
    // Get comments for a post
    @GET
    @Path("/{postId}/comments")
    public Response getPostComments(@PathParam("postId") Long postId) {
        List<Comment> comments = postService.getPostComments(postId);
        List<CommentDTO> commentDTOs = DTOmapper.toCommentDTOList(comments);
        return Response.ok(commentDTOs).build();
    }
}
package services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
//import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import DTO.CommentRequest;
import DTO.CommentResponse;
import DTO.PostRequest;
import DTO.PostResponse;
import DTO.UserSummary;
import models.Comment;
import models.Like;
//import models.NotificationEvent;
//import models.NotificationEvent.EventType;
import models.Post;
import models.User;

@Stateless
public class PostService {

    @PersistenceContext(unitName = "hello")
    private EntityManager em;
    
   /* @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;*/
    
   /* @Resource(mappedName = "java:/jms/queue/NotificationQueue")
    private Queue notificationQueue;*/
    
    @EJB
    private UserService userService;

    // Create a new post
    public Response createPost(Long userId, PostRequest postRequest) {
        User user = em.find(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        Post post = new Post();
        post.setContent(postRequest.getContent());
        post.setImageUrl(postRequest.getImageUrl());
        post.setAuthor(user);
        post.setCreatedAt(new Date());
        
        em.persist(post);
        
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"Post created successfully.\", \"postId\": " + post.getId() + "}").build();
    }
    
    // Get a specific post by ID
    public Response getPost(Long postId, Long currentUserId) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Post not found.\"}").build();
        }
        
        PostResponse response = convertToPostResponse(post, currentUserId);
        return Response.ok(response).build();
    }
    
    // Get feed (posts from user and friends)
    public Response getFeed(Long userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        // In a real implementation, you would fetch posts from friends as well
        // For simplicity, just fetching user's posts for now
        List<Post> posts = em.createQuery(
                "SELECT p FROM Post p WHERE p.author.id = :userId ORDER BY p.createdAt DESC", Post.class)
                .setParameter("userId", userId)
                .getResultList();
        
        List<PostResponse> postResponses = posts.stream()
                .map(post -> convertToPostResponse(post, userId))
                .collect(Collectors.toList());
        
        return Response.ok(postResponses).build();
    }
    
    // Update a post
    public Response updatePost(Long postId, Long userId, PostRequest postRequest) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Post not found.\"}").build();
        }
        
        // Check if user is the author of the post or an admin
        User user = em.find(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        if (!post.getAuthor().getId().equals(userId) && !"admin".equals(user.getRole())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"You don't have permission to update this post.\"}").build();
        }
        
        post.setContent(postRequest.getContent());
        if (postRequest.getImageUrl() != null) {
            post.setImageUrl(postRequest.getImageUrl());
        }
        
        em.merge(post);
        
        return Response.ok("{\"message\": \"Post updated successfully.\"}").build();
    }
    
    // Delete a post
    public Response deletePost(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Post not found.\"}").build();
        }
        
        // Check if user is the author of the post or an admin
        User user = em.find(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        if (!post.getAuthor().getId().equals(userId) && !"admin".equals(user.getRole())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"You don't have permission to delete this post.\"}").build();
        }
        
        em.remove(post);
        
        return Response.ok("{\"message\": \"Post deleted successfully.\"}").build();
    }
    
    // Like a post
    public Response likePost(Long postId, Long userId) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Post not found.\"}").build();
        }
        
        User user = em.find(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        // Check if user already liked the post
        try {
            em.createQuery("SELECT l FROM Like l WHERE l.post.id = :postId AND l.user.id = :userId")
                .setParameter("postId", postId)
                .setParameter("userId", userId)
                .getSingleResult();
            
            // User already liked the post
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"You have already liked this post.\"}").build();
        } catch (NoResultException e) {
            // User hasn't liked the post yet, proceed
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            em.persist(like);
            
            // Send notification to post author
           /* if (!userId.equals(post.getAuthor().getId())) {
                sendNotification(EventType.POST_LIKE, userId, post.getAuthor().getId(), postId, 
                                user.getName() + " liked your post");
            }
            */
            return Response.ok("{\"message\": \"Post liked successfully.\"}").build();
        }
    }
    
    // Comment on a post
    public Response commentOnPost(Long postId, Long userId, CommentRequest commentRequest) {
        Post post = em.find(Post.class, postId);
        if (post == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Post not found.\"}").build();
        }
        
        User user = em.find(User.class, userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }
        
        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        comment.setPost(post);
        comment.setUser(user);
        
        em.persist(comment);
        
        // Send notification to post author
        /*if (!userId.equals(post.getAuthor().getId())) {
            sendNotification(EventType.POST_COMMENT, userId, post.getAuthor().getId(), postId, 
                            user.getName() + " commented on your post");
        }*/
        
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"Comment added successfully.\", \"commentId\": " + comment.getId() + "}").build();
    }
    
    // Helper method to convert Post to PostResponse
    private PostResponse convertToPostResponse(Post post, Long currentUserId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setContent(post.getContent());
        response.setImageUrl(post.getImageUrl());
        response.setCreatedAt(post.getCreatedAt());
        
        UserSummary authorSummary = new UserSummary();
        authorSummary.setId(post.getAuthor().getId());
        authorSummary.setName(post.getAuthor().getName());
        response.setAuthor(authorSummary);
        
        response.setLikeCount(post.getLikes().size());
        response.setCommentCount(post.getComments().size());
        
        // Check if current user has liked the post
        boolean likedByCurrentUser = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(currentUserId));
        response.setLikedByCurrentUser(likedByCurrentUser);
        
        // Get comments (you might want to limit this in a real app)
        List<CommentResponse> commentResponses = post.getComments().stream()
                .map(comment -> {
                    CommentResponse commentResponse = new CommentResponse();
                    commentResponse.setId(comment.getId());
                    commentResponse.setContent(comment.getContent());
                    commentResponse.setCreatedAt(comment.getCreatedAt());
                    
                    UserSummary userSummary = new UserSummary();
                    userSummary.setId(comment.getUser().getId());
                    userSummary.setName(comment.getUser().getName());
                    commentResponse.setUser(userSummary);
                    
                    return commentResponse;
                })
                .collect(Collectors.toList());
        response.setComments(commentResponses);
        
        return response;
    }
    
    // Helper method to send notifications via JMS
   /* private void sendNotification(EventType eventType, Long fromUserId, Long toUserId, Long contentId, String message) {
        try (JMSContext context = connectionFactory.createContext()) {
            NotificationEvent event = new NotificationEvent(eventType, fromUserId, toUserId, contentId, message);
            context.createProducer().send(notificationQueue, event);
        } catch (Exception e) {
            // Log the error but don't stop the main flow
            System.err.println("Failed to send notification: " + e.getMessage());
        }
    }*/
}

package services;

import models.Comment;
import models.Like;
import models.Post;
import models.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class PostService {
	
    @PersistenceContext
    private EntityManager em;

    // Create a new post
    public Response createPost(Long userId, Post post) {
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"User not found.\"}").build();
            }

            post.setAuthor(user);
            post.setCreatedAt(new Date());
            post.setUpdatedAt(new Date());
            
            em.persist(post);
            
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Post created successfully.\", \"postId\": " + post.getId() + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error creating post: " + e.getMessage() + "\"}").build();
        }
    }

    // Update a post
    public Response updatePost(Long postId, Long userId, Post updatedPost) {
        try {
            Post post = em.find(Post.class, postId);
            if (post == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Post not found.\"}").build();
            }

            if (!post.getAuthor().getId().equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\": \"You can only update your own posts.\"}").build();
            }

            // Update post fields
            if (updatedPost.getContent() != null) {
                post.setContent(updatedPost.getContent());
            }
            if (updatedPost.getImageUrl() != null) {
                post.setImageUrl(updatedPost.getImageUrl());
            }
            if (updatedPost.getLinkUrl() != null) {
                post.setLinkUrl(updatedPost.getLinkUrl());
            }
            
            post.setUpdatedAt(new Date());
            
            em.merge(post);
            
            return Response.ok("{\"message\": \"Post updated successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error updating post: " + e.getMessage() + "\"}").build();
        }
    }

    // Delete a post
    public Response deletePost(Long postId, Long userId) {
        try {
            Post post = em.find(Post.class, postId);
            if (post == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Post not found.\"}").build();
            }

            if (!post.getAuthor().getId().equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\": \"You can only delete your own posts.\"}").build();
            }

            em.remove(post);
            
            return Response.ok("{\"message\": \"Post deleted successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error deleting post: " + e.getMessage() + "\"}").build();
        }
    }

    // Get user feed (posts from user and connections)
    public List<Post> getUserFeed(Long userId) {
        User user = em.find(User.class, userId);
        if (user == null) {
            return new ArrayList<>();
        }

        // Get IDs of connected users (friends)
        TypedQuery<Long> friendsQuery = em.createQuery(
                "SELECT c.sender.id FROM Connection c WHERE c.receiver.id = :userId AND c.status = 'accepted' " +
                "UNION " +
                "SELECT c.receiver.id FROM Connection c WHERE c.sender.id = :userId AND c.status = 'accepted'", 
                Long.class);
        friendsQuery.setParameter("userId", userId);
        List<Long> friendIds = friendsQuery.getResultList();
        
        // Add current user ID to the list
        friendIds.add(userId);

        // Get posts from user and friends
        TypedQuery<Post> postsQuery = em.createQuery(
                "SELECT p FROM Post p WHERE p.author.id IN :userIds ORDER BY p.createdAt DESC", 
                Post.class);
        postsQuery.setParameter("userIds", friendIds);
        
        return postsQuery.getResultList();
    }

    // Get single post
    public Post getPost(Long postId) {
        return em.find(Post.class, postId);
    }

    // Like a post
    public Response likePost(Long userId, Long postId) {
        try {
            User user = em.find(User.class, userId);
            Post post = em.find(Post.class, postId);
            
            if (user == null || post == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"User or post not found.\"}").build();
            }

            // Check if user already liked this post
            TypedQuery<Long> likeQuery = em.createQuery(
                    "SELECT COUNT(l) FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId", 
                    Long.class);
            likeQuery.setParameter("userId", userId);
            likeQuery.setParameter("postId", postId);
            
            if (likeQuery.getSingleResult() > 0) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"You already liked this post.\"}").build();
            }

            // Create new like
            Like like = new Like();
            like.setUser(user);
            like.setPost(post);
            like.setCreatedAt(new Date());
            
            em.persist(like);
            
            return Response.ok("{\"message\": \"Post liked successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error liking post: " + e.getMessage() + "\"}").build();
        }
    }

    // Unlike a post
    public Response unlikePost(Long userId, Long postId) {
        try {
            // Find the like to remove
            TypedQuery<Like> likeQuery = em.createQuery(
                    "SELECT l FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId", 
                    Like.class);
            likeQuery.setParameter("userId", userId);
            likeQuery.setParameter("postId", postId);
            
            List<Like> likes = likeQuery.getResultList();
            
            if (likes.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"You have not liked this post.\"}").build();
            }

            // Remove the like
            Like like = likes.get(0);
            em.remove(like);
            
            return Response.ok("{\"message\": \"Post unliked successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error unliking post: " + e.getMessage() + "\"}").build();
        }
    }

    // Add comment to post
    public Response addComment(Long userId, Long postId, Comment comment) {
        try {
            User user = em.find(User.class, userId);
            Post post = em.find(Post.class, postId);
            
            if (user == null || post == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"User or post not found.\"}").build();
            }

            comment.setUser(user);
            comment.setPost(post);
            comment.setCreatedAt(new Date());
            comment.setUpdatedAt(new Date());
            
            em.persist(comment);
            
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Comment added successfully.\", \"commentId\": " + comment.getId() + "}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error adding comment: " + e.getMessage() + "\"}").build();
        }
    }

    // Update a comment
    public Response updateComment(Long commentId, Long userId, Comment updatedComment) {
        try {
            Comment comment = em.find(Comment.class, commentId);
            if (comment == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Comment not found.\"}").build();
            }

            if (!comment.getUser().getId().equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\": \"You can only update your own comments.\"}").build();
            }

            // Update comment content
            if (updatedComment.getContent() != null) {
                comment.setContent(updatedComment.getContent());
            }
            
            comment.setUpdatedAt(new Date());
            
            em.merge(comment);
            
            return Response.ok("{\"message\": \"Comment updated successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error updating comment: " + e.getMessage() + "\"}").build();
        }
    }

    // Delete a comment
    public Response deleteComment(Long commentId, Long userId) {
        try {
            Comment comment = em.find(Comment.class, commentId);
            if (comment == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Comment not found.\"}").build();
            }

            if (!comment.getUser().getId().equals(userId) && 
                !comment.getPost().getAuthor().getId().equals(userId)) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("{\"message\": \"You can only delete your own comments or comments on your posts.\"}").build();
            }

            em.remove(comment);
            
            return Response.ok("{\"message\": \"Comment deleted successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error deleting comment: " + e.getMessage() + "\"}").build();
        }
    }

    // Get comments for a post
    public List<Comment> getPostComments(Long postId) {
        TypedQuery<Comment> query = em.createQuery(
                "SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt ASC", 
                Comment.class);
        query.setParameter("postId", postId);
        
        return query.getResultList();
    }
}
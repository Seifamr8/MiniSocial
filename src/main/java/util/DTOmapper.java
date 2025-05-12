package util;

import DTO.*;
import models.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class to convert between Entities and DTOs
 */
public class DTOmapper {

    /**
     * Convert User entity to UserDTO
     */
    public static UserDTO toUserDTO(User user) {
        if (user == null) return null;
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setBio(user.getBio());
        dto.setRole(user.getRole());
        
        return dto;
    }
    
    /**
     * Convert Post entity to PostDTO
     */
    public static PostDTO toPostDTO(Post post, Long currentUserId) {
        if (post == null) return null;
        
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        dto.setLinkUrl(post.getLinkUrl());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        dto.setAuthor(toUserDTO(post.getAuthor()));
        
        // Count likes and comments
        dto.setLikesCount(post.getLikes().size());
        dto.setCommentsCount(post.getComments().size());
        
        // Check if current user has liked this post
        boolean likedByCurrentUser = post.getLikes().stream()
                .anyMatch(like -> like.getUser().getId().equals(currentUserId));
        dto.setLikedByCurrentUser(likedByCurrentUser);
        
        return dto;
    }
    
    /**
     * Convert Post entity to PostDTO with comments
     */
    public static PostDTO toPostDTOWithComments(Post post, Long currentUserId) {
        PostDTO dto = toPostDTO(post, currentUserId);
        if (dto != null) {
            dto.setComments(post.getComments().stream()
                    .map(DTOmapper::toCommentDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
    
    /**
     * Convert Comment entity to CommentDTO
     */
    public static CommentDTO toCommentDTO(Comment comment) {
        if (comment == null) return null;
        
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setUser(toUserDTO(comment.getUser()));
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        
        return dto;
    }
    
    /**
     * Convert Like entity to LikeDTO
     */
    public static LikeDTO toLikeDTO(Like like) {
        if (like == null) return null;
        
        LikeDTO dto = new LikeDTO();
        dto.setId(like.getId());
        dto.setUser(toUserDTO(like.getUser()));
        dto.setPostId(like.getPost().getId());
        dto.setCreatedAt(like.getCreatedAt());
        
        return dto;
    }
    
    /**
     * Convert Connection entity to ConnectionDTO
     */
    public static ConnectionDTO toConnectionDTO(Connection connection) {
        if (connection == null) return null;
        
        ConnectionDTO dto = new ConnectionDTO();
        dto.setId(connection.getId());
        dto.setSender(toUserDTO(connection.getSender()));
        dto.setReceiver(toUserDTO(connection.getReceiver()));
        dto.setStatus(connection.getStatus());
        
        return dto;
    }
    
    /**
     * Convert list of entities to list of DTOs
     */
    public static List<PostDTO> toPostDTOList(List<Post> posts, Long currentUserId) {
        return posts.stream()
                .map(post -> toPostDTO(post, currentUserId))
                .collect(Collectors.toList());
    }
    
    public static List<CommentDTO> toCommentDTOList(List<Comment> comments) {
        return comments.stream()
                .map(DTOmapper::toCommentDTO)
                .collect(Collectors.toList());
    }
    
    public static List<ConnectionDTO> toConnectionDTOList(List<Connection> connections) {
        return connections.stream()
                .map(DTOmapper::toConnectionDTO)
                .collect(Collectors.toList());
    }
}
package DTO;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

/**
 * Data Transfer Object for Post entity
 */
public class PostDTO {
    private Long id;
    private String content;
    private String imageUrl;
    private String linkUrl;
    private Date createdAt;
    private Date updatedAt;
    private UserDTO author;
    private int likesCount;
    private int commentsCount;
    private boolean likedByCurrentUser;
    private List<CommentDTO> comments = new ArrayList<>();
    
    // Constructors
    public PostDTO() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    public String getLinkUrl() {
        return linkUrl;
    }
    
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public UserDTO getAuthor() {
        return author;
    }
    
    public void setAuthor(UserDTO author) {
        this.author = author;
    }
    
    public int getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    
    public int getCommentsCount() {
        return commentsCount;
    }
    
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }
    
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
    
    public List<CommentDTO> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentDTO> comments) {
        this.comments = comments;
    }
}
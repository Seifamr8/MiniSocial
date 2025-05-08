package DTO;

import java.util.Date;
import java.util.List;

public class PostResponse {
    private Long id;
    private String content;
    private String imageUrl;
    private Date createdAt;
    private UserSummary author;
    private int likeCount;
    private int commentCount;
    private boolean likedByCurrentUser;
    private List<CommentResponse> comments;
    
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public UserSummary getAuthor() {
        return author;
    }
    
    public void setAuthor(UserSummary author) {
        this.author = author;
    }
    
    public int getLikeCount() {
        return likeCount;
    }
    
    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }
    
    public int getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }
    
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
    }
    
    public List<CommentResponse> getComments() {
        return comments;
    }
    
    public void setComments(List<CommentResponse> comments) {
        this.comments = comments;
    }
}

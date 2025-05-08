package DTO;

import java.util.Date;

public class CommentResponse {
    private Long id;
    private String content;
    private Date createdAt;
    private UserSummary user;
    
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public UserSummary getUser() {
        return user;
    }
    
    public void setUser(UserSummary user) {
        this.user = user;
    }
}

package DTO;

/**
 * Data Transfer Object for Connection entity
 */
public class ConnectionDTO {
    private Long id;
    private UserDTO sender;
    private UserDTO receiver;
    private String status;
    
    // Constructors
    public ConnectionDTO() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public UserDTO getSender() {
        return sender;
    }
    
    public void setSender(UserDTO sender) {
        this.sender = sender;
    }
    
    public UserDTO getReceiver() {
        return receiver;
    }
    
    public void setReceiver(UserDTO receiver) {
        this.receiver = receiver;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}
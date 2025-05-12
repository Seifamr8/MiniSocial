package DTO;

import java.util.List;

public class GroupDTO {
    private Long id;
    private String name;
    private String description;
    private boolean isOpen;
    private UserDTO creator;
    private List<GroupMemberDTO> members;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isOpen() { return isOpen; }
    public void setOpen(boolean open) { isOpen = open; }

    public UserDTO getCreator() { return creator; }
    public void setCreator(UserDTO creator) { this.creator = creator; }

    public List<GroupMemberDTO> getMembers() { return members; }
    public void setMembers(List<GroupMemberDTO> members) { this.members = members; }
}

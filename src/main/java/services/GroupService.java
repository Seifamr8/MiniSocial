/*package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import models.Group;
import models.User;

@Stateless
public class GroupService {

    @PersistenceContext(unitName = "hello")
    private EntityManager em;

    public Response createGroup(Group group) {
        em.persist(group);
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"Group created successfully.\"}")
                .build();
    }

    public Response joinGroup(Long groupId, User user) {
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Group not found.\"}")
                    .build();
        }
        if (group.isOpen() || group.getCreator().equals(user.getName())) {
            group.getMembers().add(user);
            em.merge(group);
            return Response.status(Response.Status.OK)
                    .entity("{\"message\": \"Joined the group.\"}")
                    .build();
        }
        return Response.status(Response.Status.FORBIDDEN)
                .entity("{\"message\": \"Approval needed to join.\"}")
                .build();
    }

    public Response postInGroup(Long groupId, String content, User user) {
        Group group = em.find(Group.class, groupId);
        if (group == null || !group.getMembers().contains(user)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"You are not a member of this group.\"}")
                    .build();
        }
        // Logic to add post to the group (not shown)
        return Response.status(Response.Status.OK)
                .entity("{\"message\": \"Post added to the group.\"}")
                .build();
    }
}
*/
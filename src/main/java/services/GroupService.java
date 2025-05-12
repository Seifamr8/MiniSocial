package services;

import models.Group;
import models.GroupMember;
import models.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Date;

@Stateless
public class GroupService {

    @PersistenceContext
    private EntityManager em;

    public Response createGroup(Long userId, Group group) {
        User creator = em.find(User.class, userId);
        if (creator == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User not found.\"}").build();
        }

        group.setCreator(creator);
        group.setCreatedAt(new Date());
        group.setMembers(new ArrayList<>());

        GroupMember member = new GroupMember();
        member.setUser(creator);
        member.setGroup(group);
        member.setStatus("accepted");
        member.setRole("admin");
        group.getMembers().add(member);

        em.persist(group);

        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"Group created successfully.\", \"groupId\": " + group.getId() + "}").build();
    }

    public Response joinGroup(Long userId, Long groupId) {
        User user = em.find(User.class, userId);
        Group group = em.find(Group.class, groupId);
        if (user == null || group == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"User or group not found.\"}").build();
        }

        // Check if user is already a member or requested
        TypedQuery<GroupMember> query = em.createQuery(
                "SELECT gm FROM GroupMember gm WHERE gm.user.id = :userId AND gm.group.id = :groupId",
                GroupMember.class);
        query.setParameter("userId", userId);
        query.setParameter("groupId", groupId);

        if (!query.getResultList().isEmpty()) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"message\": \"Already a member or membership request pending.\"}").build();
        }

        GroupMember member = new GroupMember();
        member.setUser(user);
        member.setGroup(group);
        if (group.isOpen()) {
            member.setStatus("accepted");
            member.setRole("member");
        } else {
            member.setStatus("pending");
            member.setRole("member");
        }
        em.persist(member);
        return Response.ok("{\"message\": \"" + (group.isOpen() ? "Joined group successfully." : "Join request sent successfully.") + "\"}").build();
    }

    public Response respondToJoinRequest(Long groupId, Long memberId, Long adminId, String action) {
        Group group = em.find(Group.class, groupId);
        User admin = em.find(User.class, adminId);
        GroupMember member = em.find(GroupMember.class, memberId);
        if (group == null || admin == null || member == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Group, admin, or member not found.\"}").build();
        }

        // Check admin permission
        if (!isGroupAdmin(groupId, adminId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"Only group admins can approve or reject requests.\"}").build();
        }

        if (!member.getGroup().getId().equals(groupId)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Membership does not belong to the specified group.\"}").build();
        }

        if (!member.getStatus().equals("pending")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"This membership is already processed.\"}").build();
        }

        if (!action.equalsIgnoreCase("accept") && !action.equalsIgnoreCase("reject")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid action. Use 'accept' or 'reject'.\"}").build();
        }

        member.setStatus(action.equalsIgnoreCase("accept") ? "accepted" : "rejected");
        em.merge(member);

        // Optionally send JMS notification here

        return Response.ok("{\"message\": \"Membership request " + action + "ed.\"}").build();
    }

    public boolean isGroupAdmin(Long groupId, Long userId) {
        TypedQuery<Long> query = em.createQuery(
                "SELECT COUNT(gm) FROM GroupMember gm WHERE gm.group.id = :groupId AND gm.user.id = :userId AND gm.role = 'admin' AND gm.status = 'accepted'",
                Long.class);
        query.setParameter("groupId", groupId);
        query.setParameter("userId", userId);
        return query.getSingleResult() > 0;
    }

    public Response promoteMember(Long groupId, Long adminId, Long memberId) {
        if (!isGroupAdmin(groupId, adminId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"Only group admins can promote members.\"}").build();
        }

        GroupMember member = em.find(GroupMember.class, memberId);
        if (member == null || !member.getGroup().getId().equals(groupId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Member not found in this group.\"}").build();
        }

        member.setRole("admin");
        em.merge(member);

        return Response.ok("{\"message\": \"Member promoted to admin successfully.\"}").build();
    }

    public Response removeMember(Long groupId, Long adminId, Long memberId) {
        if (!isGroupAdmin(groupId, adminId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"Only group admins can remove members.\"}").build();
        }
        GroupMember member = em.find(GroupMember.class, memberId);
        if (member == null || !member.getGroup().getId().equals(groupId)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Member not found in this group.\"}").build();
        }

        em.remove(member);
        return Response.ok("{\"message\": \"Member removed successfully.\"}").build();
    }

    public Response deleteGroup(Long groupId, Long adminId) {
        if (!isGroupAdmin(groupId, adminId)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"message\": \"Only group admins can delete the group.\"}").build();
        }
        Group group = em.find(Group.class, groupId);
        if (group == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Group not found.\"}").build();
        }

        em.remove(group);
        return Response.ok("{\"message\": \"Group deleted successfully.\"}").build();
    }

    // Additional methods for group posts and retrieval can be added here similarly
}

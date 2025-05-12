package api;

import models.Group;
import services.GroupService;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/groups")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GroupResource {

    @EJB
    private GroupService groupService;

    // Create a group by userId
    @POST
    @Path("/create/{userId}")
    public Response createGroup(@PathParam("userId") Long userId, Group group) {
        return groupService.createGroup(userId, group);
    }

    // Join a group
    @POST
    @Path("/{groupId}/join/{userId}")
    public Response joinGroup(@PathParam("userId") Long userId, @PathParam("groupId") Long groupId) {
        return groupService.joinGroup(userId, groupId);
    }

    // Respond to join request (accept or reject)
    @PUT
    @Path("/{groupId}/respond/{memberId}/{adminId}")
    public Response respondToJoinRequest(
            @PathParam("groupId") Long groupId,
            @PathParam("memberId") Long memberId,
            @PathParam("adminId") Long adminId,
            @QueryParam("action") String action) {
        return groupService.respondToJoinRequest(groupId, memberId, adminId, action);
    }

    // Promote member to admin
    @PUT
    @Path("/{groupId}/promote/{adminId}/{memberId}")
    public Response promoteMember(
            @PathParam("groupId") Long groupId,
            @PathParam("adminId") Long adminId,
            @PathParam("memberId") Long memberId) {
        return groupService.promoteMember(groupId, adminId, memberId);
    }

    // Remove member from group
    @DELETE
    @Path("/{groupId}/remove/{adminId}/{memberId}")
    public Response removeMember(
            @PathParam("groupId") Long groupId,
            @PathParam("adminId") Long adminId,
            @PathParam("memberId") Long memberId) {
        return groupService.removeMember(groupId, adminId, memberId);
    }

    // Delete group
    @DELETE
    @Path("/{groupId}/delete/{adminId}")
    public Response deleteGroup(
            @PathParam("groupId") Long groupId,
            @PathParam("adminId") Long adminId) {
        return groupService.deleteGroup(groupId, adminId);
    }
}


/*package api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import models.Group;
import models.User;
import services.GroupService;

@Path("/groups")
public class GroupResource {

    @Inject
    private GroupService groupService;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGroup(Group group) {
        return groupService.createGroup(group);
    }

    @POST
    @Path("/{id}/join")
    @Produces(MediaType.APPLICATION_JSON)
    public Response joinGroup(@PathParam("id") Long groupId, User user) {
        return groupService.joinGroup(groupId, user);
    }

    @POST
    @Path("/{id}/post")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response postInGroup(@PathParam("id") Long groupId, String content, User user) {
        return groupService.postInGroup(groupId, content, user);
    }
}	
*/
package api;

import DTO.ConnectionDTO;
import models.Connection;
import services.ConnectionService;
import util.DTOmapper;

import javax.ejb.EJB;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/connections")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ConnectionResource {

    @EJB
    private ConnectionService connectionService;

    // Send request
    @POST
    @Path("/send/{senderId}/{receiverId}")
    public Response sendRequest(@PathParam("senderId") Long senderId, @PathParam("receiverId") Long receiverId) {
        return connectionService.sendRequest(senderId, receiverId);
    }

    // Respond to request
    @PUT
    @Path("/respond/{requestId}/{action}")
    public Response respondRequest(@PathParam("requestId") Long requestId, @PathParam("action") String action) {
        return connectionService.respondRequest(requestId, action);
    }

    // View friends
    @GET
    @Path("/view/{userId}")
    public Response viewConnections(@PathParam("userId") Long userId) {
        List<Connection> connections = connectionService.viewConnections(userId);
        List<ConnectionDTO> connectionDTOs = DTOmapper.toConnectionDTOList(connections);
        return Response.ok(connectionDTOs).build();
    }
}
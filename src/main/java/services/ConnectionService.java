package services;

import models.Connection;
import models.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class ConnectionService {

    @PersistenceContext
    private EntityManager em;

    // Send friend request
    public Response sendRequest(Long senderId, Long receiverId) {
        try {
            // Check if users exist
            User sender = em.find(User.class, senderId);
            User receiver = em.find(User.class, receiverId);

            if (sender == null || receiver == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"User not found.\"}").build();
            }

            // Check if request already exists
            TypedQuery<Connection> query = em.createQuery(
                    "SELECT c FROM Connection c WHERE " +
                            "((c.sender.id = :senderId AND c.receiver.id = :receiverId) OR " +
                            "(c.sender.id = :receiverId AND c.receiver.id = :senderId))",
                    Connection.class);
            query.setParameter("senderId", senderId);
            query.setParameter("receiverId", receiverId);

            List<Connection> existingConnections = query.getResultList();
            if (!existingConnections.isEmpty()) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"Connection request already exists.\"}").build();
            }

            // Create new connection request
            Connection connection = new Connection();
            connection.setSender(sender);
            connection.setReceiver(receiver);
            connection.setStatus("pending");

            em.persist(connection);

            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"Friend request sent successfully.\", \"requestId\": " + connection.getId() + "}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error sending friend request: " + e.getMessage() + "\"}").build();
        }
    }

    // Respond to friend request
    public Response respondRequest(Long requestId, String action) {
        try {
            Connection connection = em.find(Connection.class, requestId);
            if (connection == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"Connection request not found.\"}").build();
            }

            if (!action.equals("accept") && !action.equals("reject")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"Invalid action. Use 'accept' or 'reject'.\"}").build();
            }

            if (!connection.getStatus().equals("pending")) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("{\"message\": \"This request has already been processed.\"}").build();
            }

            // Update connection status
            connection.setStatus(action.equals("accept") ? "accepted" : "rejected");
            em.merge(connection);

            return Response.ok("{\"message\": \"Friend request " + action + "ed successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error processing friend request: " + e.getMessage() + "\"}").build();
        }
    }

    // View connections (friends)
    public List<Connection> viewConnections(Long userId) {
        TypedQuery<Connection> query = em.createQuery(
                "SELECT c FROM Connection c WHERE " +
                        "((c.sender.id = :userId) OR (c.receiver.id = :userId)) " +
                        "AND c.status = 'accepted'",
                Connection.class);
        query.setParameter("userId", userId);

        return query.getResultList();
    }
}
package services;

import models.Connection;
import models.User;

import javax.ejb.Stateless;
import javax.persistence.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class ConnectionService {

    @PersistenceContext(unitName = "hello")
    private EntityManager em;

    // Send friend request
    public Response sendRequest(Long senderId, Long receiverId) {
        User sender = em.find(User.class, senderId);
        User receiver = em.find(User.class, receiverId);

        if (sender == null || receiver == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"message\": \"Sender or receiver not found.\"}")
                    .build();
        }

        // Check if already connected or request exists
        Long count = em.createQuery(
                "SELECT COUNT(c) FROM Connection c WHERE c.sender.id = :senderId AND c.receiver.id = :receiverId", Long.class)
                .setParameter("senderId", senderId)
                .setParameter("receiverId", receiverId)
                .getSingleResult();
        if (count > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Friend request already sent.\"}")
                    .build();
        }

        Connection conn = new Connection();
        conn.setSender(sender);
        conn.setReceiver(receiver);
        conn.setStatus("pending");

        em.persist(conn);

        return Response.ok("{\"message\": \"Friend request sent.\"}").build();
    }

    // Accept or Reject request
    public Response respondRequest(Long requestId, String action) {
        Connection conn = em.find(Connection.class, requestId);
        if (conn == null) return Response.status(Response.Status.NOT_FOUND).build();

        if (!action.equalsIgnoreCase("accept") && !action.equalsIgnoreCase("reject")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Invalid action.\"}")
                    .build();
        }

        conn.setStatus(action.equalsIgnoreCase("accept") ? "accepted" : "rejected");
        em.merge(conn);

        return Response.ok("{\"message\": \"Request " + action + "ed.\"}").build();
    }

    // View connections (accepted)
    public List<Connection> viewConnections(Long userId) {
        return em.createQuery(
                "SELECT c FROM Connection c WHERE (c.sender.id = :id OR c.receiver.id = :id) AND c.status = 'accepted'",
                Connection.class)
                .setParameter("id", userId)
                .getResultList();
    }
}

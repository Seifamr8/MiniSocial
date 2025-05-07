package services;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.core.Response;

import models.User;

@Stateless
public class UserService {

    @PersistenceContext(unitName = "hello")
    private EntityManager em;

    public Response registerUser(User user) {
        if (em.createQuery("SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class)
                .setParameter("email", user.getEmail())
                .getSingleResult() > 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"message\": \"Email already in use.\"}")
                    .build();
        }

        em.persist(user);
        return Response.status(Response.Status.CREATED)
                .entity("{\"message\": \"User registered successfully.\"}")
                .build();
    }

    public User loginUser(String email, String password) {
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public Response updateUser(Long userId, User updatedData) {
        User user = em.find(User.class, userId);
        if (user == null) return Response.status(Response.Status.NOT_FOUND).build();

        user.setName(updatedData.getName());
        user.setBio(updatedData.getBio());
        user.setEmail(updatedData.getEmail());
        user.setPassword(updatedData.getPassword());

        em.merge(user);

        return Response.ok("{\"message\": \"Profile updated successfully.\"}").build();
    }
}

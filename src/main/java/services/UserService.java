package services;

import models.User;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.core.Response;

@Stateless
public class UserService {

    @PersistenceContext
    private EntityManager em;

    // Register a new user
    public Response registerUser(User user) {
        try {
            // Check if email already exists
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
            query.setParameter("email", user.getEmail());
            Long count = query.getSingleResult();

            if (count > 0) {
                return Response.status(Response.Status.CONFLICT)
                        .entity("{\"message\": \"Email already exists.\"}").build();
            }

            // Hash password (in a real application)
            // user.setPassword(hashPassword(user.getPassword()));

            // Set default role if not specified
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("user");
            }

            em.persist(user);
            return Response.status(Response.Status.CREATED)
                    .entity("{\"message\": \"User registered successfully.\", \"userId\": " + user.getId() + "}")
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error registering user: " + e.getMessage() + "\"}").build();
        }
    }

    // Login user
    public User loginUser(String email, String password) {
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class);
            query.setParameter("email", email);
            query.setParameter("password", password); // In a real app, you would verify a hashed password
            
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    // Update user profile
    public Response updateUser(Long userId, User updatedUser) {
        try {
            User user = em.find(User.class, userId);
            if (user == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"message\": \"User not found.\"}").build();
            }
            
            // Update fields if they're provided in the request
            if (updatedUser.getName() != null) {
                user.setName(updatedUser.getName());
            }
            if (updatedUser.getBio() != null) {
                user.setBio(updatedUser.getBio());
            }
            if (updatedUser.getEmail() != null) {
                // Check if new email is already in use by another user
                TypedQuery<Long> query = em.createQuery(
                        "SELECT COUNT(u) FROM User u WHERE u.email = :email AND u.id != :userId", Long.class);
                query.setParameter("email", updatedUser.getEmail());
                query.setParameter("userId", userId);
                
                if (query.getSingleResult() > 0) {
                    return Response.status(Response.Status.CONFLICT)
                            .entity("{\"message\": \"Email already in use.\"}").build();
                }
                
                user.setEmail(updatedUser.getEmail());
            }
            if (updatedUser.getPassword() != null) {
                // In a real app, you would hash the password
                user.setPassword(updatedUser.getPassword());
            }
            if (updatedUser.getRole() != null) {
                user.setRole(updatedUser.getRole());
            }
            
            em.merge(user);
            
            return Response.ok("{\"message\": \"User profile updated successfully.\"}").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"message\": \"Error updating user profile: " + e.getMessage() + "\"}").build();
        }
    }
    
    // Get user by ID
    public User getUser(Long userId) {
        return em.find(User.class, userId);
    }}
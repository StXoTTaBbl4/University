package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.AdminRequest;
import org.xoeqvdp.lab1.model.Roles;
import org.xoeqvdp.lab1.model.User;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable{
    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    private User user = new User();
    private String message = "";
    private boolean loggedIn = false;

    FacesContext fCtx = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
    String sessionID = session.getId();

    private final Session dbSession = HibernateUtil.getSessionFactory().openSession();

    public String registerUser() {

        User buff = dbSession.createQuery("from User where username = :username", User.class).setParameter("username", user.getUsername()).uniqueResult();

        if (buff != null){
            message = "Пользователь с таким именем уже существует!";
            return null;
        }

        buff = dbSession.createQuery("from User where passwordHash = :pwd", User.class).setParameter("pwd", user.getPasswordHash()).uniqueResult();
        if (buff != null) {
            message = "Пользователь с таким паролем уже существует!";
            return null;
        }

        Transaction transaction = dbSession.beginTransaction();
        dbSession.persist(user);
        transaction.commit();
        message = "Пользователь добавлен, выполните вход";
        return null;
    }

    public String loginUser() {
        user = dbSession.createQuery("from User where username = :username and passwordHash = :password", User.class)
                .setParameter("username", user.getUsername())
                .setParameter("password",user.getPasswordHash())
                .uniqueResult();
        if (user != null){
            System.out.println(user);
            loggedIn = true;
            return "main.xhtml?faces-redirect=true";
        }
        message = "Проверьте правильность логина и пароля";
        return null;
    }

    public String logoutUser() {
        user = new User();
        loggedIn = false;
        message = "Выход выполнен!";
        return "auth.xhtml?faces-redirect=true";
    }

    public boolean isAdmin(){
        if (user != null){
            return user.getRole() == Roles.ADMIN;
        }
        return false;
    }

    public void sendAdminRequest() {
        if (user.getId() != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    AdminRequest adminRequest = new AdminRequest();
                    adminRequest.setUser(user);
                    session.merge(adminRequest);
                    session.flush();
                    transaction.commit();
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error adding entity to database", e);
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            }
        }
    }
}

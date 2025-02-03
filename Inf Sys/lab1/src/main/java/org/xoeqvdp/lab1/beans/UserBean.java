package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PreDestroy;
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



import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable {
    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    private User user = new User();
    private User new_user = new User();
    private String message = "";
    private boolean loggedIn = false;

    FacesContext fCtx = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
    String sessionID = session.getId();

    public String  registerUser() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (session.createQuery("from User where username = :username", User.class).setParameter("username", new_user.getUsername()).uniqueResult() != null){
                    message = "Такой пользователь уже существует!";
                    return null;
                }

                session.persist(new_user);
                session.flush();
                transaction.commit();
                message = "Пользователь добавлен, выполните вход";
            } catch (Exception e){
                transaction.rollback();
                logger.log(Level.SEVERE, "Error adding entity to database", e);
                message = "Ошибка при создании пользователя";
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }

        new_user = new User();
        return null;
    }

    public String loginUser() {
        System.out.println(user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                user = session.createQuery("from User where username = :username and passwordHash = :password", User.class)
                        .setParameter("username", user.getUsername())
                        .setParameter("password",user.getPasswordHash())
                        .uniqueResult();
                if (user == null) {
                    message = "Проверьте логин или пароль";
                    user = new User();
                    return "";
                }
                System.out.println(user);
                loggedIn = true;
                return "main.xhtml?faces-redirect=true";
            } catch (Exception e){
                transaction.rollback();
                logger.log(Level.SEVERE, "Error adding entity to database", e);
                message = "Ошибка при поиске пользователя";
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }
        return "";
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
        System.out.println("INVOKED ADMIN REQUEST\n\n\n");
        if (user.getId() != null && user.getRole() != Roles.ADMIN) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    if (!session.createQuery("FROM AdminRequest where user = :user", AdminRequest.class).setParameter("user", user).getResultList().isEmpty()) {
                        return;
                    }
                    AdminRequest adminRequest = new AdminRequest();
                    adminRequest.setUser(user);
                    session.persist(adminRequest);
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

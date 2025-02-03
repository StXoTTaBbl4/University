package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.AdminRequest;
import org.xoeqvdp.lab1.model.Roles;
import org.xoeqvdp.lab1.model.User;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class AdminBean implements Serializable {
    private static final Logger logger = Logger.getLogger(AdminBean.class.getName());
    @Getter
    private String message;
    private List<AdminRequest> requests;

    @Inject
    UserBean userBean;

    @PostConstruct
    public void init(){
        requests = getRequests();
    }

    public List<AdminRequest> getRequests() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                try {
                    return session.createQuery("SELECT r FROM AdminRequest r", AdminRequest.class).getResultList();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error executing query", e);
                    message = "Ошибка при обращении к БД";
                }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }
        return new ArrayList<>();
    }

    public void acceptRequest(Long id) {

            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                AdminRequest request = session.find(AdminRequest.class, id);
                if (request != null) {
                    Transaction transaction = session.beginTransaction();
                    try {
                        User user = session.createQuery("from User where id = :id", User.class).setParameter("id", id).getSingleResult();
                        AdminRequest adminRequest = session.createQuery("from AdminRequest where user = :user", AdminRequest.class).setParameter("user", user).getSingleResult();
                        if (user != null && adminRequest != null) {
                            user.setRole(Roles.ADMIN);
                            session.merge(user);
                            session.remove(adminRequest);
                            session.flush();
                            transaction.commit();
                        } else {
                            transaction.rollback();
                            logger.log(Level.WARNING, "User or admin request not found, idi ispravlay");
                            message = "Не найдена запись о пользователе или о запросе";
                        }
                    } catch (Exception e) {
                        transaction.rollback();
                        logger.log(Level.SEVERE, "Error executing query", e);
                        message = "Ошибка при обращении к БД";
                    }
                }
            } catch (Exception e){
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                message = "Ошибка при попытке подключения к БД";
            }

        requests = getRequests();
        NotificationWebSocket.broadcast("update-admin");
    }

    public void rejectRequest(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AdminRequest request = session.find(AdminRequest.class, id);
            if (request != null) {
                Transaction transaction = session.beginTransaction();
                try {
                    AdminRequest adminRequest = session.find(AdminRequest.class, id);
                    if (adminRequest != null) {
                        session.remove(adminRequest);
                        transaction.commit();
                        requests = getRequests();
                        NotificationWebSocket.broadcast("update-admin");
                    } else {
                        transaction.rollback();
                        logger.log(Level.WARNING, "User or admin request not found, idi ispravlay");
                        message = "Не найдена запись о пользователе или о запросе";
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    message = "Ошибка при обращении к БД";
                }
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }
    }

    public Long getAdminRequests(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                return session.createQuery("select count(*) from AdminRequest", Long.class).uniqueResult();
            } catch (Exception e){
                transaction.rollback();
                logger.log(Level.SEVERE, "Error while making request", e);
                message = "Ошибка при обращении к БД";
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }
        return -1L;
    }
}

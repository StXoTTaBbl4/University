package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("coordinatesInfo")
@ViewScoped
@Getter
public class CoordinatesInfoBean implements Serializable {
    private static final Logger logger = Logger.getLogger(CoordinatesInfoBean.class.getName());

    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    @Inject
    private InfoBean infoBean;

    private Coordinates original_coordinates;
    private Coordinates coordinates;
    private CoordinatesInteraction coordinatesInteraction;
    private boolean isEditable = false;

    @PostConstruct
    public void init() {
        try {
            Transaction transaction = session.beginTransaction();
            original_coordinates = session.createQuery("from Coordinates where id = :id", Coordinates.class)
                    .setParameter("id", infoBean.getId())
                    .uniqueResult();

            coordinatesInteraction = session.createQuery("from CoordinatesInteraction where coordinate = :coordinates", CoordinatesInteraction.class)
                    .setParameter("coordinates", original_coordinates)
                    .getSingleResult();

            transaction.commit();
            coordinates = original_coordinates;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "CoordinatesInfoBean init(): db interaction error", e);
        }

        if (userBean.getUser() == null || coordinatesInteraction == null) {
            return;
        }

        if (userBean.getUser().getRole() == Roles.ADMIN) {
            isEditable = true;
            return;
        }

        if (Objects.equals(coordinatesInteraction.getCreator().getId(), userBean.getUser().getId())) {
            isEditable = true;
        }

    }

    public void update() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(coordinates);
                transaction.commit();
                NotificationWebSocket.broadcast("update-coordinates");
            } catch (Exception e) {
                transaction.rollback();
                logger.log(Level.SEVERE, "Error updating entity to database", e);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
        }
    }

    public void reset() {
        init();
    }

    public String delete(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(coordinates);
                session.flush();
                transaction.commit();
                NotificationWebSocket.broadcast("update-coordinates");
                return "/main.xhtml?faces-redirect=true";
            } catch (Exception e) {
                transaction.rollback();
                logger.log(Level.SEVERE, "Error deleting entity from database", e);
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return null;
        }
    }
}

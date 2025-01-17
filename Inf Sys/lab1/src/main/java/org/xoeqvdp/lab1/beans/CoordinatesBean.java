package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Named("coordinatesBean")
@RequestScoped
@Getter
@Setter
public class CoordinatesBean implements Serializable {

    private Coordinates coordinates = new Coordinates();
    private List<Coordinates> allCoordinates = null;
    private String message;
    private Long selectedId;

    private int page = 1;
    private boolean lastPage = false;
    private final int itemsPerPage = 10;

    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    @PostConstruct
    public void init(){
        loadPage();
    }

    public String createCoordinates(){
        if (userBean.getUser() == null || userBean.getUser().getId() == null) {
            message = "Только авторизованные пользователи могут вносить изменения!";
            return null;
        }

        CoordinatesInteraction interaction = new CoordinatesInteraction();
        interaction.setCreator(userBean.getUser());
        interaction.setCoordinate(coordinates);
        interaction.setModifier(userBean.getUser());
        interaction.setModifiedDate(Timestamp.from(Instant.now()));

        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            session.persist(coordinates);
            session.persist(interaction);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        resetCoordinates();
        message = "Successful";
        NotificationWebSocket.broadcast("update-coordinates");
        return null;
    }

    public void resetCoordinates() {
        coordinates = new Coordinates();
    }

    public void loadPage() {
        Query<Coordinates> query = session.createQuery("FROM Coordinates ", Coordinates.class);
        query.setFirstResult((page - 1) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        allCoordinates = query.getResultList();
        lastPage = allCoordinates.size() < itemsPerPage;
    }

    public void nextPage() {
        if (!lastPage) {
            page++;
            loadPage();
        }
    }

    public void previousPage(){
        if (page > 1){
            page--;
            loadPage();
        }
    }

    public String redirectToEntityPage() {
        if (selectedId != null) {
            return "/info.xhtml?faces-redirect=true&entityId=" + selectedId + "&key=c";
        }
        return null;
    }
}


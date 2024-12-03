package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.websocket.VehicleWebSocket;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Named("coordinatesBean")
@RequestScoped
@Getter
public class CoordinatesBean implements Serializable {

    private Coordinates coordinates = new Coordinates();
    private List<Coordinates> allCoordinates = null;
    private String message;

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

    public Vehicle getVehicleById(Long id) {
        return session.createQuery("from Vehicle where id = :id", Vehicle.class).setParameter("id", id).uniqueResult();
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
}


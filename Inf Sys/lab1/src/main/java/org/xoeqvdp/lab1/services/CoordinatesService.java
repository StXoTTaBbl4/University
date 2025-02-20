package org.xoeqvdp.lab1.services;


import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.xoeqvdp.lab1.beans.AdminBean;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.CoordinatesInteraction;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CoordinatesService {
    private static final Logger logger = Logger.getLogger(CoordinatesService.class.getName());

    public ServiceResult<Coordinates> createCoordinates(Coordinates coordinates , CoordinatesInteraction interaction){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Transaction transaction = session.beginTransaction();
                session.persist(coordinates);
                session.persist(interaction);
                transaction.commit();
                return new ServiceResult<>(null, "Запись сохранена");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<List<Coordinates>> loadPage(int page, int itemsPerPage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Query<Coordinates> query = session.createQuery("FROM Coordinates ", Coordinates.class);
                query.setFirstResult((page - 1) * itemsPerPage);
                query.setMaxResults(itemsPerPage);
                List<Coordinates> allCoordinates = query.getResultList();
                return new ServiceResult<>(allCoordinates, "");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }
}

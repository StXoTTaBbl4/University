package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CoordinatesInfoService {
    private static final Logger logger = Logger.getLogger(CoordinatesInfoService.class.getName());

    public ServiceResult<Coordinates> update(Coordinates coordinates) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Coordinates existingCoordinates = session.find(Coordinates.class, coordinates.getId(), LockMode.PESSIMISTIC_WRITE);
                if (existingCoordinates == null) {
                    return new ServiceResult<>("Запись не найдена");
                }

                session.merge(coordinates);
                transaction.commit();
                NotificationWebSocket.broadcast("update-coordinates");
                return new ServiceResult<>(null, "Запись обновлена");
            } catch (Exception e) {
                transaction.rollback();
                logger.log(Level.SEVERE, "Error updating entity to database", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<String> delete(Coordinates coordinates){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Coordinates existingCoordinates = session.find(Coordinates.class, coordinates.getId(), LockMode.PESSIMISTIC_WRITE);
                if (existingCoordinates == null) {
                    return new ServiceResult<>("Запись не найдена");
                }

                session.remove(existingCoordinates);
                session.flush();
                transaction.commit();
                NotificationWebSocket.broadcast("update-coordinates");
                return new ServiceResult<>("/main.xhtml?faces-redirect=true", "");
            } catch (Exception e) {
                transaction.rollback();
                logger.log(Level.SEVERE, "Error deleting entity from database", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }
}

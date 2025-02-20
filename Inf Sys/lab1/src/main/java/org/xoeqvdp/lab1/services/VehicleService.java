package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.model.VehicleInteraction;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class VehicleService {
    private static final Logger logger = Logger.getLogger(VehicleService.class.getName());

    public ServiceResult<Vehicle> createVehicle(Vehicle vehicle, VehicleInteraction vehicleInteraction, Long coordinatesId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (coordinatesId != null) {
                    Coordinates c = session.find(Coordinates.class, coordinatesId);
                    if (c != null) {
                        vehicle.setCoordinates(c);
                    } else {
                        return new ServiceResult<>("Записи Coordinates с таким ID не существует");
                    }
                }

                // Гыгыговнокод time, потому что, а как еще предотвратить параллельную запись если в БД unique нет?
                session.createNativeMutationQuery("LOCK TABLE vehicles IN EXCLUSIVE MODE ").executeUpdate();

                Long count = session.createQuery("SELECT COUNT(v) FROM Vehicle v WHERE v.name = :uniqueField", Long.class)
                        .setParameter("uniqueField", vehicle.getName())
                        .uniqueResult();

                if (count != 0) {
                    return new ServiceResult<>("Сущность с таким полем Name уже существует");
                }

                session.persist(vehicle);
                session.persist(vehicleInteraction);
                transaction.commit();

                NotificationWebSocket.broadcast("update-vehicle");
                return new ServiceResult<>(null, "");
            } catch (Exception e) {
                transaction.rollback();
                logger.log(Level.SEVERE, "Error executing query", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<List<Vehicle>> loadPage(int page, int itemsPerPage) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Query<Vehicle> query = session.createQuery("FROM Vehicle ", Vehicle.class);
                query.setFirstResult((page - 1) * itemsPerPage);
                query.setMaxResults(itemsPerPage);
                List<Vehicle> vehicles = query.getResultList();
                return new ServiceResult<>(vehicles, "");
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

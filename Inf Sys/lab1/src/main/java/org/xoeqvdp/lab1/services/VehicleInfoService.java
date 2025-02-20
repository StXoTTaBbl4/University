package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.model.VehicleInteraction;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class VehicleInfoService {
    private static final Logger logger = Logger.getLogger(VehicleInfoService.class.getName());

    public ServiceResult<Vehicle> update(Vehicle vehicle, VehicleInteraction vehicleInteraction, Long coordinatesID) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                Coordinates coordinates = null;
                if (coordinatesID != null) {
                    coordinates = session.get(Coordinates.class, coordinatesID);
                    if (coordinates == null) {
                        logger.log(Level.WARNING, "Coordinates record with ID " + coordinatesID + " not found!");
                        transaction.rollback();
                        return new ServiceResult<>("Записи Coordinates с ID " + coordinatesID + " не найдена!");
                    }
                }

                session.merge(vehicleInteraction);
                session.merge(vehicle);
                session.flush();

                transaction.commit();

                NotificationWebSocket.broadcast("update-vehicle");
                return new ServiceResult<>(null, "Данные обновлены");
            } catch (OptimisticLockException e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                logger.warning("Optimistic lock exception: " + e.getMessage());
                return new ServiceResult<>("Данные были изменены другим пользователем. Обновите страницу.");
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                logger.severe("Error updating Vehicle data: " + e.getMessage());
                return new ServiceResult<>("Ошибка при обновлении данных Vehicle");
            }
        } catch (Exception e) {
            logger.severe("Failed to update Vehicle data: " + e.getMessage());
            return new ServiceResult<>("Не удалось обновить данные Vehicle");
        }
    }


    public ServiceResult<Vehicle> delete(Long vehicleId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                // 🔎 Загружаем объект с блокировкой, чтобы избежать конфликтов
                Vehicle vehicle = session.createQuery(
                                "FROM Vehicle v WHERE v.id = :id", Vehicle.class)
                        .setParameter("id", vehicleId)
                        .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                        .uniqueResult();

                if (vehicle != null) {
                    session.remove(vehicle);
                    session.flush();
                    transaction.commit();

                    //Оповещение об обновлении
                    NotificationWebSocket.broadcast("update-vehicle");
                    return new ServiceResult<>(null, "/main.xhtml?faces-redirect=true");
                } else {
                    transaction.rollback();
                    return new ServiceResult<>("Запись не существует");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to delete Vehicle: " + e.getMessage());
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                return new ServiceResult<>("Ошибка при удалении записи.");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process request: " + e.getMessage());
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

}

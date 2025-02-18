package org.xoeqvdp.lab1.services;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.beans.VehicleInfoBean;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.utils.Utility;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VehicleInfoService {
    private static final Logger logger = Logger.getLogger(VehicleInfoBean.class.getName());

//    public ServiceResult<Vehicle> update(Long coordinatesID, Long vehicleId) {
//        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
//            Transaction transaction = session.beginTransaction();
//
//            try {
//                if (coordinatesID != null) {
//                    Coordinates coordinates = session.get(Coordinates.class, coordinatesID);
//                    if (coordinates != null) {
//                        Vehicle vehicle = session.get(Vehicle.class, vehicleId);
//                    } else {
//                        Utility.sendMessage("Записи Coordinates с ID " + coordinatesID + " не найдена!");
//                        logger.log(Level.WARNING, "Coordinates records with ID " + coordinatesID + " not found!");
//                        transaction.rollback();
//                        return;
//                    }
//                }
//
//                vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));
//                vehicleInteraction.setModifier(userBean.getUser());
//
//                session.merge(vehicleInteraction);
//                session.merge(vehicle);
//                session.flush();
//
//                transaction.commit();
//
//                NotificationWebSocket.broadcast("update-vehicle");
//            } catch (Exception e) {
//                if (transaction != null && transaction.getStatus().canRollback()) {
//                    transaction.rollback();
//                }
//                logger.severe("Error updating Vehicle data: " + e.getMessage());
//                Utility.sendMessage("Ошибка при обновлении данных Vehicle");
//            }
//        } catch (Exception e) {
//            logger.severe("Failed to update Vehicle data: " + e.getMessage());
//            Utility.sendMessage("Не удалось обновить данные Vehicle");
//        }
//    }
}

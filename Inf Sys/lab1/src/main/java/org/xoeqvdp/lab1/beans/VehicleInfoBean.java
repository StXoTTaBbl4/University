package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("vehicleInfo")
@ViewScoped
@Getter
public class VehicleInfoBean implements Serializable {
    private static final Logger logger = Logger.getLogger(VehicleInfoBean.class.getName());

    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    @Inject
    private InfoBean infoBean;

    private Vehicle vehicle;
    private VehicleInteraction vehicleInteraction;

    @Setter
    private Long coordinatesID = null;
    private Long original_coordinatesID = null;
    private boolean isEditable = false;
    private String message;

    @PostConstruct
    public void init(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                vehicle = session.createQuery(
                                "from Vehicle where id = :id", Vehicle.class)
                        .setParameter("id", infoBean.getId())
                        .uniqueResult();

                if (vehicle != null) {
                    vehicleInteraction = session.createQuery(
                                    "from VehicleInteraction where vehicle = :vehicle", VehicleInteraction.class)
                            .setParameter("vehicle", vehicle)
                            .getSingleResult();

                    if (vehicle.getCoordinates() != null) {
                        coordinatesID = vehicle.getCoordinates().getId();
                        original_coordinatesID = coordinatesID;
                    }
                } else {
                    logger.log(Level.WARNING,"Vehicle with id " + infoBean.getId() + " not found.");
                    message = "Vehicle с id " + infoBean.getId() + " не найден.";
                    transaction.rollback();
                    return;
                }
                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.log(Level.SEVERE,"Error loading Vehicle data: " + e);
                message = "Ошибка при загрузке данных Vehicle";
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Failed to process request: " + e);
            message = "Не удалось обработать запрос";
        }

        if (userBean.getUser() == null || vehicleInteraction == null) {
            message = "Вы не выполнили вход или у записи отсутствуют данные vehicleInteraction";
            return;
        }

        if (userBean.getUser().getRole() == Roles.ADMIN) {
            isEditable = true;
            return;
        }

        if (Objects.equals(vehicleInteraction.getCreator().getId(), userBean.getUser().getId())) {
            isEditable = true;
        }
    }

    public List<VehicleType> getVehicleTypes() {
        return Arrays.asList(VehicleType.values());
    }

    public List<FuelType> getFuelTypes() {
        return Arrays.asList(FuelType.values());
    }

    public void update() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                if (coordinatesID != null) {
                    Coordinates coordinates = session.get(Coordinates.class, coordinatesID);
                    if (coordinates != null) {
                        vehicle.setCoordinates(coordinates);
                    } else {
                        message = "Записи Coordinates с ID " + coordinatesID + " не найдена!";
                        logger.log(Level.WARNING, "Coordinates records with ID " + coordinatesID + " not found!");
                        transaction.rollback();
                        return;
                    }
                }

                vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));
                vehicleInteraction.setModifier(userBean.getUser());

                session.merge(vehicleInteraction);
                session.merge(vehicle);
                session.flush();

                transaction.commit();

                NotificationWebSocket.broadcast("update-vehicle");
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                logger.severe("Error updating Vehicle data: " + e.getMessage());
                message = "Ошибка при обновлении данных Vehicle";
            }
        } catch (Exception e) {
            logger.severe("Failed to update Vehicle data: " + e.getMessage());
            message = "Не удалось обновить данные Vehicle";
        }
    }

    public String delete(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(vehicle);
                session.flush();
                transaction.commit();
                NotificationWebSocket.broadcast("update-vehicle");
                return "/main.xhtml?faces-redirect=true";
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to delete Vehicle: " + e.getMessage());
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process request: " + e.getMessage());
            return null;
        }
    }

    public String goToCoordinates(){
        if (original_coordinatesID != null) {
            return "/info.xhtml?faces-redirect=true&entityId=" + original_coordinatesID + "&key=c";
        }
        return null;
    }
}

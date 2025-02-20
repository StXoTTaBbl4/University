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
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.services.VehicleInfoService;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
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

    @Inject
    VehicleInfoService vehicleInfoService;

    private Vehicle vehicle;
    private VehicleInteraction vehicleInteraction;

    @Setter
    private Long coordinatesID = null;
    private Long original_coordinatesID = null;
    private boolean isEditable = false;

    @PostConstruct
    public void init(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
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
                    Utility.sendMessage("Vehicle с id " + infoBean.getId() + " не найден.");
                    return;
                }

            } catch (Exception e) {
                logger.log(Level.SEVERE,"Error loading Vehicle data: " + e);
                Utility.sendMessage("Ошибка при загрузке данных Vehicle");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE,"Failed to process request: " + e);
            Utility.sendMessage("Не удалось обработать запрос");
        }

        if (userBean.getUser() == null || vehicleInteraction == null) {
            Utility.sendMessage( "Вы не выполнили вход или у записи отсутствуют данные vehicleInteraction");
            return;
        }

        if (userBean.getUser().getRole() == Roles.ADMIN) {
            isEditable = true;
            return;
        }

        if (vehicleInteraction.getCreator().getId().equals(userBean.getUser().getId())) {
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
        vehicleInteraction.setModifier(userBean.getUser());
        vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));
        ServiceResult<Vehicle> result = vehicleInfoService.update(vehicle, vehicleInteraction, coordinatesID);
        Utility.sendMessage(result.getMessage());
    }

    public String delete(){
        ServiceResult<Vehicle> result = vehicleInfoService.delete(vehicle.getId());
        if (result.isSuccess()){
            return result.getMessage();
        }
        return null;
    }

    public String goToCoordinates(){
        if (original_coordinatesID != null) {
            return "/info.xhtml?faces-redirect=true&entityId=" + original_coordinatesID + "&key=c";
        }
        return null;
    }
}

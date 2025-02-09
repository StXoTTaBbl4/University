package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.services.VehicleService;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

@Named("vehicleBean")
@RequestScoped
@Getter
@Setter
public class VehicleBean implements Serializable {
    private static final Logger logger = Logger.getLogger(VehicleBean.class.getName());

    private Vehicle vehicle = new Vehicle();
    private List<Vehicle> vehicles = null;
    private Long selectedId;

    Long coordinatesId = null;

    private int page = 1;
    private boolean lastPage = false;
    private final int itemsPerPage = 10;

    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    @Inject
    VehicleService vehicleService;

    @PostConstruct
    public void init(){
        loadPage();
    }

    public String createVehicle() {
        if (userBean.getUser() == null || userBean.getUser().getId() == null) {
            Utility.sendMessage("Только авторизованные пользователи могут вносить изменения!");
            return null;
        }

        VehicleInteraction vehicleInteraction = new VehicleInteraction();

        vehicleInteraction.setVehicle(vehicle);
        vehicleInteraction.setCreator(userBean.getUser());
        vehicleInteraction.setModifier(userBean.getUser());
        vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));

        ServiceResult<Vehicle> result = vehicleService.createVehicle(vehicle, vehicleInteraction, coordinatesId);

        if (result.isSuccess()) {
            resetVehicle();
            Utility.sendMessage("Запись успешно сохранена.");
            return null;
        }

        Utility.sendMessage(result.getMessage());
        return null;
    }

    public void loadPage() {
        ServiceResult<List<Vehicle>> result = vehicleService.loadPage(page, itemsPerPage);
        if (result.isSuccess()){
            vehicles = result.getResult();
        }
        lastPage = vehicles.size() < itemsPerPage;
        Utility.sendMessage(result.getMessage());
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

    public List<VehicleType> getVehicleTypes() {
        return Arrays.asList(VehicleType.values());
    }

    public List<FuelType> getFuelTypes() {
        return Arrays.asList(FuelType.values());
    }

    public void resetVehicle() {
        vehicle = new Vehicle();
        Utility.sendMessage("Форма сброшена");
    }

    public String redirectToEntityPage() {
        if (selectedId != null) {
            return "/info.xhtml?faces-redirect=true&entityId=" + selectedId + "&key=v";
        }
        return null;
    }


}

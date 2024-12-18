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
import java.util.Arrays;
import java.util.List;

@Named("vehicleBean")
@RequestScoped
@Getter
public class VehicleBean implements Serializable {

    private Vehicle vehicle = new Vehicle();
    private List<Vehicle> vehicles = null;
    private String message;
    Long coordinatesId = null;

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


    public String createVehicle() {
        if (userBean.getUser() == null || userBean.getUser().getId() == null) {
            message = "Только авторизованные пользователи могут вносить изменения!";
            return null;
        }

        if (coordinatesId != null) {
            Coordinates c = session.find(Coordinates.class, coordinatesId);
            if (c != null) {
                vehicle.setCoordinates(c);
            } else {
                message = "Записи Coordinates с таким ID не существует";
                return null;
            }
        }

        VehicleInteraction vehicleInteraction = new VehicleInteraction();

        vehicleInteraction.setVehicle(vehicle);
        vehicleInteraction.setCreator(userBean.getUser());
        vehicleInteraction.setModifier(userBean.getUser());
        vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));


        Transaction transaction = session.beginTransaction();
        session.persist(vehicle);
        session.persist(vehicleInteraction);
        transaction.commit();

        vehicle = new Vehicle();
        loadAllVehicles();
        VehicleWebSocket.broadcast("update-vehicle");
        message = "Successful";
        return null;
    }

    public Vehicle getVehicleById(Long id) {
        return session.createQuery("from Vehicle where id = :id", Vehicle.class).setParameter("id", id).uniqueResult();
    }

    public boolean updateVehicle(Long userId) {
        session.beginTransaction();
        session.merge(vehicle);
        VehicleWebSocket.broadcast("update-vehicle");
        return true;
    }

    public void loadAllVehicles() {
        vehicles = session.createQuery("from Vehicle", Vehicle.class).getResultList();
    }

    public Vehicle testVehicleAdd(){
        System.out.println("Test Invoked");
        Vehicle vehicle1 = new Vehicle();
        vehicle1.setName("TestCar");
        vehicle1.setCoordinates(null);
        vehicle1.setType(VehicleType.CAR);
        vehicle1.setEnginePower(50);
        vehicle1.setNumberOfWheels(4L);
        vehicle1.setCapacity(600L);
        vehicle1.setDistanceTravelled(40000L);
        vehicle1.setFuelConsumption(20L);
        vehicle1.setFuelType(FuelType.NUCLEAR);

        return vehicle1;
    }

    public void loadPage() {
        Query<Vehicle> query = session.createQuery("FROM Vehicle ", Vehicle.class);
        query.setFirstResult((page - 1) * itemsPerPage);
        query.setMaxResults(itemsPerPage);
        vehicles = query.getResultList();
        lastPage = vehicles.size() < itemsPerPage;
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
    }
}

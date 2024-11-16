package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;

import java.io.Serializable;
import java.util.List;

@Named("vehicleBean")
@SessionScoped
@Getter
public class VehicleBean implements Serializable {

    private Vehicle vehicle = new Vehicle();
    private VehicleInteraction vehicleInteraction = new VehicleInteraction();
    private List<Vehicle> vehicles;

    @Inject
    private UserBean user;

    private final Session session = HibernateUtil.getSessionFactory().openSession();
//    private final Transaction transaction = session.getTransaction();

    public boolean createVehicle(Long userId) {
        Transaction transaction = session.beginTransaction();
        session.persist(vehicle);
        session.persist(vehicleInteraction);
        transaction.commit();
        return true;
    }

    public Vehicle getVehicleById(Long id) {
        return session.createQuery("from Vehicle where id = :id", Vehicle.class).setParameter("id", id).uniqueResult();
    }

    public boolean updateVehicle(Long userId) {
        session.beginTransaction();
        session.merge(vehicle);
        session.merge(vehicleInteraction);
        return true;
    }

    public boolean deleteVehicle(Long id, Long userId) {
        return false;
    }

    public void loadAllVehicles() {
        vehicles = session.createQuery("from Vehicle", Vehicle.class).getResultList();
    }

    public List<Vehicle> getVehiclesByVehicleType(VehicleType vehicleType) {
        return List.of();
    }

    public List<Vehicle> getVehiclesByFuelType(FuelType fuelType) {
        return List.of();
    }

    public List<Vehicle> getVehiclesByEnginePower(Double enginePower) {
        return List.of();
    }

    public List<Vehicle> getVehiclesByNumberOfWheels(Long wheels) {
        return List.of();
    }

    public List<Vehicle> getVehiclesByCapacity(Long capacity) {
        return List.of();
    }

    public List<Vehicle> getVehiclesByFuelConsumption(Long fuelConsumption) {
        return List.of();
    }

}

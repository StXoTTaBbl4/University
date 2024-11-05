package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Named("vehicleBean")
@SessionScoped
public class VehicleBean implements VehicleService, Serializable {

    private Vehicle vehicle = new Vehicle();
    private VehicleInteraction vehicleInteraction = new VehicleInteraction();

    private final Session session = HibernateUtil.getSessionFactory().openSession();
//    private final Transaction transaction = session.getTransaction();

    @Override
    public boolean createVehicle(Long userId) {
        session.beginTransaction();
        session.persist(vehicle);
        session.persist(vehicleInteraction);
        return true;
    }

    @Override
    public Vehicle getVehicleById(Long id) {
        session.beginTransaction();
        return session.createQuery("from Vehicle where id = :id", Vehicle.class).setParameter("id", id).uniqueResult();
    }

    @Override
    public boolean updateVehicle(Long userId) {
        session.beginTransaction();
        session.merge(vehicle);
        session.merge(vehicleInteraction);
        return true;
    }

    @Override
    public boolean deleteVehicle(Long id, Long userId) {
        return false;
    }

    @Override
    public List<Vehicle> getAllVehicles() {
//        transaction.begin();
        session.beginTransaction();
        Query<Vehicle> query = session.createQuery("from Vehicle", Vehicle.class);
        //transaction.commit();
        return query.getResultList();
    }

    @Override
    public List<Vehicle> getVehiclesByVehicleType(VehicleType vehicleType) {
        return List.of();
    }

    @Override
    public List<Vehicle> getVehiclesByFuelType(FuelType fuelType) {
        return List.of();
    }

    @Override
    public List<Vehicle> getVehiclesByEnginePower(Double enginePower) {
        return List.of();
    }

    @Override
    public List<Vehicle> getVehiclesByNumberOfWheels(Long wheels) {
        return List.of();
    }

    @Override
    public List<Vehicle> getVehiclesByCapacity(Long capacity) {
        return List.of();
    }

    @Override
    public List<Vehicle> getVehiclesByFuelConsumption(Long fuelConsumption) {
        return List.of();
    }

    public Vehicle getVehicle() {
        return vehicle;
    }
}

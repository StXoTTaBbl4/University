package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.FuelType;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.model.VehicleType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Named("searchBean")
@SessionScoped
public class SearchBean implements Serializable {
    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    private Vehicle vehicle = new Vehicle();
    private Coordinates coordinates = new Coordinates();

    private List<Vehicle> found_vehicles;
    private List<Coordinates> found_coordinates;

//  Так как в нужном Enum ANY быть не может, приходится костылить через переменные
    private String vehicle_type;
    private String fuel_type;

    public void resetVehicle() {
        vehicle = new Vehicle();
    }

    public void resetCoordinates() {
        coordinates = new Coordinates();
    }

    public void findVehicles(){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Vehicle> cq = cb.createQuery(Vehicle.class);
        Root<Vehicle> root = cq.from(Vehicle.class);
        List<Predicate> predicates = new ArrayList<>();

        if (vehicle.getId() != null) {
            predicates.add(cb.equal(root.get("id"), vehicle.getId()));
        }
        if (vehicle.getName() != null && !vehicle.getName().isEmpty()) {
            predicates.add(cb.equal(root.get("name"), vehicle.getName()));
        }
        if (!vehicle_type.equals("ANY")) {
            predicates.add(cb.equal(root.get("type"), VehicleType.valueOf(vehicle_type)));
        }
        if (vehicle.getEnginePower() != null) {
            predicates.add(cb.equal(root.get("enginePower"), vehicle.getEnginePower()));
        }
        if (vehicle.getNumberOfWheels() != null) {
            predicates.add(cb.equal(root.get("numberOfWheels"), vehicle.getNumberOfWheels()));
        }
        if (vehicle.getCapacity() != null) {
            predicates.add(cb.equal(root.get("capacity"), vehicle.getCapacity()));
        }
        if (vehicle.getDistanceTravelled() != null) {
            predicates.add(cb.equal(root.get("distanceTravelled"), vehicle.getDistanceTravelled()));
        }
        if (vehicle.getFuelConsumption() != null) {
            predicates.add(cb.equal(root.get("fuelConsumption"), vehicle.getFuelConsumption()));
        }
        if (!fuel_type.equals("ANY")) {
            predicates.add(cb.equal(root.get("fuelType"), FuelType.valueOf(fuel_type)));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        found_vehicles = session.createQuery(cq).getResultList();
    }

    public void findCoordinates(){
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Coordinates> cq = cb.createQuery(Coordinates.class);
        Root<Coordinates> root = cq.from(Coordinates.class);
        List<Predicate> predicates = new ArrayList<>();

        if (coordinates.getX() != null) {
            predicates.add(cb.equal(root.get("x"), coordinates.getX()));
        }
        if (coordinates.getY() != null) {
            predicates.add(cb.equal(root.get("y"), coordinates.getY()));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        found_coordinates = session.createQuery(cq).getResultList();
    }

    public List<SelectItem> getVehicleTypes() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("ANY", "ANY"));

        for (VehicleType value : VehicleType.values()) {
            items.add(new SelectItem(value.name(), value.name()));
        }

        return items;
    }

    public List<SelectItem> getFuelTypes() {
        List<SelectItem> items = new ArrayList<>();
        items.add(new SelectItem("ANY", "ANY"));

        for (FuelType value : FuelType.values()) {
            items.add(new SelectItem(value.name(), value.name()));
        }

        return items;
    }

}

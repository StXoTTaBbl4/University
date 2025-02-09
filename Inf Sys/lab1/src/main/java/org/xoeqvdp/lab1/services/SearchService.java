package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.FuelType;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.model.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SearchService {
    private static final Logger logger = Logger.getLogger(SearchService.class.getName());

    public ServiceResult<List<Vehicle>> findVehicles(Vehicle vehicle, String vehicle_type, String fuel_type){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
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
                List<Vehicle> found_vehicles = session.createQuery(cq).getResultList();

                return new ServiceResult<>(found_vehicles, "");

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error updating entity to database", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<List<Coordinates>> findCoordinates(Coordinates coordinates){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
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
                List<Coordinates> found_coordinates = session.createQuery(cq).getResultList();

                return new ServiceResult<>(found_coordinates, "");

            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error updating entity to database", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }
}

package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.FuelType;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.model.VehicleType;
import org.xoeqvdp.lab1.services.SearchService;
import org.xoeqvdp.lab1.services.ServiceResult;

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

    @Inject
    SearchService searchService;

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
        ServiceResult<List<Vehicle>> result = searchService.findVehicles(vehicle, vehicle_type, fuel_type);
        if (result.isSuccess()) {
            found_vehicles = result.getResult();
        }
    }

    public void findCoordinates(){
        ServiceResult<List<Coordinates>> result = searchService.findCoordinates(coordinates);
        if (result.isSuccess()) {
            found_coordinates = result.getResult();
        }
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

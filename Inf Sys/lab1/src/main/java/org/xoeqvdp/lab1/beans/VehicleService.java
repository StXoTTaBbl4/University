package org.xoeqvdp.lab1.beans;

import org.xoeqvdp.lab1.model.Coordinates;
import org.xoeqvdp.lab1.model.FuelType;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.model.VehicleType;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleService {

    boolean createVehicle(Long userId);

    Vehicle getVehicleById(Long id);

    boolean updateVehicle(Long userId);

    boolean deleteVehicle(Long id, Long userId);

    List<Vehicle> getAllVehicles();

    List<Vehicle> getVehiclesByVehicleType(VehicleType vehicleType);

    List<Vehicle> getVehiclesByFuelType(FuelType fuelType);

    List<Vehicle> getVehiclesByEnginePower(Double enginePower);

    List<Vehicle> getVehiclesByNumberOfWheels(Long wheels);

    List<Vehicle> getVehiclesByCapacity(Long capacity);

    List<Vehicle> getVehiclesByFuelConsumption(Long fuelConsumption);
}

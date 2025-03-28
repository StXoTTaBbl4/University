package org.xoeqvdp.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @OneToOne
    @JoinColumn(name = "coordinates_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_vehicle_coordinates"))
    private Coordinates coordinates = null;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType type = null;

    @Column(name = "engine_power", nullable = false)
    @Min(1)
    private Double enginePower;

    @Column(name = "number_of_wheels", nullable = false)
    @Min(1)
    @Max(26)
    private Long numberOfWheels = null;

    @Column(name = "capacity")
    @Min(1)
    private Long capacity = null;

    @Column(name = "distance_travelled", nullable = false)
    @Min(1)
    private Long distanceTravelled;

    @Column(name = "fuel_consumption")
    @Min(1)
    private Long fuelConsumption = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false)
    private FuelType fuelType;

    @Version
    @Column(nullable = false)
    private int version;

    @Override
    public String toString() {
        return "Vehicle{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", type=" + type +
                ", enginePower=" + enginePower +
                ", numberOfWheels=" + numberOfWheels +
                ", capacity=" + capacity +
                ", distanceTravelled=" + distanceTravelled +
                ", fuelConsumption=" + fuelConsumption +
                ", fuelType=" + fuelType +
                ", version=" + version +
                '}';
    }

    public Long getCoordinatesID(){
        if (coordinates != null){
            return coordinates.getId();
        }
        return null;
    }
}



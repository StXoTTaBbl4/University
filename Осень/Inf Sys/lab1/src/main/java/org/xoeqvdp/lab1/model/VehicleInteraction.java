package org.xoeqvdp.lab1.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Timestamp;
import java.time.Instant;

@Entity
@Table(name = "vehicle_interactions")
@Getter
@Setter
public class VehicleInteraction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @NotNull
    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "modified_date", nullable = false)
    private Timestamp modifiedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "modifier_id")
    private User modifier;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    public static VehicleInteraction createVehicleInteraction(Vehicle vehicle, User user) {
        VehicleInteraction vehicleInteraction = new VehicleInteraction();
        vehicleInteraction.setVehicle(vehicle);
        vehicleInteraction.setCreator(user);
        vehicleInteraction.setModifier(user);
        vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));

        return vehicleInteraction;
    }

    @Override
    public String toString() {
        return "VehicleInteraction{" +
                "id=" + id +
                ", creator=" + creator +
                ", modifiedDate=" + modifiedDate +
                ", modifier=" + modifier +
                ", vehicle=" + vehicle +
                '}';
    }
}
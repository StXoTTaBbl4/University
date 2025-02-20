package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.model.file.UploadedFile;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.services.CoordinatesService;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.services.VehicleService;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named
@SessionScoped
@Getter
@Setter
public class CsvUploadBean implements Serializable {

//    private final UserBean userBean;
//    private final VehicleService vehicleService;
//
//    @Inject
//    public CsvUploadBean(UserBean userBean, VehicleService vehicleService){
//        this.userBean = userBean;
//        this.vehicleService = vehicleService;
//    }

    @Inject
    UserBean userBean;

    @Inject
    VehicleService vehicleService;

    @Inject
    CoordinatesService coordinatesService;

    private UploadedFile uploadedFile;

    private String selectedValue;
    private List<String> types = Arrays.asList("Vehicles", "Coordinates");

    @PostConstruct
    public void init(){
        getHistory();
    }

    private ArrayList<FilesHistory> filesHistory;

    public void handleFileUpload() {
        if (uploadedFile == null) {
            Utility.sendMessage("Файла нет");
            return;
        }

        try (InputStream input = uploadedFile.getInputStream();
             Reader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {

            CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setTrim(true)
                    .build());

            if (selectedValue != null && uploadedFile != null) {
                if (selectedValue.equals("Vehicles")) {
                    ArrayList<Vehicle> vehicles = new ArrayList<>();

                    for (CSVRecord record : parser) {
                        try {
                            Vehicle vehicle = parseVehicle(record);
                            vehicles.add(vehicle);
                        } catch (IllegalArgumentException e) {
                            Utility.sendMessage(e.getMessage());
                            return;
                        }
                    }

                    saveToDatabase(vehicles);

                } else {
                    ArrayList<Coordinates> coordinates = new ArrayList<>();

                    for (CSVRecord record : parser) {
                        try {
                            Coordinates c = parseCoordinates(record);
                            coordinates.add(c);
                        } catch (IllegalArgumentException e) {
                            Utility.sendMessage(e.getMessage());
                            return;
                        }
                    }

                    saveToDatabase(coordinates);
                }
            }

        } catch (Exception e) {
            Utility.sendMessage("Ошибка при чтении файла");
        }


    }

    private <T> void saveToDatabase(ArrayList<T> entities) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            if (entities.isEmpty()) {
                reportUpload(-1L, String.valueOf(UploadStatus.FAILURE));
                return;
            }
            if (selectedValue.equals("Vehicles")) {
                for (T entity: entities){
                    Vehicle vehicle = (Vehicle) entity;
                    VehicleInteraction vehicleInteraction = new VehicleInteraction();
                    vehicleInteraction.setVehicle(vehicle);
                    vehicleInteraction.setCreator(userBean.getUser());
                    vehicleInteraction.setModifier(userBean.getUser());
                    vehicleInteraction.setModifiedDate(Timestamp.from(Instant.now()));
                    ServiceResult<Vehicle>  result = vehicleService.createVehicle(vehicle, vehicleInteraction, vehicle.getCoordinatesID());
                    if (!result.isSuccess()) {
                        Utility.sendMessage(result.getMessage());
                        reportUpload(-1L, String.valueOf(UploadStatus.FAILURE));
                        transaction.rollback();
                        return;
                    }
                }
                reportUpload((long) entities.size(), String.valueOf(UploadStatus.SUCCESS));
                session.flush();
                transaction.commit();
            } else {
                for (T entity: entities){
                    Coordinates coordinates = (Coordinates) entity;
                    CoordinatesInteraction coordinatesInteraction = new CoordinatesInteraction();
                    coordinatesInteraction.setCoordinate(coordinates);
                    coordinatesInteraction.setCreator(userBean.getUser());
                    coordinatesInteraction.setModifier(userBean.getUser());
                    coordinatesInteraction.setModifiedDate(Timestamp.from(Instant.now()));

                    ServiceResult<Coordinates> result = coordinatesService.createCoordinates(coordinates, coordinatesInteraction);
                    if (!result.isSuccess()) {
                        Utility.sendMessage(result.getMessage());
                        reportUpload(-1L, String.valueOf(UploadStatus.FAILURE));
                        transaction.rollback();
                        return;
                    }
                }
            }

        } catch (Exception e) {
            Utility.sendMessage(e.getMessage());
            e.printStackTrace();
        }
    }

    private void reportUpload(Long amount, String status){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            FilesHistory filesHistory = new FilesHistory();
            filesHistory.setAmount(amount);
            filesHistory.setStatus(status);
            filesHistory.setInitiator(userBean.getUser());

            session.persist(filesHistory);
            session.flush();
            transaction.commit();
        }catch (Exception e){
            Utility.sendMessage(e.getMessage());
        }
    }

    private Vehicle parseVehicle(CSVRecord record) {
        try {
            String name = getNullableString(record, 0);
            if (name == null || name.isEmpty()) {
                throw new IllegalArgumentException("Поле 'name' не может быть пустым");
            }

            // Координаты (может быть null)
            Coordinates coordinates = null;
            Long coordinates_id = getNullableLong(record, 1, 1L, Long.MAX_VALUE);
            coordinates = findCoordinatesByIdOrNull(coordinates_id);

            // VehicleType (ENUM)
            VehicleType vehicleType = getNullableEnum(record, 2, VehicleType.class);

            // Числовые поля (с валидацией @Min/@Max)
            Double enginePower = getRequiredDouble(record, 3, 1.0, Double.MAX_VALUE);
            Long numberOfWheels = getNullableLong(record, 4, 1L, 26L);
            Long capacity = getNullableLong(record, 5, 1L, Long.MAX_VALUE);
            Long distanceTravelled = getRequiredLong(record, 6, 1L, Long.MAX_VALUE);
            Long fuelConsumption = getNullableLong(record, 7, 1L, Long.MAX_VALUE);

            // FuelType (ENUM, обязательно)
            FuelType fuelType = getRequiredEnum(record, 8, FuelType.class);

            Vehicle vehicle = new Vehicle();

            vehicle.setName(name);
            vehicle.setCoordinates(coordinates);
            vehicle.setType(vehicleType);
            vehicle.setEnginePower(enginePower);
            vehicle.setNumberOfWheels(numberOfWheels);
            vehicle.setCapacity(capacity);
            vehicle.setDistanceTravelled(distanceTravelled);
            vehicle.setFuelConsumption(fuelConsumption);
            vehicle.setFuelType(fuelType);

            return vehicle;
        }catch (Exception e) {
            Utility.sendMessage(e.getMessage());
            throw e;
        }
    }

    private Coordinates parseCoordinates(CSVRecord record){
        try{
            Long x = getRequiredLong(record, 0, -694L, Long.MAX_VALUE);
            Long y = getRequiredLong(record, 1, Long.MIN_VALUE, 999L);

            Coordinates coordinates = new Coordinates();
            coordinates.setX(x);
            coordinates.setY(y);

            return coordinates;
        }catch (Exception e) {
            Utility.sendMessage(e.getMessage());
            throw e;
        }
    }

    // Методы для обработки значений с учетом валидации

    private String getNullableString(CSVRecord record, int column) {
        String value = record.get(column);
        return (value == null || value.isEmpty()) ? null : value.trim();
    }

    private Double getNullableDouble(CSVRecord record, int column) {
        String value = record.get(column);
        return (value == null || value.isEmpty()) ? null : Double.parseDouble(value);
    }

    private Long getNullableLong(CSVRecord record, int column, Long min, Long max) {
        String value = record.get(column);
        if (value == null || value.isEmpty()) return null;

        Long parsed = Long.parseLong(value);
        if (min != null && parsed < min) throw new IllegalArgumentException("Поле '" + column + "' должно быть ≥ " + min);
        if (max != null && parsed > max) throw new IllegalArgumentException("Поле '" + column + "' должно быть ≤ " + max);

        return parsed;
    }

    private Double getRequiredDouble(CSVRecord record, int column, Double min, Double max) {
        Double value = getNullableDouble(record, column);
        if (value == null) throw new IllegalArgumentException("Поле '" + column + "' обязательно");
        if (min != null && value < min) throw new IllegalArgumentException("Поле '" + column + "' должно быть ≥ " + min);
        if (max != null && value > max) throw new IllegalArgumentException("Поле '" + column + "' должно быть ≤ " + max);
        return value;
    }

    private Long getRequiredLong(CSVRecord record, int column, Long min, Long max) {
        Long value = getNullableLong(record, column, min, max);
        if (value == null) throw new IllegalArgumentException("Поле '" + column + "' обязательно");
        return value;
    }

    private <T extends Enum<T>> T getNullableEnum(CSVRecord record, int column, Class<T> enumClass) {
        String value = getNullableString(record, column);
        if (value == null) return null;

        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Type " + value + "not found in " + enumClass.getName()); // Если значение не соответствует Enum, возвращаем null
        }
    }

    private <T extends Enum<T>> T getRequiredEnum(CSVRecord record, int column, Class<T> enumClass) {
        T value = getNullableEnum(record, column, enumClass);
        if (value == null) throw new IllegalArgumentException("Поле '" + column + "' обязательно");
        return value;
    }

    private Coordinates findCoordinatesByIdOrNull(Long id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.find(Coordinates.class, id);
        } catch (NoResultException e) {
            return null;
        }
    }

    public void getHistory(){
        if (userBean.getUser() == null) {
            return;
        }
        try (Session session =  HibernateUtil.getSessionFactory().openSession()) {
            if (userBean.getUser().getRole().equals(Roles.ADMIN)) {
                filesHistory = (ArrayList<FilesHistory>) session.createQuery("SELECT v FROM FilesHistory v", FilesHistory.class).getResultList();
            } else {
                filesHistory = (ArrayList<FilesHistory>) session.createQuery("SELECT v FROM FilesHistory v where initiator=:user", FilesHistory.class).
                        setParameter("user", userBean.getUser()).
                        getResultList();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

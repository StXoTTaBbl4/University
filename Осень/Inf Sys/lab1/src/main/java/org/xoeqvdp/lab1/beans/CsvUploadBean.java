package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import jakarta.persistence.NoResultException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.hibernate.Session;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.minio.MinioStorageService;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.services.CsvUploadService;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Slf4j
@Named
@SessionScoped
@Getter
@Setter
public class CsvUploadBean implements Serializable {
    private static final Logger logger = Logger.getLogger(CsvUploadBean.class.getName());

    @Inject
    UserBean userBean;

    @Inject
    CsvUploadService csvUploadService;

    private UploadedFile uploadedFile;

    private String selectedValue;
    private List<String> types = Arrays.asList("Vehicles", "Coordinates");

    @Inject
    private MinioStorageService minioStorageService;

    @PostConstruct
    public void init(){
        getHistory();
    }

    private ArrayList<FilesHistory> filesHistory;

    public void handleFileUpload() {
        System.out.println("File check");
        if (uploadedFile == null) {
            Utility.sendMessage("Файла нет");
            return;
        }
        System.out.println("Checked");

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
                    System.out.println("Vehicles parsed");
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

    private <T> void saveToDatabase(ArrayList<T> entities) throws IOException {
       csvUploadService.saveToDatabase(entities,
               selectedValue,
               uploadedFile.getFileName(),
               uploadedFile.getInputStream(),
               uploadedFile.getSize(),
               uploadedFile.getContentType());
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
        ServiceResult<ArrayList<FilesHistory>> result = csvUploadService.getHistory();
        if (result.isSuccess()) {
            filesHistory = result.getResult();
        } else {
            filesHistory = result.getResult();
            Utility.sendMessage(result.getMessage());
        }

    }

    public StreamedContent downloadFile(FilesHistory file) {
        if (!"SUCCESS".equals(file.getStatus())) {
            return null;
        }

        String fileName = file.getFileName();
        InputStream fileStream;

        try {
            fileStream = minioStorageService.downloadFile(file.getInitiator().getId().toString(), fileName);

            return DefaultStreamedContent.builder()
                    .name(fileName)
                    .contentType("text/csv")
                    .stream(() -> fileStream) // Поток не закрываем
                    .build();
        } catch (Exception e) {
            Utility.sendMessage("Failed to download");
            logger.log(Level.WARNING, "Failed to download file from MinIO, fileName: " + file.getFileName(), e);
            return null;
        }
    }
}

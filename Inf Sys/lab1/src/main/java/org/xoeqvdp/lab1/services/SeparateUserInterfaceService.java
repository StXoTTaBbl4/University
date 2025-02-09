package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.beans.SeparateUserInterface;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class SeparateUserInterfaceService {
    private static final Logger logger = Logger.getLogger(SeparateUserInterface.class.getName());
    public ServiceResult<Vehicle> first(FuelType fuelType, User user){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                if (user.getId() == null) {
                    return new ServiceResult<>("Неавторизованные пользователи не могут изменять базу данных.");
                }

                if (user.getRole() == Roles.ADMIN){
                    session.createMutationQuery("DELETE FROM Vehicle WHERE fuelType = :fuelType").setParameter("fuelType", fuelType).executeUpdate();
                } else {
                    session.createMutationQuery(
                                    "DELETE FROM Vehicle v " +
                                            "WHERE v.id IN (" +
                                            "   SELECT vi.vehicle.id FROM VehicleInteraction vi " +
                                            "   WHERE vi.creator.id = :creatorId" +
                                            ") " +
                                            "AND v.fuelType = :fuelType").
                            setParameter("creatorId", user.getId()).
                            setParameter("fuelType", fuelType).
                            executeUpdate();
                }
                transaction.commit();
                NotificationWebSocket.broadcast("update-vehicle");
                return new ServiceResult<>(null,"Записи успешно удалены");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<Vehicle> second(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                Vehicle result = session.createQuery("FROM Vehicle WHERE name = (SELECT MAX(name) FROM Vehicle)", Vehicle.class).setMaxResults(1).getSingleResultOrNull();
                System.out.println(result);
                if (result == null){
                    return new ServiceResult<>("Подходящих записей не найдено!");
                }
                return new ServiceResult<>(result, "");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<String> third(Long fuelConsumption){
        if (fuelConsumption != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                try {
                    List<Vehicle> result = session.createQuery("FROM Vehicle WHERE fuelConsumption > :fuelConsumption", Vehicle.class).setParameter("fuelConsumption", fuelConsumption).getResultList();
                    if (result.isEmpty()){
                        return new ServiceResult<>(null,  "Ничего не найдено");
                    }
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Vehicle vehicle : result) {
                        stringBuilder.append(vehicle.getId());
                        stringBuilder.append(" ");
                    }
                    return new ServiceResult<>(stringBuilder.toString(), "");
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error executing query", e);
                    return new ServiceResult<>("Ошибка при обращении к БД");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                return new ServiceResult<>("Ошибка при попытке подключения к БД");
            }
        }
        return new ServiceResult<>("Параметр fuelConsumption не определён");
    }

    public ServiceResult<Vehicle> fourth(Long id, User user){
        if (id != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    Vehicle result = session.createQuery("FROM Vehicle WHERE id = :id", Vehicle.class).
                            setParameter("id", id).
                            uniqueResultOptional().
                            orElse(null);

                    if (result == null) {
                        return new ServiceResult<>("По данному ID не найдено сущности");
                    }

                    ServiceResult<VehicleInteraction> vehicleInteractionServiceResult = validateOwnership(result, user);
                    if (!vehicleInteractionServiceResult.isSuccess()){
                        return new ServiceResult<>(vehicleInteractionServiceResult.getMessage());
                    }

                    VehicleInteraction vehicleInteraction = vehicleInteractionServiceResult.getResult();

                    result.setDistanceTravelled(1L);
                    vehicleInteraction.setModifier(user);
                    session.merge(result);
                    session.merge(vehicleInteraction);
                    session.flush();
                    transaction.commit();
                    NotificationWebSocket.broadcast("update-vehicle");
                    return new ServiceResult<>(null, "Изменения применены к записи с id " + result.getId());
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    return new ServiceResult<>("Ошибка при обращении к БД");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                return new ServiceResult<>("Ошибка при попытке подключения к БД");
            }
        }
        return new ServiceResult<>("Параметр id не определён");
    }

    public ServiceResult<Vehicle> fifth(Long id, Long numberOfWheels, User user){
        if (id != null && numberOfWheels != null ) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    Vehicle result = session.createQuery("FROM Vehicle WHERE id = :id", Vehicle.class).setParameter("id", id).uniqueResultOptional().orElse(null);
                    if (result == null) {
                        return new ServiceResult<>("По данному ID не найдено сущности");
                    }

                    ServiceResult<VehicleInteraction> vehicleInteractionServiceResult = validateOwnership(result, user);
                    if (!vehicleInteractionServiceResult.isSuccess()){
                        return new ServiceResult<>(vehicleInteractionServiceResult.getMessage());
                    }

                    VehicleInteraction vehicleInteraction = vehicleInteractionServiceResult.getResult();

                    result.setNumberOfWheels(result.getNumberOfWheels() + numberOfWheels);
                    vehicleInteraction.setModifier(user);
                    session.merge(result);
                    session.merge(vehicleInteraction);
                    session.flush();
                    transaction.commit();
                    NotificationWebSocket.broadcast("update-vehicle");
                    return new ServiceResult<>(null, "Теперь у машины с id " + result.getId() + " кол-во колёс: " + result.getNumberOfWheels());
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    return new ServiceResult<>("Ошибка при обращении к БД");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                return new ServiceResult<>("Ошибка при попытке подключения к БД");
            }
        }
        return new ServiceResult<>("Параметр fuelConsumption не определён");
    }

    private ServiceResult<VehicleInteraction> validateOwnership(Vehicle result, User user){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            VehicleInteraction vehicleInteraction = session.createQuery("FROM VehicleInteraction where vehicle = :vehicle", VehicleInteraction.class).
                    setParameter("vehicle", result).
                    uniqueResultOptional().
                    orElse(null);

            if (vehicleInteraction == null) {
                return new ServiceResult<>("Запись о принадлежности сущности не найдена");
            }

            if (user.getRole() != Roles.ADMIN) {
                if (!vehicleInteraction.getCreator().getId().equals(user.getId())) {
                    return new ServiceResult<>("Данная запись не принадлежит вам.");
                }
            }

            return new ServiceResult<>(vehicleInteraction, "");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }
}

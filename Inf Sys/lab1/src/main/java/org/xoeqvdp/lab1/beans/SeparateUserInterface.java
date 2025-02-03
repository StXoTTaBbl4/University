package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.FuelType;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
@Getter
@Setter
public class SeparateUserInterface {
    private static final Logger logger = Logger.getLogger(SeparateUserInterface.class.getName());

    private String message;

    @Inject
    UserBean userBean;

    private Vehicle result;

    private FuelType fuelType;
    private Long fuelConsumption = null;
    private Long fourth_id = null;
    private Long fifth_id = null;
    private Long numberOfWheels = null;

    public void first(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.createMutationQuery("DELETE FROM Vehicle WHERE fuelType = :fuelType").setParameter("fuelType", fuelType).executeUpdate();
                transaction.commit();
                message = "Записи успешно удалены";
                NotificationWebSocket.broadcast("update-vehicle");
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                message = "Ошибка при обращении к БД";
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }
    }

    public void second(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                result = session.createQuery("FROM Vehicle WHERE name = (SELECT MAX(name) FROM Vehicle)", Vehicle.class).setMaxResults(1).getSingleResultOrNull();
                message = result.toString();
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                message = "Ошибка при обращении к БД";
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            message = "Ошибка при попытке подключения к БД";
        }
    }

    public void third(){
        if (fuelConsumption != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                try {
                    List<Vehicle> result = session.createQuery("FROM Vehicle WHERE fuelConsumption > :fuelConsumption", Vehicle.class).setParameter("fuelConsumption", fuelConsumption).getResultList();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Vehicle vehicle : result) {
                        stringBuilder.append(vehicle.getId());
                        stringBuilder.append(" ");
                    }
                    message = stringBuilder.toString();
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error executing query", e);
                    message = "Ошибка при обращении к БД";
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                message = "Ошибка при попытке подключения к БД";
            }
        }
    }

    public void fourth(){
        if (fourth_id != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    result = session.createQuery("FROM Vehicle WHERE id = :id", Vehicle.class).setParameter("id", fourth_id).uniqueResultOptional().orElse(null);
                    if (result != null) {
                        result.setDistanceTravelled(1L);
                        session.merge(result);
                        session.flush();
                        transaction.commit();
                        message = "Изменения применены к записи с id " + result.getId();
                        NotificationWebSocket.broadcast("update-vehicle");
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    message = "Ошибка при обращении к БД";
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                message = "Ошибка при попытке подключения к БД";
            }
        }
    }

    public void fifth(){
        if (fifth_id != null) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    result = session.createQuery("FROM Vehicle WHERE id = :id", Vehicle.class).setParameter("id", fifth_id).uniqueResultOptional().orElse(null);
                    System.out.println("Fifth: ");
                    System.out.println(result);
                    if (result != null) {
                        result.setNumberOfWheels(result.getNumberOfWheels() + numberOfWheels);
                        session.merge(result);
                        session.flush();
                        transaction.commit();
                        message = "Теперь у машины с id " + result.getId() + " кол-во колёс: " + result.getNumberOfWheels();
                        NotificationWebSocket.broadcast("update-vehicle");
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    message = "Ошибка при обращении к БД";
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                message = "Ошибка при попытке подключения к БД";
            }
        }
    }

    public List<FuelType> getFuelTypes() {
        return Arrays.asList(FuelType.values());
    }
}

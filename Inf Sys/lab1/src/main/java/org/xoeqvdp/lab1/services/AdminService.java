package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.LockModeType;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.AdminRequest;
import org.xoeqvdp.lab1.model.Roles;
import org.xoeqvdp.lab1.model.User;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class AdminService {
    private static final Logger logger = Logger.getLogger(AdminService.class.getName());

    public ServiceResult<List<AdminRequest>> getRequests() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            try {
                List<AdminRequest> result = session.createQuery("SELECT r FROM AdminRequest r", AdminRequest.class).getResultList();
                if (result.isEmpty()) {
                    return new ServiceResult<>("Записи не найдены");
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

    public ServiceResult<AdminRequest> acceptRequest(Long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AdminRequest request = session.find(AdminRequest.class, id);
            if (request != null) {
                Transaction transaction = session.beginTransaction();
                try {
                    AdminRequest adminRequest = session.createQuery("from AdminRequest where id=:id", AdminRequest.class).
                            setParameter("id", id).
                            setLockMode(LockModeType.PESSIMISTIC_WRITE).
                            uniqueResultOptional().
                            orElse(null);

                    if (adminRequest != null) {
                        User user = session.createQuery("from User where id=:id", User.class).
                                setParameter("id",adminRequest.getUser().getId()).
                                setLockMode(LockModeType.PESSIMISTIC_WRITE).
                                uniqueResultOptional().orElse(null);
                        if (user == null) {
                            transaction.rollback();
                            return new ServiceResult<>("Не найдена запись о пользователе или о запросе");
                        }
                        user.setRole(Roles.ADMIN);
                        session.merge(user);
                        session.remove(adminRequest);
                        session.flush();
                        transaction.commit();
                        return new ServiceResult<>(null, "Роль пользователя обновлена");
                    } else {
                        transaction.rollback();
                        logger.log(Level.WARNING, "User or admin request not found, idi ispravlay");
                        return new ServiceResult<>("Не найдена запись о пользователе или о запросе");
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    return new ServiceResult<>("Ошибка при обращении к БД");
                }
            }

            return new ServiceResult<>("Запрос с данным id: " + id +" не найден");
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }

    }

    public  ServiceResult<AdminRequest> rejectRequest(Long id){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            AdminRequest request = session.find(AdminRequest.class, id);
            if (request != null) {
                Transaction transaction = session.beginTransaction();
                try {
                    AdminRequest adminRequest = session.createQuery("from AdminRequest where id=:id", AdminRequest.class).
                            setParameter("id", id).
                            setLockMode(LockModeType.PESSIMISTIC_WRITE).
                            uniqueResultOptional().
                            orElse(null);
                    if (adminRequest != null) {
                        session.remove(adminRequest);
                        transaction.commit();
                        return new ServiceResult<>(null, "Заявка отклонена");
                    } else {
                        transaction.rollback();
                        logger.log(Level.WARNING, "User or admin request not found, idi ispravlay");
                        return new ServiceResult<>("Не найдена запись о пользователе или о запросе");
                    }
                } catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error executing query", e);
                    return new ServiceResult<>("Ошибка при обращении к БД");
                }
            }
            return new ServiceResult<>("Запрос с данным id: " + id +" не найден");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<Long> getAdminRequests(){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Long result = session.createQuery("select count(*) from AdminRequest", Long.class).uniqueResult();
                return new ServiceResult<>(result, "");
            } catch (Exception e){
                transaction.rollback();
                logger.log(Level.SEVERE, "Error while making request", e);
                return new ServiceResult<>("Ошибка при обращении к БД");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }
}
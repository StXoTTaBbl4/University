package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.AdminRequest;
import org.xoeqvdp.lab1.model.Roles;
import org.xoeqvdp.lab1.model.User;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    public ServiceResult<User> registerUser(User new_user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(new_user);
                session.flush();
                transaction.commit();
                return new ServiceResult<>(new_user, "Пользователь добавлен, выполните вход");
            } catch (ConstraintViolationException e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                logger.warning("User with that username already exists: " + new_user.getUsername());
                return new ServiceResult<>("Такой пользователь уже существует!");
            } catch (Exception e) {
                if (transaction != null && transaction.getStatus().canRollback()) {
                    transaction.rollback();
                }
                logger.log(Level.SEVERE, "Error adding entity to database", e);
                return new ServiceResult<>("Ошибка при создании пользователя");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<User> loginUser(User user) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                user = session.createQuery("from User where username = :username and passwordHash = :password", User.class)
                        .setParameter("username", user.getUsername())
                        .setParameter("password",user.getPasswordHash())
                        .uniqueResult();
                if (user == null) {
                    return new ServiceResult<>("Проверьте логин или пароль");
                }
                return new ServiceResult<>(user, "main.xhtml?faces-redirect=true");
            } catch (Exception e){
                transaction.rollback();
                logger.log(Level.SEVERE, "Error adding entity to database", e);
                return new ServiceResult<>("Ошибка при поиске пользователя");
            }
        } catch (Exception e){
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
            return new ServiceResult<>("Ошибка при попытке подключения к БД");
        }
    }

    public ServiceResult<User> sendAdminRequest(User user) {
        if (user.getId() != null && user.getRole() != Roles.ADMIN) {
            try (Session session = HibernateUtil.getSessionFactory().openSession()) {
                Transaction transaction = session.beginTransaction();
                try {
                    if (!session.createQuery("FROM AdminRequest where user = :user", AdminRequest.class).setParameter("user", user).getResultList().isEmpty()) {
                        return new ServiceResult<>("Вы уже отправили заявку.");
                    }
                    AdminRequest adminRequest = new AdminRequest();
                    adminRequest.setUser(user);
                    session.persist(adminRequest);
                    session.flush();
                    transaction.commit();
                    return new ServiceResult<>(user,"Заявка отправилена");
                }
                catch (ConstraintViolationException e) {
                    if (transaction != null && transaction.getStatus().canRollback()) {
                        transaction.rollback();
                    }
                    logger.warning("Request already sent.");
                    return new ServiceResult<>("Заявка уже отправлена");
                }
                catch (Exception e) {
                    transaction.rollback();
                    logger.log(Level.SEVERE, "Error adding entity to database", e);
                    return new ServiceResult<>("Ошибка при создании пользователя");
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error managing Hibernate session", e);
                return new ServiceResult<>("Ошибка при попытке подключения к БД");
            }
        }
        return new ServiceResult<>("Либо вы не авторизованы, либи вы уже админ.");
    }

}

package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.User;

import java.io.Serializable;

@Getter
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable{
    private User user = new User();
    private String message = "";
    private Long userID = null;

    FacesContext fCtx = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
    String sessionID = session.getId();

    private final Session dbSession = HibernateUtil.getSessionFactory().openSession();

    public String registerUser() {

        User buff = dbSession.createQuery("from User where username = :username", User.class).setParameter("username", user.getUsername()).uniqueResult();

        if (buff != null){
            message = "Пользователь с таким именем уже существует!";
            return null;
        }

        buff = dbSession.createQuery("from User where passwordHash = :pwd", User.class).setParameter("pwd", user.getPasswordHash()).uniqueResult();
        if (buff != null) {
            message = "Пользователь с таким паролем уже существует!";
            return null;
        }

        Transaction transaction = dbSession.beginTransaction();
        dbSession.persist(user);
        transaction.commit();
        message = "Пользователь добавлен, выполните вход";
        return null;
    }

    public String loginUser() {
        User buff = dbSession.createQuery("from User where username = :username and passwordHash = :password", User.class)
                .setParameter("username", user.getUsername())
                .setParameter("password",user.getPasswordHash())
                .uniqueResult();
        if (buff != null){
            userID = buff.getId();
            return "main.xhtml?faces-redirect=true";
        }
        message = "Проверьте правильность логина и пароля";
        return null;
    }

    public String logoutUser(){
        userID = null;
        user = new User();
        message = "Выход выполнен!";
        return "auth.xhtml?faces-redirect=true";
    }

}

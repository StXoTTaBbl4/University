package org.xoeqvdp.lab1.beans;

import jakarta.annotation.ManagedBean;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import org.hibernate.Session;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.User;

import java.io.Serializable;

@Getter
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable, UserService {
    private User user = new User();
    private String message = "";

//    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Override
    public String registerUser() {
        System.out.println("=============");
        System.out.println(user);
        message = "Введите логин!";
        return null;
    }

    @Override
    public String loginUser() {
        System.out.println(user.getUsername());
        System.out.println(user.getPasswordHash());
        message = "Проверьте правильность логина или пароля";
        return null;
    }

}

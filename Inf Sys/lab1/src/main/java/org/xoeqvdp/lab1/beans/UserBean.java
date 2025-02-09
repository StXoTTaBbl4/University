package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.Setter;
import org.xoeqvdp.lab1.model.Roles;
import org.xoeqvdp.lab1.model.User;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.services.UserService;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.Serializable;
import java.util.logging.Logger;

@Getter
@Setter
@Named("userBean")
@SessionScoped
public class UserBean implements Serializable {
    private static final Logger logger = Logger.getLogger(UserBean.class.getName());

    @Inject
    UserService userService;

    private User user = new User();
    private User new_user = new User();
    private boolean loggedIn = false;

    FacesContext fCtx = FacesContext.getCurrentInstance();
    HttpSession session = (HttpSession) fCtx.getExternalContext().getSession(false);
    String sessionID = session.getId();

    public String  registerUser() {
        ServiceResult<User> result = userService.registerUser(new_user);
        if (result.isSuccess()){
            user = result.getResult();
            new_user = new User();
            Utility.sendMessage(result.getMessage());
            return null;
        }

        Utility.sendMessage(result.getMessage());
        return null;
    }

    public String loginUser() {
        ServiceResult<User> result = userService.loginUser(user);
        if (result.isSuccess()){
            user = result.getResult();
            loggedIn = true;
            return result.getMessage();
        }
        Utility.sendMessage(result.getMessage());
        return "";
    }

    public String logoutUser() {
        user = new User();
        loggedIn = false;
        Utility.sendMessage("Выход выполнен!");
        return "auth.xhtml?faces-redirect=true";
    }

    public boolean isAdmin(){
        if (user != null){
            return user.getRole() == Roles.ADMIN;
        }
        return false;
    }

    public void sendAdminRequest() {
        ServiceResult<User> result = userService.sendAdminRequest(user);
        Utility.sendMessage(result.getMessage());
    }
}

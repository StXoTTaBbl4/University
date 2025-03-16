package org.xoeqvdp.lab1.utils;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class Utility {
    public static void sendMessage(String message){
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, message, ""));
    }
}

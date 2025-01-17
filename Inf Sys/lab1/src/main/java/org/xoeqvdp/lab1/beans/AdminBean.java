package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named
@ViewScoped
public class AdminBean implements Serializable {

    @Inject
    UserBean userBean;

    @PostConstruct
    public void init(){
    }
}

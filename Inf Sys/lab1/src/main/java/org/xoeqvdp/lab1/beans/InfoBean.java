package org.xoeqvdp.lab1.beans;

import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Named("info")
@ViewScoped
@Getter
@Setter
public class InfoBean implements Serializable {
    @ManagedProperty("#{param.entityId}")
    private Long id;
    @ManagedProperty("#{param.key}")
    private Character key;
    private String template;

    public void init(){
        if (key.equals('v')){
            template = "WEB-INF/templates/info/vehicle-info.xhtml";
            return;
        }
        if (key.equals('c')){
            template = "WEB-INF/templates/info/coordinates-info.xhtml";
            return;
        }
        template = "WEB-INF/templates/info/placeholder-info.xhtml";
    }

}

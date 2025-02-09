package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.services.CoordinatesInfoService;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named("coordinatesInfo")
@ViewScoped
@Getter
public class CoordinatesInfoBean implements Serializable {
    private static final Logger logger = Logger.getLogger(CoordinatesInfoBean.class.getName());

    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    @Inject
    private InfoBean infoBean;

    @Inject
    CoordinatesInfoService coordinatesInfoService;

    private Coordinates original_coordinates;
    private Coordinates coordinates;
    private CoordinatesInteraction coordinatesInteraction;
    private boolean isEditable = false;

    @PostConstruct
    public void init() {
        try {
            Transaction transaction = session.beginTransaction();
            original_coordinates = session.createQuery("from Coordinates where id = :id", Coordinates.class)
                    .setParameter("id", infoBean.getId())
                    .uniqueResult();

            coordinatesInteraction = session.createQuery("from CoordinatesInteraction where coordinate = :coordinates", CoordinatesInteraction.class)
                    .setParameter("coordinates", original_coordinates)
                    .getSingleResult();

            transaction.commit();
            coordinates = original_coordinates;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "CoordinatesInfoBean init(): db interaction error", e);
        }

        if (userBean.getUser() == null || coordinatesInteraction == null) {
            return;
        }

        if (userBean.getUser().getRole() == Roles.ADMIN) {
            isEditable = true;
            return;
        }

        if (Objects.equals(coordinatesInteraction.getCreator().getId(), userBean.getUser().getId())) {
            isEditable = true;
        }

    }

    public void update() {
        ServiceResult<Coordinates> result = coordinatesInfoService.update(coordinates);
        Utility.sendMessage(result.getMessage());
    }

    public void reset() {
        init();
    }

    public String delete(){
        ServiceResult<String> result = coordinatesInfoService.delete(coordinates);
        if (result.isSuccess()){
            return result.getResult();
        }
        return null;
    }
}

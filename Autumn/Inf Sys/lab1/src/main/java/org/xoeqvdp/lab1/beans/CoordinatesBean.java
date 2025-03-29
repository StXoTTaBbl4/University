package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.services.CoordinatesService;
import org.xoeqvdp.lab1.utils.Utility;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Named("coordinatesBean")
@RequestScoped
@Getter
@Setter
public class CoordinatesBean implements Serializable {

    private Coordinates coordinates = new Coordinates();
    private List<Coordinates> allCoordinates = null;
    private Long selectedId;

    private int page = 1;
    private boolean lastPage = false;
    private final int itemsPerPage = 10;

    private final Session session = HibernateUtil.getSessionFactory().openSession();

    @Inject
    private UserBean userBean;

    @Inject
    CoordinatesService coordinatesService;

    @PostConstruct
    public void init(){
        loadPage();
    }

    public void createCoordinates(){
        if (userBean.getUser() == null || userBean.getUser().getId() == null) {
            Utility.sendMessage("Только авторизованные пользователи могут вносить изменения!");
            return;
        }

        CoordinatesInteraction interaction = new CoordinatesInteraction();
        interaction.setCreator(userBean.getUser());
        interaction.setCoordinate(coordinates);
        interaction.setModifier(userBean.getUser());
        interaction.setModifiedDate(Timestamp.from(Instant.now()));

        ServiceResult<Coordinates> result = coordinatesService.createCoordinates(coordinates, interaction);

        if (result.isSuccess()) {
            NotificationWebSocket.broadcast("update-coordinates");
            resetCoordinates();
        }
        Utility.sendMessage(result.getMessage());

    }

    public void resetCoordinates() {
        coordinates = new Coordinates();
    }

    public void loadPage() {
        ServiceResult<List<Coordinates>> result = coordinatesService.loadPage(page, itemsPerPage);
        if (result.isSuccess()){
            allCoordinates = result.getResult();
        }
        lastPage = allCoordinates.size() < itemsPerPage;
    }

    public void nextPage() {
        if (!lastPage) {
            page++;
            loadPage();
        }
    }

    public void previousPage(){
        if (page > 1){
            page--;
            loadPage();
        }
    }

    public String redirectToEntityPage() {
        if (selectedId != null) {
            return "/info.xhtml?faces-redirect=true&entityId=" + selectedId + "&key=c";
        }
        return null;
    }
}


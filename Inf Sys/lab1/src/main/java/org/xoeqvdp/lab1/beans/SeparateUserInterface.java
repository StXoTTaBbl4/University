package org.xoeqvdp.lab1.beans;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.xoeqvdp.lab1.model.FuelType;
import org.xoeqvdp.lab1.model.Vehicle;
import org.xoeqvdp.lab1.services.SeparateUserInterfaceService;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.utils.Utility;

import java.util.Arrays;
import java.util.List;

@Named
@RequestScoped
@Getter
@Setter
public class SeparateUserInterface {

    @Inject
    UserBean userBean;

    @Inject
    SeparateUserInterfaceService separateUserInterfaceService;

    private Vehicle result;

    private FuelType fuelType;
    private Long fuelConsumption = null;
    private Long fourth_id = null;
    private Long fifth_id = null;
    private Long numberOfWheels = null;

    public void first() {
        if (userBean.isLoggedIn()) {
            ServiceResult<Vehicle> result = separateUserInterfaceService.first(fuelType, userBean.getUser());
            Utility.sendMessage(result.getMessage());
            return;
        }
        Utility.sendMessage("Для выполнения этого запроса нужно авторизоваться.");
    }

    public void second(){
        ServiceResult<Vehicle> result = separateUserInterfaceService.second();
        if (result.isSuccess()) {
            Utility.sendMessage("ID: " + result.getResult().getId() + ", name: " + result.getResult().getName());
            return;
        }
        Utility.sendMessage(result.getMessage());

    }

    public void third(){
        ServiceResult<String> result = separateUserInterfaceService.third(fuelConsumption);
        if (result.isSuccess()) {
            Utility.sendMessage(result.getResult());
            return;
        }
        Utility.sendMessage(result.getMessage());
    }

    public void fourth(){
        if (userBean.isLoggedIn()) {
            ServiceResult<Vehicle> result = separateUserInterfaceService.fourth(fourth_id, userBean.getUser());
            Utility.sendMessage(result.getMessage());
            return;
        }

        Utility.sendMessage("Для выполнения этого запроса нужно авторизоваться.");
    }

    public void fifth(){
        if (userBean.isLoggedIn()) {
            ServiceResult<Vehicle> result = separateUserInterfaceService.fifth(fifth_id, numberOfWheels, userBean.getUser());
            Utility.sendMessage(result.getMessage());
            return;
        }
        Utility.sendMessage("Для выполнения этого запроса нужно авторизоваться.");
    }

    public List<FuelType> getFuelTypes() {
        return Arrays.asList(FuelType.values());
    }
}

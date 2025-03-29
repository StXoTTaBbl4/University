package org.xoeqvdp.lab1.beans;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.xoeqvdp.lab1.model.AdminRequest;
import org.xoeqvdp.lab1.services.AdminService;
import org.xoeqvdp.lab1.services.ServiceResult;
import org.xoeqvdp.lab1.utils.Utility;
import org.xoeqvdp.lab1.websocket.NotificationWebSocket;

import java.io.Serializable;
import java.util.List;

@Named
@RequestScoped
public class AdminBean implements Serializable {

    private List<AdminRequest> requests;

    @Inject
    AdminService adminService;

    @PostConstruct
    public void init(){
        requests = getRequests();
    }

    public List<AdminRequest> getRequests() {
        ServiceResult<List<AdminRequest>> result = adminService.getRequests();
        if (result.isSuccess()){
            return result.getResult();
        }
        Utility.sendMessage(result.getMessage());
        return null;
    }

    public void acceptRequest(Long id) {
        ServiceResult<AdminRequest> result = adminService.acceptRequest(id);
        if (result.isSuccess()) {
            requests = getRequests();
            Utility.sendMessage(result.getMessage());
            NotificationWebSocket.broadcast("update-admin");
            return;
        }
        Utility.sendMessage(result.getMessage());
    }

    public void rejectRequest(Long id) {
        ServiceResult<AdminRequest> result = adminService.rejectRequest(id);
        if (result.isSuccess()){
            requests = getRequests();
            Utility.sendMessage(result.getMessage());
            NotificationWebSocket.broadcast("update-admin");
            return;
        }
        Utility.sendMessage(result.getMessage());
    }

    public Long getAdminRequests(){
        ServiceResult<Long> result = adminService.getAdminRequests();
        if (result.isSuccess()){
            return result.getResult();
        }
        return -1L;
    }
}

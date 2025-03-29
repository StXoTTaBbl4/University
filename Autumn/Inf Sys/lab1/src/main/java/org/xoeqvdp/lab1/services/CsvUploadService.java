package org.xoeqvdp.lab1.services;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.xoeqvdp.lab1.beans.UserBean;
import org.xoeqvdp.lab1.database.HibernateUtil;
import org.xoeqvdp.lab1.minio.MinioStorageService;
import org.xoeqvdp.lab1.model.*;
import org.xoeqvdp.lab1.utils.Utility;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class CsvUploadService {
    private static final Logger logger = Logger.getLogger(CsvUploadService.class.getName());

    @Inject
    UserBean userBean;

    @Inject
    MinioStorageService minioStorageService;

    public <T> void saveToDatabase(ArrayList<T> entities, String selectedValue, String fileName, InputStream fileStream, long fileSize, String contentType) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            if (entities.isEmpty()) {
                reportUpload(-1L, String.valueOf(UploadStatus.FAILURE), fileName);
                transaction.rollback();
                return;
            }

            try {
                if (selectedValue.equals("Vehicles")) {
                    session.createNativeMutationQuery("LOCK TABLE vehicles IN EXCLUSIVE MODE ").executeUpdate();
                    for (T entity : entities) {
                        Vehicle vehicle = (Vehicle) entity;
                        VehicleInteraction vehicleInteraction = VehicleInteraction.createVehicleInteraction(vehicle, userBean.getUser());

                        Long count = session.createQuery("SELECT COUNT(v) FROM Vehicle v WHERE v.name = :uniqueField", Long.class)
                                .setParameter("uniqueField", vehicle.getName())
                                .uniqueResult();

                        if (count != 0) {
                            transaction.rollback();
                            reportUpload(-1L, String.valueOf(UploadStatus.FAILURE), fileName);
                            Utility.sendMessage("Нарушено условие уникальности имени машины!");
                            return;
                        }

                        session.persist(vehicle);
                        session.persist(vehicleInteraction);

                    }
                    reportUpload((long) entities.size(), String.valueOf(UploadStatus.SUCCESS), fileName);
                } else if (selectedValue.equals("Coordinates")){
                    for (T entity : entities) {
                        Coordinates coordinates = (Coordinates) entity;
                        CoordinatesInteraction coordinatesInteraction = CoordinatesInteraction.createCoordinatesInteraction(userBean.getUser(), coordinates);

                        session.persist(coordinates);
                        session.persist(coordinatesInteraction);
                    }
                    reportUpload((long) entities.size(), String.valueOf(UploadStatus.SUCCESS), fileName);
                }

                if(!minioStorageService.uploadFile(String.valueOf(userBean.getUser().getId()), fileName, fileStream, fileSize, contentType)){
                    transaction.rollback();
                    Utility.sendMessage("Error occurred while uploading to MinIO. Transaction aborted");
                    return;
                }
                session.flush();
                transaction.commit();
            }catch (Exception e) {
                logger.log(Level.SEVERE, "Error executing query", e);
                reportUpload(-1L, String.valueOf(UploadStatus.FAILURE), fileName);
                if (transaction != null){
                    transaction.rollback();
                }
                minioStorageService.deleteFile(String.valueOf(userBean.getUser().getId()), fileName);
            }

        } catch (Exception e) {
            Utility.sendMessage(e.getMessage());
            reportUpload(-1L, String.valueOf(UploadStatus.FAILURE), fileName);
            logger.log(Level.SEVERE, "Error managing Hibernate session", e);
        }
    }

    private void reportUpload(Long amount, String status, String fileName){
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            FilesHistory filesHistory = new FilesHistory();
            filesHistory.setAmount(amount);
            filesHistory.setStatus(status);
            filesHistory.setInitiator(userBean.getUser());
            filesHistory.setFileName(fileName);

            session.persist(filesHistory);
            session.flush();
            transaction.commit();
        }catch (Exception e){
            Utility.sendMessage(e.getMessage());
        }
    }

    public ServiceResult<ArrayList<FilesHistory>> getHistory(){
        if (userBean.getUser() == null) {
            return new ServiceResult<>("Пользователь не найден");
        }
        try (Session session =  HibernateUtil.getSessionFactory().openSession()) {
            ArrayList<FilesHistory> filesHistory;

            if (userBean.getUser().getRole().equals(Roles.ADMIN)) {
                filesHistory = (ArrayList<FilesHistory>) session.createQuery("SELECT v FROM FilesHistory v", FilesHistory.class).getResultList();
            } else {
                filesHistory = (ArrayList<FilesHistory>) session.createQuery("SELECT v FROM FilesHistory v where initiator=:user", FilesHistory.class).
                        setParameter("user", userBean.getUser()).
                        getResultList();
            }

            return new ServiceResult<>(filesHistory, "");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

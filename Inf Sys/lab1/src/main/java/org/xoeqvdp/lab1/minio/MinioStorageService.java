package org.xoeqvdp.lab1.minio;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.MinioException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class MinioStorageService {
    private static final Logger logger = Logger.getLogger(MinioStorageService.class.getName());

    private static final String BUCKET_NAME = "csv-files";

    @Inject
    private MinioClientProvider minioClientProvider;

    public boolean uploadFile(String userId, String fileName, InputStream fileStream, long fileSize, String contentType){
        logger.log(Level.INFO, "Invoked uploadFile method with params: " + userId + " " + fileName);

        try {
            MinioClient minioClient = minioClientProvider.getClient();
            String objectName = userId + "/" + fileName;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .stream(fileStream, fileSize, -1)
                            .contentType(contentType)
                            .build()
            );

            return true;
        } catch (Exception e){
            logger.log(Level.WARNING, "Error occurred while uploading to MinIO:" + e.getMessage());
            return false;
        }
    }

    public void deleteFile(String userId, String fileName) {
        logger.log(Level.INFO, "Invoked deleteFile method with params: " + userId + " " + fileName);
        try {
            MinioClient minioClient = minioClientProvider.getClient();
            String objectName = userId + "/" + fileName;
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(objectName)
                            .build()
            );
        } catch (MinioException | IllegalArgumentException | InvalidKeyException | IOException |
                 NoSuchAlgorithmException e) {
            throw new RuntimeException("Error deleting file from MinIO: " + e.getMessage(), e);
        }
    }

    public InputStream downloadFile(String userId, String fileName) {
        try {
            MinioClient minioClient = minioClientProvider.getClient();
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET_NAME)
                            .object(userId + "/" + fileName)
                            .build()
            );
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error occurred while trying to download from MinIO file info: " + userId + "/" + fileName);
            logger.log(Level.WARNING, "Error occurred while trying to download from MinIO:" + e.getMessage());
            return null;
        }
    }
}

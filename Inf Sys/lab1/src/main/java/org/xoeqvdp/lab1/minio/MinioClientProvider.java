package org.xoeqvdp.lab1.minio;

import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.xoeqvdp.lab1.services.CsvUploadService;

import java.util.logging.Level;
import java.util.logging.Logger;

@ApplicationScoped
public class MinioClientProvider {
    private static final Logger logger = Logger.getLogger(MinioClientProvider.class.getName());

    @Inject
    @ConfigProperty(name = "minio.url")
    private String minioUrl;

    @Inject
    @ConfigProperty(name = "minio.accessKey")
    private String accessKey;

    @Inject
    @ConfigProperty(name = "minio.secretKey")
    private String secretKey;

    private MinioClient minioClient;

    @PostConstruct
    public void init() {
        this.minioClient = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(accessKey, secretKey)
                .build();
        logger.log(Level.INFO, "minioClient initiated with properties: " + minioUrl + " " + accessKey);
    }

    public MinioClient getClient() {
        return minioClient;
    }
}

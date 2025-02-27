package org.xoeqvdp.backend.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Entity
@Table(name = "certificate_category")
public class CertificateCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @OneToMany(mappedBy = "category", orphanRemoval = true)
    private List<CertificateSubCategory> subCategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CertificateSubCategory> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<CertificateSubCategory> subCategories) {
        this.subCategories = subCategories;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


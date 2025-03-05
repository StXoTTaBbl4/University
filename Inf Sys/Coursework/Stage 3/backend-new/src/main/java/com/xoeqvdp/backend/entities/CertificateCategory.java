package com.xoeqvdp.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "certificate_category")
@Getter
@Setter
public class CertificateCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 256)
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "certificate_sub_category", joinColumns = @JoinColumn(name = "category_id"))
    @Column(name = "name")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<String> subCategories;

}


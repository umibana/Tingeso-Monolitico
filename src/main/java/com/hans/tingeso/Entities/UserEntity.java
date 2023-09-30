package com.hans.tingeso.Entities;

import com.hans.tingeso.Models.SchoolType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_entity")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private UUID id;
    private String rut;
    private String name;
    private String surname;
    private LocalDate birthdate;
    // This is an enum declared in SchoolType.java
    @Enumerated(EnumType.STRING)
    @Column(name = "school_type")
    private SchoolType schoolType;
    private int graduationYear;
    // This should be separated in it's own entity
    // but due to lack of time we do it here
    private boolean enrollStatus;
    private boolean usingCredit;
    private int discount;
    // We save the transient objects
    @OneToMany(cascade = CascadeType.ALL)
    private List<ScoreEntity> scores;
    @OneToMany(cascade = CascadeType.ALL)
    private List<InstallmentEntity> installments;
}




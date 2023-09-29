package com.hans.tingeso.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "installement_entity")
public class InstallmentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    private boolean isPaid;
    private int amount;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "user_id") // add this line
    private UserEntity user;
}
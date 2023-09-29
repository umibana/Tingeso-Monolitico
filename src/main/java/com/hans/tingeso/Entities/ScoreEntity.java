package com.hans.tingeso.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "score_entity")
public class ScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;
    private int score;
    private LocalDate date;
    @ManyToOne
    @JoinColumn(name = "user_id") // add this line
    private UserEntity user;
}

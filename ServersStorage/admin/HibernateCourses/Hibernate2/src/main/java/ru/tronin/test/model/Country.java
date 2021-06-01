package ru.tronin.test.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "_countries")
public class Country {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String name;
}

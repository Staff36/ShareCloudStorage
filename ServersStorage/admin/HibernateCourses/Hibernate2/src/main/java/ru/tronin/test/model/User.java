package ru.tronin.test.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
@ToString
public class User {

    public User(String name, String phone) {
        this.name = name;
        this.phone = phone;

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Integer id;

    @Column
    private String name;

    @Column
    private String phone;

}

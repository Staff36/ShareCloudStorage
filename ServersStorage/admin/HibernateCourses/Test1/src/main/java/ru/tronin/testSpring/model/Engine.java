package ru.tronin.testSpring.model;


import lombok.*;

import javax.persistence.Id;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Engine {

    private String name;
    private int power;
}

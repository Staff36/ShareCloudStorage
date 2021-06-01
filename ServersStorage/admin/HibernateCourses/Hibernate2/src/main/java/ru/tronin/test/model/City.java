package ru.tronin.test.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.*;

@Entity
@Table(name = "_cities")
@Data
@NoArgsConstructor

public class City {
    @Id
    private Integer id;

    private Integer important;
    @Column(name = "title")
    private String name;
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    private Country country;
    @ManyToOne
    @Cascade(CascadeType.SAVE_UPDATE)
    private Region region;

    @Override
    public String toString() {
        String obl = region == null ? "" : region.getName() + ", ";
        return country.getName()+", " + obl + name;
    }
}

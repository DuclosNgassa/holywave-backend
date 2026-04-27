package org.softsystem.holywave.model.entities;

import jakarta.persistence.*;
import lombok.Data;


@Embeddable
@Data
public class Address {
    @Column(name = "street", length = 100)
    private String street;

    @Column(name = "houseNumber", length = 10)
    private String houseNumber;

    @Column(name = "zipCode", length = 20)
    private String zipCode;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100)
    private String country;
}

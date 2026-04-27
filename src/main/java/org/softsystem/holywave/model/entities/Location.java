package org.softsystem.holywave.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Embeddable
@Data
public class Location {

    @Column(name="online", nullable = false)
    @ColumnDefault("false")
    private Boolean online;

    @Column(name="onsite", nullable = false)
    @ColumnDefault("false")
    private Boolean onsite;

}

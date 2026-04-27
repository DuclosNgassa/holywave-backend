package org.softsystem.holywave.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;

@Embeddable
@Data
public class Frequency {

    @Column(name="daily", nullable = false)
    @ColumnDefault("false")
    private Boolean daily;

    @Column(name="weekly", nullable = false)
    @ColumnDefault("false")
    private Boolean weekly;

    @Column(name="monthly", nullable = false)
    @ColumnDefault("false")
    private Boolean monthly;

    @Column(name="yearly", nullable = false)
    @ColumnDefault("false")
    private Boolean yearly;

}

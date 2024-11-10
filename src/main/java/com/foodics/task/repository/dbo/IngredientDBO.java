package com.foodics.task.repository.dbo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "ingredient")
@Getter
@Setter
public class IngredientDBO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double initialAmount;
    private Double currentAmount;
    @Column(name = "low_amount_alert_sent", columnDefinition = "boolean default false")
    private boolean lowAmountAlertSent = false;
    @ManyToOne
    @JoinColumn(name = "unit_id", nullable = false)
    private StandardUnitDBO unit;

}

package com.reactive.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author u2cc
 */


@Table("diecast_cars_audit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiecastCarAudit {
    @Id
    @Column("id")
    private Long id;

    private Long diecastcar_id;
    private String brand;
    private String series;
    private String make;
    private String model;
    private BigDecimal scale;
    private String color;
    private String body_style;
    private LocalDate purchase_date;

    private String user;

    @Column("purchase_location")
    private String location;
    private String action;

    @CreatedDate
    private LocalDateTime ts;

}


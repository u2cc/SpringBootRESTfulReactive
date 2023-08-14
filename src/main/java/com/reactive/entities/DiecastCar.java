package com.reactive.entities;

//import io.swagger.annotations.ApiModel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

//@ApiModel(value="Diecast cars Model",description="Holds diecast car info")


@Table("diecast_cars")
@Getter
@Setter
public class DiecastCar {

    @Id
    @Column("id")
    private Long id;
    private String brand;
    private String series;
    private String make;
    private String model;
    private BigDecimal scale;
    private String color;
    private String body_style;
    private LocalDate purchase_date;
    @CreatedDate
    private java.time.LocalDateTime ts;

    //for the purpose of showcasing explicit column mapping, we give a param name other than the one of the column
    @Column("purchase_location")
    private String location;
}

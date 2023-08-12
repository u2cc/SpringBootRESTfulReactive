package com.reactive.entities;

//import io.swagger.annotations.ApiModel;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.math.BigDecimal;
import java.util.Date;

//@ApiModel(value="Diecast cars Model",description="Holds diecast car info")


@Table("diecast_cars")
public class DiecastCar {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column("id")
    private Long id;
    private String brand;
    private String series;
    private String make;
    private String model;
    private BigDecimal scale;
    private String color;
    private String body_style;

    private java.sql.Date purchase_date;
//    @CreatedDate
//    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private java.time.ZonedDateTime ts;

    //for the purpose of showcasing explicit column mapping, we give a param name other than the one of the column
    @Column("purchase_location")
    private String location;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getScale() {
        return scale;
    }

    public void setScale(BigDecimal scale) {
        this.scale = scale;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getBody_style() {
        return body_style;
    }

    public void setBody_style(String body_style) {
        this.body_style = body_style;
    }

    public Date getPurchase_date() {
        return purchase_date;
    }

    public void setPurchase_date(java.sql.Date purchase_date) {
        this.purchase_date = purchase_date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public java.time.ZonedDateTime  getTs() {
        return ts;
    }

    public void setTs(java.time.ZonedDateTime ts) {
        this.ts = ts;
    }


}

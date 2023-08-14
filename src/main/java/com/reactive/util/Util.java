package com.reactive.util;

import com.reactive.entities.DiecastCar;
import com.reactive.entities.DiecastCarAudit;

/**
 * @author u2cc
 */
public class Util {
    public static DiecastCarAudit toDiecastCarAudit(DiecastCar diecastCar){

        DiecastCarAudit diecastCarAudit = new DiecastCarAudit();
        diecastCarAudit.setBody_style(diecastCar.getBody_style());
        diecastCarAudit.setBrand(diecastCar.getBrand());
        diecastCarAudit.setColor(diecastCar.getColor());
        diecastCarAudit.setDiecastcar_id(diecastCar.getId());
        diecastCarAudit.setLocation(diecastCar.getLocation());
        diecastCarAudit.setMake(diecastCar.getMake());
        diecastCarAudit.setModel(diecastCar.getModel());
        diecastCarAudit.setScale(diecastCar.getScale());
        diecastCarAudit.setSeries(diecastCar.getSeries());
        diecastCarAudit.setBrand(diecastCar.getBrand());
        diecastCarAudit.setDiecastcar_id(diecastCar.getId());
        diecastCarAudit.setPurchase_date(diecastCar.getPurchase_date());

        return diecastCarAudit;
    }
}

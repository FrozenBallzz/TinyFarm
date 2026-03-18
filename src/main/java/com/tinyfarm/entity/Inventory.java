package com.tinyfarm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventories")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false, unique = true)
    private Farm farm;

    @Column(nullable = false)
    private int strawStock;

    @Column(nullable = false)
    private int waterBucketStock;

    @Column(nullable = false)
    private int soapStock;

    @Column(nullable = false)
    private int syringeStock;

    @Column(nullable = false)
    private int milkStock;

    protected Inventory() {
    }

    public Inventory(Farm farm, int strawStock, int waterBucketStock, int soapStock, int syringeStock, int milkStock) {
        this.farm = farm;
        this.strawStock = strawStock;
        this.waterBucketStock = waterBucketStock;
        this.soapStock = soapStock;
        this.syringeStock = syringeStock;
        this.milkStock = milkStock;
    }

    public Long getId() {
        return id;
    }

    public Farm getFarm() {
        return farm;
    }

    public int getStrawStock() {
        return strawStock;
    }

    public int getWaterBucketStock() {
        return waterBucketStock;
    }

    public int getSoapStock() {
        return soapStock;
    }

    public int getSyringeStock() {
        return syringeStock;
    }

    public int getMilkStock() {
        return milkStock;
    }

    public void consumeStraw(int amount) {
        this.strawStock -= amount;
    }

    public void consumeWaterBucket(int amount) {
        this.waterBucketStock -= amount;
    }

    public void consumeSoap(int amount) {
        this.soapStock -= amount;
    }

    public void consumeSyringe(int amount) {
        this.syringeStock -= amount;
    }

    public void addMilk(int amount) {
        this.milkStock += amount;
    }
}

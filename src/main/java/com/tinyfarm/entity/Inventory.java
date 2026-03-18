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
    private int feedStock;

    @Column(nullable = false)
    private int waterStock;

    @Column(nullable = false)
    private int milkStock;

    protected Inventory() {
    }

    public Inventory(Farm farm, int feedStock, int waterStock, int milkStock) {
        this.farm = farm;
        this.feedStock = feedStock;
        this.waterStock = waterStock;
        this.milkStock = milkStock;
    }

    public Long getId() {
        return id;
    }

    public Farm getFarm() {
        return farm;
    }

    public int getFeedStock() {
        return feedStock;
    }

    public int getWaterStock() {
        return waterStock;
    }

    public int getMilkStock() {
        return milkStock;
    }

    public void consumeFeed(int amount) {
        this.feedStock -= amount;
    }

    public void consumeWater(int amount) {
        this.waterStock -= amount;
    }

    public void addMilk(int amount) {
        this.milkStock += amount;
    }
}

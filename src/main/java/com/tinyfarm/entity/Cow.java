package com.tinyfarm.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cows")
public class Cow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int energy;

    @Column(nullable = false)
    private boolean readyToMilk;

    protected Cow() {
    }

    public Cow(Farm farm, String name, int energy, boolean readyToMilk) {
        this.farm = farm;
        this.name = name;
        this.energy = energy;
        this.readyToMilk = readyToMilk;
    }

    public Long getId() {
        return id;
    }

    public Farm getFarm() {
        return farm;
    }

    public String getName() {
        return name;
    }

    public int getEnergy() {
        return energy;
    }

    public boolean isReadyToMilk() {
        return readyToMilk;
    }

    public void feed(int energyGain) {
        this.energy += energyGain;
        this.readyToMilk = true;
    }

    public void collectMilk() {
        this.energy = Math.max(0, this.energy - 10);
        this.readyToMilk = false;
    }
}

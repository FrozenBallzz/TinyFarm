package com.tinyfarm.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "farms")
public class Farm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int coins;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private AppUser owner;

    @OneToOne(mappedBy = "farm", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Inventory inventory;

    @OneToMany(mappedBy = "farm", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cow> cows = new ArrayList<>();

    protected Farm() {
    }

    public Farm(String name, int coins, AppUser owner) {
        this.name = name;
        this.coins = coins;
        this.owner = owner;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCoins() {
        return coins;
    }

    public void spendCoins(int amount) {
        this.coins -= amount;
    }

    public AppUser getOwner() {
        return owner;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public List<Cow> getCows() {
        return cows;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void addCow(Cow cow) {
        cows.add(cow);
    }
}

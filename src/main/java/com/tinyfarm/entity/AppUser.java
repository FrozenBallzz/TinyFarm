package com.tinyfarm.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "app_users")
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String githubLogin;

    @Column(nullable = false, unique = true)
    private Long githubId;

    @Column(nullable = false)
    private String displayName;

    @OneToOne(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Farm farm;

    protected AppUser() {
    }

    public AppUser(String githubLogin, Long githubId, String displayName) {
        this.githubLogin = githubLogin;
        this.githubId = githubId;
        this.displayName = displayName;
    }

    public Long getId() {
        return id;
    }

    public String getGithubLogin() {
        return githubLogin;
    }

    public Long getGithubId() {
        return githubId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Farm getFarm() {
        return farm;
    }

    public void updateDisplayName(String newDisplayName) {
        this.displayName = newDisplayName;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }
}

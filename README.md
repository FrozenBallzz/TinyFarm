# TinyFarm
## Cette branche est faite pour le prototype utilisant la fausse authentification et les tests. 
##README POUR LES SPECS IL EST PAS FAIT POUR LES UTILISATEURS

L'application simule une ferme virtuelle inspirée du jeu **My eFarm**.
Le joueur gère différents animaux (poules, lapins, vache), produit des ressources (œufs, lait, lapins) et peut vendre ces produits afin de gagner des écus.

Le projet est implémenté sous la forme d'une **application web Spring Boot avec une API REST**, une base de données PostgreSQL et une authentification via **GitHub OAuth2**.

---

# Stack technique

Backend

* Java
* Spring Boot
* Spring Security
* OAuth2 (GitHub Login)
* Spring Data JPA
* PostgreSQL

Frontend

* HTML
* CSS
* JavaScript

Infrastructure

* Git
* GitHub
* GitHub Codespaces (environnement recommandé)

Guides disponibles :

* `miseenplace.md` pour une installation locale classique
* `Codespaces.md` pour une utilisation via GitHub Codespaces

---

# Prérequis

Pour lancer l'application, les outils suivants doivent être installés :

* **Java 17 ou supérieur**
* **Maven** (ou utiliser le wrapper Maven fourni)
* **PostgreSQL**
* **Git**

Vérifier les installations :

```bash
java -version
mvn -version
psql --version
```

---

# Installation du projet

## 1. Cloner le repository

```bash
git clone https://github.com/FrozenBallzz/TinyFarm
cd TinyFarm
```

---

# Configuration de la base de données

L'application utilise **PostgreSQL**.

## 1. Créer la base

Se connecter à PostgreSQL :

```bash
psql postgres
```

Créer la base :

```sql
CREATE DATABASE tinyfarm;
```

Créer l'utilisateur (optionnel mais recommandé) :

```sql
CREATE USER tinyfarm WITH PASSWORD 'tinyfarm';
ALTER ROLE tinyfarm SUPERUSER;
```

Quitter PostgreSQL :

```
\q
```

---

## 2. Configuration Spring Boot

Le fichier de configuration principal se trouve dans :

```
src/main/resources/application.yml
```

Exemple de configuration :

```yaml
spring:

  datasource:
    url: jdbc:postgresql://localhost:5432/tinyfarm
    username: tinyfarm
    password: tinyfarm

  jpa:
    hibernate:
      ddl-auto: update

    show-sql: true

    properties:
      hibernate:
        format_sql: true
```

---

# Configuration OAuth GitHub

L'application utilise **GitHub OAuth2** pour l'authentification.

## 1. Créer une application OAuth

Aller sur :

```
https://github.com/settings/developers
```

Créer une nouvelle **OAuth App**.

Configuration :

```
Application name : TinyFarm
Homepage URL     : http://localhost:8080
Callback URL     : http://localhost:8080/login/oauth2/code/github
```

---

## 2. Ajouter les variables d'environnement

Avant de lancer l'application :

```bash
export GITHUB_CLIENT_ID=xxxxx
export GITHUB_CLIENT_SECRET=xxxxx
```

---

# Lancer l'application

Depuis la racine du projet :

```bash
./mvnw spring-boot:run
```

Ou avec Maven :

```bash
mvn spring-boot:run
```

---

# Accéder à l'application

Une fois lancée :

```
http://localhost:8080
```

L'utilisateur sera invité à se connecter avec **GitHub**.

---

# Architecture du repository

Le projet est organisé selon une architecture en couches typique des applications **Spring Boot**.

```
tinyfarm/
│
├── src/
│   ├── main/
│   │   ├── java/com/tinyfarm/
│   │   │   ├── controller/
│   │   │   │   Endpoints REST exposés par l'application.
│   │   │   ├── service/
│   │   │   │   Logique métier du jeu (gestion des animaux, production, marché).
│   │   │   ├── repository/
│   │   │   │   Accès à la base de données via Spring Data JPA.
│   │   │   ├── entity/
│   │   │   │   Modèle de données persistant (entités JPA).
│   │   │   ├── dto/
│   │   │   │   Objets utilisés pour échanger des données avec l'API.
│   │   │   ├── config/
│   │   │   │   Configuration Spring (sécurité, OAuth2, etc.).
│   │   │   ├── domain/
│   │   │   │   Règles métier spécifiques au jeu.
│   │   │   ├── util/
│   │   │   │   Fonctions utilitaires utilisées dans l'application.
│   │   │   └── TinyFarmApplication.java
│   │   └── resources/
│   │       ├── application.yml
│   │       └── static/
│   └── test/
│       └── java/com/tinyfarm/
│
├── docs/
│   Documentation technique du projet :
│   architecture, API REST, modèle de données.
│
├── sql/
│   Scripts SQL (schéma et données de test).
│
├── scripts/
│   Scripts utilitaires pour faciliter le développement.
│
├── pom.xml
│   Configuration Maven et dépendances du projet.
│
└── README.md
    Documentation principale du projet.
```

---

# Architecture logicielle

L'application suit une architecture **en couches** :

```
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Controller

Responsable de :

* recevoir les requêtes HTTP
* valider les paramètres
* appeler les services

Exemple :

```
GET /farm
POST /chicken/feed
GET /market
```

---

## Service

Contient la logique métier du jeu.

Exemples :

* nourrir un animal
* calculer la production de lait
* gérer la reproduction des lapins
* vendre un produit

---

## Repository

Interfaces JPA permettant d'accéder aux données.

Spring Data JPA génère automatiquement les requêtes SQL.



## Entity

Classes représentant les tables de la base de données.

Chaque entité est persistée via **JPA / Hibernate**.

---

# Modèle métier

Les principales entités du domaine sont :

```
User
Farm
Chicken
Rabbit
Cow
Inventory
MarketItem
Transaction
```

Chaque entité correspond à une table PostgreSQL.

---

# Tests

Les tests se trouvent dans :

```
src/test/
```

Ils couvrent :

* la logique métier
* l'accès aux données
* les endpoints REST

---

# Documentation

La documentation technique est disponible dans :

```
docs/
```

Elle contient :

* l'architecture détaillée
* la documentation de l'API
* le modèle de base de données
* les règles métier du jeu

---

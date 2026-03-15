# TinyFarm
##README POUR LES SPECS IL EST PAS FAIT POUR LES UTILISATEURS

L'application simule une ferme virtuelle inspirée du jeu **My eFarm**.
Le joueur gère différents animaux (poules, lapins, vache), produit des ressources (œufs, lait, lapins) et peut vendre ces produits afin de gagner des écus.

Le projet est implémenté sous la forme d'une **application web Spring Boot avec une API REST**, une base de données embarquée pour le kernel, une cible PostgreSQL pour la suite, et une authentification via **GitHub OAuth2**.

Etat actuel du kernel :

* **H2** par défaut pour le développement et les tests
* **GitHub OAuth** obligatoire au démarrage
* domaine minimal : **User**, **Farm**, **Inventory**, **Cow**
* frontend minimal avec une page d'accueil et un tableau de bord simple

---

# Stack technique

Backend

* Java
* Spring Boot
* Spring Security
* OAuth2 (GitHub Login)
* Spring Data JPA
* H2
* PostgreSQL

Frontend

* HTML
* CSS
* JavaScript

Infrastructure

* Git
* GitHub
* GitHub Codespaces (environnement recommandé)

---

# Prérequis

Pour lancer l'application, les outils suivants doivent être installés :

* **Java 17 ou supérieur**
* **Maven** (ou utiliser le wrapper Maven fourni)
* **PostgreSQL** si vous activez le profil `postgres`
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
cd tinyfarm
```

---

# Configuration de la base de données

Le kernel utilise **H2 par défaut**.

## 1. Mode par défaut : H2

Aucune base externe n'est nécessaire pour lancer le kernel.

La base locale est stockée via :

```text
jdbc:h2:file:./data/tinyfarm
```

Console H2 :

```text
http://localhost:8080/h2-console
```

---

## 2. Mode PostgreSQL

Quand vous voudrez migrer vers PostgreSQL :

```bash
export SPRING_PROFILES_ACTIVE=postgres
```

Puis créez la base.

## 3. Créer la base

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

## 4. Configuration Spring Boot

Le fichier de configuration principal se trouve dans :

```
src/main/resources/application.yml
```

Exemple de configuration PostgreSQL :

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tinyfarm
    username: tinyfarm
    password: tinyfarm
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

Le kernel ne démarre pas tant que ces variables ne sont pas définies.

Pour un setup local simple :

```bash
cp .env.example .env
```

Puis remplissez `.env` avec vos identifiants GitHub OAuth.

Avant de lancer l'application, vous pouvez soit utiliser `.env`, soit exporter les variables à la main :

```bash
export GITHUB_CLIENT_ID=xxxxx
export GITHUB_CLIENT_SECRET=xxxxx
```

---

# Lancer l'application

Depuis la racine du projet :

```bash
mvn spring-boot:run
```

Ou avec PostgreSQL :

```bash
SPRING_PROFILES_ACTIVE=postgres mvn spring-boot:run
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
GET /api/dashboard
POST /api/cows/{id}/feed
POST /api/cows/{id}/collect-milk
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

Les principales entités du kernel sont :

```
User
Farm
Cow
Inventory
```

Chaque entité correspond à une table persistante. Le domaine est volontairement minimal pour faciliter les évolutions suivantes.

---

# Tests

Les tests se trouvent dans :

```
src/test/
```

Ils couvrent :

* la logique métier
* l'accès aux données
* le provisioning utilisateur OAuth
* les actions coeur sur les vaches

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

# Mise en place locale de TinyFarm

Ce document explique comment lancer **TinyFarm** en local sur **votre propre machine** et avec **votre propre compte GitHub**.

Le projet utilise actuellement :

* **Spring Boot**
* **GitHub OAuth2**
* **H2** en base embarquee par defaut
* **Maven**

L'objectif est de pouvoir lancer le kernel localement, se connecter avec GitHub, et verifier que l'application fonctionne.

---

# 1. Prerequis

Installez les outils suivants :

* **Java 17 minimum**
* **Maven**
* **Git**

Vous pouvez verifier :

```bash
java -version
mvn -version
git --version
```

---

# 2. Recuperer le projet

Clonez le depot puis placez-vous dans le dossier du projet :

```bash
git clone https://github.com/FrozenBallzz/TinyFarm
cd TinyFarm
```

---

# 3. Creer votre application OAuth GitHub

Le projet utilise une **GitHub OAuth App**.

Allez sur :

```text
https://github.com/settings/developers
```

Puis :

1. Ouvrez **OAuth Apps**
2. Cliquez sur **New OAuth App**
3. Remplissez les champs

Valeurs a utiliser :

* **Application name** : `TinyFarm`
* **Homepage URL** : `http://localhost:8080`
* **Authorization callback URL** : `http://localhost:8080/login/oauth2/code/github`

Une fois l'application creee :

* recuperez le **Client ID**
* generee un **Client Secret**

Conservez ces deux valeurs.

---

# 4. Configurer les variables locales

Le projet lit automatiquement un fichier `.env` local.

Creez-le a partir du modele fourni :

```bash
cp .env.example .env
```

Ouvrez ensuite `.env` et remplacez les valeurs d'exemple :

```env
GITHUB_CLIENT_ID=votre_client_id
GITHUB_CLIENT_SECRET=votre_client_secret
```

Important :

* ne committez jamais le fichier `.env`
* JE REPETE NE COMMITEZ JAMAIS LE FICHIER .env
* le fichier est deja ignore par Git
* vous pouvez partager `.env.example`, pas `.env`

---

# 5. Lancer l'application

Depuis la racine du projet :

```bash
mvn spring-boot:run
```

Si tout est correct, l'application demarre sur :

```text
http://localhost:8080
```

---

# 6. Tester la connexion GitHub

Quand la page s'affiche :

1. cliquez sur **Login with GitHub**
2. acceptez l'autorisation sur GitHub
3. revenez sur l'application

Si la connexion reussit :

* votre utilisateur est cree en base
* une ferme est provisionnee
* un inventaire est cree
* une vache de depart est ajoutee

---

# 7. Base de donnees locale

Par defaut, TinyFarm utilise **H2** en local.

Cela signifie :

* pas besoin d'installer PostgreSQL pour demarrer
* la base sert de support simple pour le developpement

La console H2 est disponible ici :

```text
http://localhost:8080/h2-console
```

Configuration H2 par defaut :

* JDBC URL : `jdbc:h2:file:./data/tinyfarm`
* User Name : `sa`
* Password : vide

---

# 8. Lancer les tests

Avant de pousser vos changements, vous pouvez verifier que tout fonctionne :

```bash
mvn test
```

Les tests actuels couvrent notamment :

* le demarrage du contexte Spring
* le provisioning apres connexion GitHub
* les actions coeur du kernel

---

# 9. Problemes frequents

## L'application ne demarre pas

Verifiez :

* que `.env` existe
* que `GITHUB_CLIENT_ID` est renseigne
* que `GITHUB_CLIENT_SECRET` est renseigne

Le projet est configure pour **echouer au demarrage** si les credentials GitHub sont absents.

## GitHub refuse la connexion

Verifiez que vous avez bien cree une **OAuth App** et non une **GitHub App**.

Verifiez aussi :

* **Homepage URL** : `http://localhost:8080`
* **Authorization callback URL** : `http://localhost:8080/login/oauth2/code/github`

## Page Whitelabel / erreur 500 apres connexion

Dans ce cas, regardez la console Spring Boot.

La page du navigateur ne montre pas le detail complet. La vraie erreur utile se trouve dans les logs du terminal.

---

# 10. Passage futur a PostgreSQL

Le projet est prepare pour evoluer vers PostgreSQL plus tard.

Quand ce sera necessaire, vous pourrez utiliser le profil :

```bash
export SPRING_PROFILES_ACTIVE=postgres
```

Et renseigner dans `.env` :

```env
DB_URL=jdbc:postgresql://localhost:5432/tinyfarm
DB_USERNAME=tinyfarm
DB_PASSWORD=tinyfarm
```

Pour l'instant, ce n'est pas necessaire pour demarrer localement.

---

# 11. Resume rapide

Pour aller vite :

```bash
git clone <url-du-repo>
cd TinyFarm
cp .env.example .env
```

Puis :

1. creer une GitHub OAuth App
2. copier le Client ID et le Client Secret dans `.env`
3. lancer `mvn spring-boot:run`
4. ouvrir `http://localhost:8080`
5. tester la connexion GitHub

---

# 12. Fichiers utiles

* `README.md`
* `.env.example`
* `src/main/resources/application.yml`
* `src/main/resources/application-postgres.yml`


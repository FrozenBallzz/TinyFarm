# TinyFarm sur GitHub Codespaces

Ce document explique comment utiliser **TinyFarm** directement dans **GitHub Codespaces**, avec **votre propre compte GitHub** et **votre propre OAuth App**.

Le point important a comprendre est le suivant :

* un Codespace possede une URL publique propre
* cette URL sert pour le callback OAuth
* donc chaque utilisateur doit en pratique configurer sa propre **GitHub OAuth App**

Pour TinyFarm, c'est la solution la plus simple et la plus fiable.

---

# 1. Objectif

A la fin de ce guide, vous serez capable de :

* ouvrir le projet dans un Codespace
* demarrer l'application
* configurer GitHub OAuth avec l'URL de votre Codespace
* vous connecter a TinyFarm avec votre compte GitHub

---

# 2. Ouvrir le projet dans Codespaces

Depuis le depot GitHub :

1. cliquez sur **Code**
2. ouvrez l'onglet **Codespaces**
3. cliquez sur **Create codespace on main** ou sur votre branche

Le repo contient deja un fichier de configuration :

* [`.devcontainer/devcontainer.json`](/home/armand/Bureau/LABO/COURS/WEB/repo/TinyFarm/.devcontainer/devcontainer.json)

Ce fichier prepare :

* Java
* Maven
* le port `8080`
* quelques extensions VS Code utiles

Attendez que le Codespace termine son initialisation.

---

# 3. Ajouter vos secrets dans Codespaces

TinyFarm attend ces variables d'environnement :

* `GITHUB_CLIENT_ID`
* `GITHUB_CLIENT_SECRET`

Dans GitHub, ajoutez-les comme **Codespaces secrets**.

Vous pouvez les definir :

* au niveau de votre compte GitHub
* ou au niveau du repository si vous avez les droits

Chemin habituel :

```text
GitHub > Settings > Codespaces > Secrets
```

Nom des secrets a creer :

```text
GITHUB_CLIENT_ID
GITHUB_CLIENT_SECRET
```

Important :

* ne stockez pas ces valeurs dans un commit
* ne poussez jamais un vrai `.env`
* dans Codespaces, les **Secrets** sont preferables a `.env`

---

# 4. Demarrer l'application une premiere fois

Dans le terminal du Codespace, a la racine du projet :

```bash
mvn spring-boot:run
```

Quand l'application demarre :

* Codespaces detecte le port `8080`
* une URL publique ou privee est generee

Exemple de forme d'URL :

```text
https://<nom-du-codespace>-8080.app.github.dev
```

Gardez cette URL, elle est indispensable pour GitHub OAuth.

---

# 5. Rendre le port 8080 accessible

Pour que GitHub puisse rediriger vers votre application, le port `8080` doit etre accessible.

Dans l'onglet **Ports** de VS Code / Codespaces :

1. reperez le port `8080`
2. verifiez l'URL associee
3. si necessaire, changez la visibilite du port pour autoriser la redirection OAuth

Selon votre configuration GitHub, il faudra souvent utiliser :

* **Public**

Si votre organisation interdit les ports publics, il faudra verifier la politique Codespaces de l'organisation.

---

# 6. Creer votre GitHub OAuth App pour Codespaces

TinyFarm utilise une **GitHub OAuth App**, pas une **GitHub App**.

Allez sur :

```text
https://github.com/settings/developers
```

Puis :

1. ouvrez **OAuth Apps**
2. cliquez sur **New OAuth App**

Remplissez :

* **Application name** : `TinyFarm Codespaces`
* **Homepage URL** : l'URL publique de votre Codespace
* **Authorization callback URL** : l'URL publique de votre Codespace suivie de `/login/oauth2/code/github`

Exemple :

```text
Homepage URL:
https://mon-codespace-8080.app.github.dev

Authorization callback URL:
https://mon-codespace-8080.app.github.dev/login/oauth2/code/github
```

Ensuite :

* copiez le **Client ID**
* generee un **Client Secret**
* mettez a jour vos **Codespaces secrets**

Si vous aviez deja demarre l'application avant de creer les secrets, redemarrez-la ensuite.

---

# 7. Redemarrer proprement l'application

Une fois les secrets presents :

```bash
mvn spring-boot:run
```

Puis ouvrez l'URL du port `8080`.

Vous devriez voir la page d'accueil de TinyFarm.

---

# 8. Tester la connexion GitHub

Sur la page :

1. cliquez sur **Login with GitHub**
2. validez l'autorisation GitHub
3. laissez GitHub vous rediriger vers l'URL du Codespace

Si tout se passe bien :

* votre utilisateur est cree
* votre ferme est creee
* votre inventaire est cree
* votre vache de depart apparait

---

# 9. Comprendre la contrainte principale de Codespaces

En local, l'URL est stable :

```text
http://localhost:8080
```

Dans Codespaces, l'URL depend du Codespace.

Cela veut dire :

* si vous recreez un nouveau Codespace, vous pouvez obtenir une nouvelle URL
* si l'URL change, votre OAuth App doit etre mise a jour

En pratique, si l'authentification arrete de fonctionner dans un nouveau Codespace, le premier reflexe est :

1. recuperer la nouvelle URL du port `8080`
2. mettre a jour **Homepage URL**
3. mettre a jour **Authorization callback URL**

---

# 10. Base de donnees dans Codespaces

Le projet utilise **H2** par defaut.

Dans Codespaces, cela permet de demarrer sans PostgreSQL.

La base locale est stockee dans :

```text
data/
```

Ce dossier est maintenant ignore par Git, car c'est une donnee locale de developpement.

La console H2 est disponible ici :

```text
https://<votre-url-codespace>/h2-console
```

Parametres H2 :

* JDBC URL : `jdbc:h2:file:./data/tinyfarm`
* User Name : `sa`
* Password : vide

---

# 11. Lancer les tests dans Codespaces

Depuis le terminal du Codespace :

```bash
mvn test
```

Les tests utilisent le profil `test` avec H2 en memoire, donc ils sont adaptes a Codespaces.

---

# 12. Problemes frequents

## L'application demarre mais GitHub refuse la connexion

Verifiez :

* que vous avez bien cree une **OAuth App**
* que l'URL de callback correspond exactement a votre URL Codespace
* que le port `8080` est accessible

## La page d'accueil marche mais le login echoue

Verifiez :

* `GITHUB_CLIENT_ID`
* `GITHUB_CLIENT_SECRET`
* l'URL du port `8080`
* la callback GitHub

Redemarrez ensuite l'application.

## Le login marchait avant, puis plus maintenant

Cause tres probable :

* votre Codespace a change d'URL

Dans ce cas :

1. recuperez la nouvelle URL
2. mettez a jour l'OAuth App GitHub
3. relancez l'application

## Erreur 500 / Whitelabel

Dans ce cas, regardez le terminal Spring Boot.

La vraie erreur utile est dans les logs du backend, pas seulement dans la page du navigateur.

---

# 13. Resume ultra court

1. ouvrez le repo dans Codespaces
2. ajoutez `GITHUB_CLIENT_ID` et `GITHUB_CLIENT_SECRET` comme **Codespaces secrets**
3. lancez `mvn spring-boot:run`
4. recuperez l'URL publique du port `8080`
5. creez une **GitHub OAuth App** avec cette URL
6. utilisez :

```text
Homepage URL = https://<codespace-url>
Callback URL = https://<codespace-url>/login/oauth2/code/github
```

7. ouvrez TinyFarm et testez la connexion

---

# 14. Fichiers utiles

* [`.devcontainer/devcontainer.json`](/home/armand/Bureau/LABO/COURS/WEB/repo/TinyFarm/.devcontainer/devcontainer.json)
* [`.env.example`](/home/armand/Bureau/LABO/COURS/WEB/repo/TinyFarm/.env.example)
* [`src/main/resources/application.yml`](/home/armand/Bureau/LABO/COURS/WEB/repo/TinyFarm/src/main/resources/application.yml)
* [`miseenplace.md`](/home/armand/Bureau/LABO/COURS/WEB/repo/TinyFarm/miseenplace.md)

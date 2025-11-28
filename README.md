# Sparadrap : Système de Gestion Médicale Simplifié

![Statut du Projet](https://img.shields.io/badge/Statut-Initial-orange)
![Langage](https://img.shields.io/badge/Langage-Java%2017+-blue)
![Build Tool](https://img.shields.io/badge/Build-Maven-red)
![Licence](https://img.shields.io/badge/Licence-MIT-green)

## Description du Projet

**Sparadrap** est un outil de gestion backend développé en Java. Il vise à simuler et gérer les interactions et les données critiques au sein d'un système médical simplifié, incluant la gestion des fiches de **Personnes** (patients), des **Médecins**, et des informations de **Mutuelle**.

Ce projet suit les bonnes pratiques de développement Java, notamment l'utilisation de **Maven** pour la gestion des dépendances, la **journalisation (logging)** pour le débogage, et une couverture de **tests unitaires** pour garantir la fiabilité de la logique métier.

##  Technologies Utilisées

| Technologie | Version | Rôle Principal |
| :--- | :--- | :--- |
| **Langage** | Java 17+ | Cœur de l'application. |
| **Build Tool** | Maven 3.8+ | Gestion des dépendances, compilation, et packaging. |
| **Base de Données** | MySQL | Stockage des données d'entités. |
| **Logging** | SLF4J / Logback | Journalisation des événements d'application. |
| **Testing** | JUnit 5 | Framework d'écriture et d'exécution des tests unitaires. |

##  Démarrage et Installation

Pour lancer et exécuter le projet **Sparadrap**, suivez les étapes ci-dessous.

### Pré-requis

Vous devez avoir installé les éléments suivants sur votre machine :

1. **Java Development Kit (JDK) 17** ou une version plus récente.
2. **Apache Maven 3.8+**.
3. Un serveur de base de données **MySQL**.
4. **MySQL Workbench** (ou un client SQL similaire) pour la configuration de la base.

### 1. Configuration de la Base de Données (MySQL Workbench)

Vous devez initialiser la structure de la base de données avant de lancer l'application.

1. **Ouvrir MySQL Workbench :** Lancez l'application et connectez-vous à votre instance de serveur MySQL.

2. **Exécuter le Script de Schéma :**
    * Le script de création des tables se trouve dans le fichier : `src/main/Script_SQL/sparadrap.sql`.
    * Ouvrez ce fichier, copiez son contenu, et collez-le dans la fenêtre de requête de Workbench.
    * **Exécutez le script** pour créer les tables (`personne`, `medecin`, `mutuelle`, etc.).
3. **Mettre à jour la Configuration Java :**
    * Assurez-vous que les informations de connexion (URL, nom d'utilisateur et mot de passe) dans votre fichier de configuration (`application.properties` ou `application.yml` si vous utilisez Spring) correspondent à votre configuration MySQL.

### 2. Instructions de Construction et Lancement

1. **Clonage du Dépôt**
   Ouvrez votre terminal et clonez le projet depuis GitHub :

    ```bash
    git clone [https://github.com/orhan54/sparadrap.git](https://github.com/orhan54/sparadrap.git)
    cd sparadrap
    ```

2. **Compilation et Installation des Dépendances**
   Cette étape télécharge les dépendances (comme Logback) et crée le fichier JAR :

    ```bash
    mvn clean install
    ```

3. **Exécution du Projet**
   Lancez l'application en utilisant le fichier JAR généré :

    ```bash
    java -jar target/sparadrap-1.0-SNAPSHOT.jar 
    # (Adaptez le nom du fichier JAR si nécessaire)
    ```

## Configuration des Logs

Ce projet utilise la façade **SLF4J** avec l'implémentation **Logback** pour la journalisation.

* Le fichier de configuration principal est `src/main/resources/logback.xml`.
* Par défaut, les messages de niveau **INFO** et supérieur sont affichés en console.
* Pour des diagnostics plus détaillés, vous pouvez changer le niveau de log à **DEBUG** ou **TRACE** dans le fichier `logback.xml`.

## Tests Unitaires

Le projet inclut des tests unitaires essentiels, écrits avec **JUnit 5**, pour valider la logique métier.

### Exécution des Tests

Pour lancer tous les tests unitaires :

```bash
    mvn test
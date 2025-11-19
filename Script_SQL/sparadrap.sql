-- Créer la base si elle n'existe pas
CREATE DATABASE IF NOT EXISTS mavensparadrap CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Sélectionner la base
USE mavensparadrap;

-- Table Lieu
CREATE TABLE IF NOT EXISTS Lieu(
    Id_Lieu INT AUTO_INCREMENT,
    lieu_adresse VARCHAR(100) NOT NULL,
    lieu_email VARCHAR(50),
    lieu_telephone VARCHAR(12),
    lieu_ville VARCHAR(70) NOT NULL,
    lieu_cp INT NOT NULL,
    PRIMARY KEY(Id_Lieu),
    UNIQUE(lieu_email),
    UNIQUE(lieu_telephone)
    );

-- Table Pharmacie
CREATE TABLE IF NOT EXISTS Pharmacie(
    Id_Pharmacie INT AUTO_INCREMENT,
    pha_nom VARCHAR(30) NOT NULL,
    pha_prenom VARCHAR(50) NOT NULL,
    Id_Lieu INT NOT NULL,
    PRIMARY KEY(Id_Pharmacie),
    FOREIGN KEY(Id_Lieu) REFERENCES Lieu(Id_Lieu) ON DELETE CASCADE
    );

-- Table Mutuelle
CREATE TABLE IF NOT EXISTS Mutuelle(
    Id_Mutuelle INT AUTO_INCREMENT,
    mut_nom VARCHAR(70) NOT NULL,
    mut_taux_prise_en_charge INT NOT NULL,
    mut_num_departement INT NOT NULL,
    Id_Lieu INT,
    PRIMARY KEY(Id_Mutuelle),
    FOREIGN KEY(Id_Lieu) REFERENCES Lieu(Id_Lieu) ON DELETE SET NULL
    );

-- Table type medecin
CREATE TABLE IF NOT EXISTS TYPE_Medecin(
    Id_TYPE_Medecin INT AUTO_INCREMENT,
    label_medecin VARCHAR(30),
    PRIMARY KEY(Id_TYPE_Medecin)
    );

-- Table Medecin
CREATE TABLE IF NOT EXISTS Medecin(
    Id_Medecin INT AUTO_INCREMENT,
    med_nom VARCHAR(30) NOT NULL,
    med_prenom VARCHAR(50) NOT NULL,
    med_numero_agreement CHAR(11) NOT NULL,
    Id_Lieu INT NOT NULL,
    PRIMARY KEY(Id_Medecin),
    UNIQUE(med_numero_agreement),
    FOREIGN KEY(Id_Lieu) REFERENCES Lieu(Id_Lieu) ON DELETE CASCADE
    );

-- Table Stock_Medicament
CREATE TABLE IF NOT EXISTS Stock_Medicament(
    Id_Stock_Medicament INT AUTO_INCREMENT,
    medic_nom VARCHAR(30) NOT NULL,
    medic_categorie VARCHAR(30) NOT NULL,
    medic_quantite INT NOT NULL,
    medic_date_mise_en_service DATE NOT NULL,
    medic_date_entree_stock DATE NOT NULL,
    medic_prix_unitaire DOUBLE,
    Id_TYPE_Categorie INT NOT NULL,
    Id_Pharmacie INT NOT NULL,
    PRIMARY KEY(Id_Stock_Medicament),
    FOREIGN KEY(Id_Pharmacie) REFERENCES Pharmacie(Id_Pharmacie)
    );

-- Table Patient
CREATE TABLE IF NOT EXISTS Patient(
    Id_Patient INT AUTO_INCREMENT,
    pat_nom VARCHAR(30) NOT NULL,
    pat_prenom VARCHAR(50) NOT NULL,
    pat_num_secu VARCHAR(15) NOT NULL,
    pat_date_naissance DATE NOT NULL,
    Id_Lieu INT NOT NULL,
    Id_Mutuelle INT NOT NULL,
    PRIMARY KEY(Id_Patient),
    FOREIGN KEY(Id_Lieu) REFERENCES Lieu(Id_Lieu) ON DELETE CASCADE,
    FOREIGN KEY(Id_Mutuelle) REFERENCES Mutuelle(Id_Mutuelle) ON DELETE CASCADE
    );

-- Table Ordonnance
CREATE TABLE IF NOT EXISTS Ordonnance(
    Id_Ordonnance INT AUTO_INCREMENT,
    ordo_date DATE NOT NULL,
    ordo_nom_medecin VARCHAR(50) NOT NULL,
    ordo_nom_patient VARCHAR(50) NOT NULL,
    Id_Medecin INT NOT NULL,
    Id_Patient INT NOT NULL,
    PRIMARY KEY(Id_Ordonnance),
    FOREIGN KEY(Id_Medecin) REFERENCES Medecin(Id_Medecin),
    FOREIGN KEY(Id_Patient) REFERENCES Patient(Id_Patient)
    );

-- Table Commande
CREATE TABLE IF NOT EXISTS Commande(
    Id_Commande INT AUTO_INCREMENT,
    com_date_commande DATE NOT NULL,
    com_nom_medecin VARCHAR(50) NOT NULL,
    com_nom_patient VARCHAR(50) NOT NULL,
    com_quantite INT NOT NULL,
    com_prix DECIMAL(6,2) NOT NULL,
    Id_Patient INT,
    Id_Ordonnance INT,
    Id_Pharmacie INT NOT NULL,
    PRIMARY KEY(Id_Commande),
    FOREIGN KEY(Id_Patient) REFERENCES Patient(Id_Patient) ON DELETE SET NULL,
    FOREIGN KEY(Id_Ordonnance) REFERENCES Ordonnance(Id_Ordonnance) ON DELETE SET NULL,
    FOREIGN KEY(Id_Pharmacie) REFERENCES Pharmacie(Id_Pharmacie)
    );

-- Table contenir
CREATE TABLE IF NOT EXISTS contenir(
    Id_Commande INT,
    Id_Stock_Medicament INT,
    total_achete INT NOT NULL,
    prix_achat INT NOT NULL,
    PRIMARY KEY(Id_Commande, Id_Stock_Medicament),
    FOREIGN KEY(Id_Commande) REFERENCES Commande(Id_Commande),
    FOREIGN KEY(Id_Stock_Medicament) REFERENCES Stock_Medicament(Id_Stock_Medicament)
    );

-- Jeu de données pour mon application sparadrap
-- Insert des données
INSERT INTO Lieu(lieu_adresse, lieu_email, lieu_telephone, lieu_ville, lieu_cp) VALUES
        ("15 rue du Moulin", "contact1@gmail.com", "+33311000001", "Nancy", 54000),
        ("20 avenue des Vosges", "contact2@gmail.com", "+33311000002", "Metz", 57000),
        ("7 rue du Port", "contact3@gmail.com", "+33311000003", "Toul", 54200),
        ("3 rue des Écoles", "contact4@gmail.com", "+33311000004", "Verdun", 55100),
        ("78 boulevard des Arts", "contact5@gmail.com", "+33311000005", "Strasbourg", 67000);

INSERT INTO Pharmacie(pha_nom, pha_prenom, Id_Lieu) VALUES
        ("Durand", "Marie", 1),
        ("Bernard", "Luc", 2),
        ("Thomas", "Sophie", 3),
        ("Petit", "Louis", 4),
        ("Richard", "Emma", 5);


INSERT INTO Mutuelle(mut_nom, mut_taux_prise_en_charge, mut_num_departement, Id_Lieu) VALUES
        ("Harmonie Mutuelle", 30, 54, 1),
        ("MGEN", 30, 57, 2),
        ("AXA Santé", 30, 67, 3),
        ("Mutuelle Bleue", 30, 75, 4),
        ("SwissLife", 30, 33, 5);


INSERT INTO Medecin(med_nom, med_prenom, med_numero_agreement, Id_Lieu) VALUES
        ("Lambert", "Pierre", "18469275106", 1),
        ("Girard", "Julie", "54978558429", 2),
        ("Faure", "Nicolas", "37842598556", 3),
        ("Marchand", "Elise", "75146935297", 4),
        ("Picard", "Hugo", "82454120563", 5);


INSERT INTO Stock_Medicament(medic_nom, medic_categorie, medic_quantite, medic_date_mise_en_service, medic_date_entree_stock, medic_prix_unitaire, Id_TYPE_Categorie, Id_Pharmacie) VALUES
        ("Doliprane", "Antalgique", 120, "2023-05-01", "2023-04-28", 2.50, 1, 1),
        ("Ibuprofène", "Anti-inflammatoire",80, "2023-06-15", "2023-06-10", 3.40, 2, 2),
        ("Efferalgan", "Antalgique", 200, "2023-02-20", "2023-02-19", 2.20, 1, 3),
        ("Smecta", "Digestif",150, "2023-07-12", "2023-07-10", 4.30, 3, 4),
        ("Amoxicilline", "Antibiotique", 50, "2023-08-01", "2023-07-30", 8.60, 4, 5);


INSERT INTO Patient(pat_nom, pat_prenom, pat_num_secu, pat_date_naissance, Id_Lieu, Id_Mutuelle) VALUES
        ("Dupont", "Jean", 185054789012345, "1985-05-16", 1, 1),
        ("Martin", "Claire", 282067789012345, "1982-06-20", 2, 2),
        ("Robert", "Lucie", 194037789012345, "1994-03-12", 3, 3),
        ("Thomas", "Eric", 276118789012345, "1976-11-08", 4, 4),
        ("Bernard", "Julie", 201225789012345, "2001-12-25", 5, 5);


INSERT INTO Ordonnance(ordo_date, ordo_nom_medecin, ordo_nom_patient, Id_Medecin, Id_Patient) VALUES
        ("2024-01-10", "Lambert Pierre", "Dupont Jean", 1, 1),
        ("2024-02-05", "Girard Julie", "Martin Claire", 2, 2),
        ("2024-03-20", "Faure Nicolas", "Robert Lucie", 3, 3),
        ("2024-04-11", "Marchand Elise", "Thomas Eric", 4, 4),
        ("2024-05-03", "Picard Hugo", "Bernard Julie", 5, 5);


INSERT INTO Commande(com_date_commande, com_nom_medecin, com_nom_patient, com_quantite, com_prix, Id_Patient, Id_Ordonnance, Id_Pharmacie) VALUES
        ("2024-01-12", "Lambert Pierre", "Dupont Jean", 2, 5.00, 1, 1, 1),
        ("2024-02-06", "Girard Julie", "Martin Claire", 1, 3.40, 2, 2, 2),
        ("2024-03-22", "Faure Nicolas", "Robert Lucie", 3, 6.60, 3, 3, 3),
        ("2024-04-13", "Marchand Elise", "Thomas Eric", 1, 4.30, 4, 4, 4),
        ("2024-05-05", "Picard Hugo", "Bernard Julie", 2, 17.20, 5, 5, 5);


INSERT INTO contenir(Id_Commande, Id_Stock_Medicament, total_achete, prix_achat) VALUES
        (1, 1, 2, 5),
        (2, 2, 1, 3),
        (3, 3, 3, 7),
        (4, 4, 1, 4),
        (5, 5, 2, 17);

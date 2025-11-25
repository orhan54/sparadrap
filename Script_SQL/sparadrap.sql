-- Créer la base si elle n'existe pas
CREATE DATABASE IF NOT EXISTS sparadrap CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- Sélectionner la base
USE sparadrap;

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

-- Table Medecin
CREATE TABLE IF NOT EXISTS Medecin(
    Id_Medecin INT AUTO_INCREMENT,
    med_nom VARCHAR(30) NOT NULL,
    med_prenom VARCHAR(50) NOT NULL,
    med_numero_agreement VARCHAR(11) NOT NULL,
    Id_Lieu INT NOT NULL,
    PRIMARY KEY(Id_Medecin),
    UNIQUE(med_numero_agreement),
    FOREIGN KEY(Id_Lieu) REFERENCES Lieu(Id_Lieu) ON DELETE CASCADE
    );

-- Table Patient
CREATE TABLE IF NOT EXISTS Patient(
    Id_Patient INT AUTO_INCREMENT,
    pat_nom VARCHAR(30) NOT NULL,
    pat_prenom VARCHAR(50) NOT NULL,
    pat_num_secu VARCHAR(15) NOT NULL,
    pat_date_naissance DATE NOT NULL,
    Id_Medecin INT NOT NULL,
    Id_Lieu INT NOT NULL,
    Id_Mutuelle INT,
    PRIMARY KEY(Id_Patient),
    UNIQUE(pat_num_secu),
    FOREIGN KEY(Id_Medecin) REFERENCES Medecin(Id_Medecin),
    FOREIGN KEY(Id_Lieu) REFERENCES Lieu(Id_Lieu) ON DELETE CASCADE,
    FOREIGN KEY(Id_Mutuelle) REFERENCES Mutuelle(Id_Mutuelle) ON DELETE SET NULL
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
    ("78 boulevard des Arts", "contact5@gmail.com", "+33311000005", "Strasbourg", 67000),
    ("12 rue de la République", "contact6@gmail.com", "+33311000006", "Épinal", 88000),
    ("45 avenue du Général Leclerc", "contact7@gmail.com", "+33311000007", "Thionville", 57100),
    ("9 place des Fêtes", "contact8@gmail.com", "+33311000008", "Colmar", 68000),
    ("33 rue du Marché", "contact9@gmail.com", "+33311000009", "Mulhouse", 68100),
    ("50 chemin des Jardins", "contact10@gmail.com", "+33311000010", "Saint-Dié-des-Vosges", 88100),
    ("2 rue des Peupliers", "contact11@gmail.com", "+33311000011", "Forbach", 57600),
    ("18 rue du Stade", "contact12@gmail.com", "+33311000012", "Haguenau", 67500),
    ("27 avenue de Lorraine", "contact13@gmail.com", "+33311000013", "Sarreguemines", 57200),
    ("64 rue des Prés", "contact14@gmail.com", "+33311000014", "Sélestat", 67600),
    ("89 route de Bâle", "contact15@gmail.com", "+33311000015", "Illkirch-Graffenstaden", 67400);


INSERT INTO Pharmacie(pha_nom, pha_prenom, Id_Lieu) VALUES
    ("Durand", "Marie", 1),
    ("Bernard", "Luc", 2),
    ("Thomas", "Sophie", 3),
    ("Petit", "Louis", 4),
    ("Richard", "Emma", 5);


INSERT INTO Stock_Medicament(medic_nom, medic_categorie, medic_quantite, medic_date_mise_en_service, medic_date_entree_stock, medic_prix_unitaire, Id_TYPE_Categorie, Id_Pharmacie) VALUES
    ("Doliprane", "Antalgique", 120, "2023-05-01", "2023-04-28", 2.50, 1, 1),
    ("Ibuprofène", "Anti-inflammatoire",80, "2023-06-15", "2023-06-10", 3.40, 2, 2),
    ("Efferalgan", "Antalgique", 200, "2023-02-20", "2023-02-19", 2.20, 1, 3),
    ("Smecta", "Digestif",150, "2023-07-12", "2023-07-10", 4.30, 3, 4),
    ("Amoxicilline", "Antibiotique", 50, "2023-08-01", "2023-07-30", 8.60, 4, 5),
    ("Xanax", "Anxiolytique", 60, "2023-03-05", "2023-03-01", 5.20, 5, 1),
    ("Levothyrox", "Hormonal", 180, "2023-01-15", "2023-01-10", 4.10, 6, 2),
    ("Spasfon", "Antispasmodique", 140, "2023-09-12", "2023-09-08", 3.00, 7, 3),
    ("Doliprane Enfant", "Antalgique", 90, "2023-04-22", "2023-04-20", 2.00, 1, 1),
    ("Azithromycine", "Antibiotique", 70, "2023-10-05", "2023-10-02", 9.50, 4, 4),
    ("Imodium", "Digestif", 110, "2023-06-11", "2023-06-09", 4.80, 3, 5),
    ("Ventoline", "Respiratoire", 40, "2023-11-01", "2023-10-28", 12.00, 8, 2),
    ("Aspirine", "Antalgique", 250, "2023-03-18", "2023-03-15", 1.90, 1, 3),
    ("Omeprazole", "Gastrique", 130, "2023-05-25", "2023-05-22", 6.70, 9, 4),
    ("Cétirizine", "Antihistaminique", 95, "2023-04-12", "2023-04-10", 3.60, 10, 5);


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


INSERT INTO Patient(pat_nom, pat_prenom, pat_num_secu, pat_date_naissance, Id_Lieu, Id_Mutuelle, Id_Medecin) VALUES
    ("Dupont", "Jean", 185054789012345, "1985-05-16", 1, 1, 1),
    ("Martin", "Claire", 282067789012345, "1982-06-20", 2, 2, 2),
    ("Robert", "Lucie", 194037789012345, "1994-03-12", 3, 3, 3),
    ("Thomas", "Eric", 276118789012345, "1976-11-08", 4, 4, 4),
    ("Bernard", "Julie", 201225789012345, "2001-12-25", 5, 5, 5),
    ("Lefevre", "Marion", 293041789012345, "1993-04-01", 1, 2, 3),
    ("Moreau", "Antoine", 189022789012312, "1989-02-02", 2, 1, 5),
    ("Simon", "Patrick", 270111789045678, "1970-11-11", 3, 3, 2),
    ("Laurent", "Sophie", 298073789013579, "1998-07-03", 4, 5, 4),
    ("Gonzalez", "Aline", 186095789085421, "1986-09-05", 5, 4, 1),
    ("Roussel", "Karim", 277124789032514, "1977-12-04", 1, 2, 5),
    ("Fontaine", "Mélanie", 201018789014785, "2001-01-18", 2, 3, 2),
    ("Caron", "Bruno", 266041789017452, "1966-04-01", 3, 1, 3),
    ("Gaillard", "Camille", 193033789026897, "1993-03-03", 4, 2, 1),
    ("Boucher", "Hélène", 284082789054123, "1984-08-02", 5, 5, 4),
    ("Perrin", "Louis", 291052789071456, "1991-05-02", 1, 4, 3),
    ("Dufour", "Nina", 203022789011234, "2003-02-02", 2, 3, 5),
    ("Blanc", "Aurélien", 275101789036541, "1975-10-01", 3, 2, 1),
    ("Rodriguez", "Clara", 297091789024789, "1997-09-01", 4, 1, 4),
    ("Lopez", "Esteban", 190061789014753, "1990-06-01", 5, 5, 2);


INSERT INTO Ordonnance(ordo_date, ordo_nom_medecin, ordo_nom_patient, Id_Medecin, Id_Patient) VALUES
    ("2024-01-10", "Lambert Pierre", "Dupont Jean", 1, 1),
    ("2024-02-05", "Girard Julie", "Martin Claire", 2, 2),
    ("2024-03-20", "Faure Nicolas", "Robert Lucie", 3, 3),
    ("2024-04-11", "Marchand Elise", "Thomas Eric", 4, 4),
    ("2024-05-03", "Picard Hugo", "Bernard Julie", 5, 5),
    ("2024-06-12", "Lambert Pierre", "Lefevre Marion", 1, 6),
    ("2024-06-25", "Girard Julie", "Moreau Antoine", 2, 7),
    ("2024-07-03", "Faure Nicolas", "Simon Patrick", 3, 8),
    ("2024-07-18", "Marchand Elise", "Laurent Sophie", 4, 9),
    ("2024-07-29", "Picard Hugo", "Gonzalez Aline", 5, 10),
    ("2024-08-04", "Lambert Pierre", "Roussel Karim", 1, 11),
    ("2024-08-19", "Girard Julie", "Fontaine Mélanie", 2, 12),
    ("2024-09-02", "Faure Nicolas", "Caron Bruno", 3, 13),
    ("2024-09-15", "Marchand Elise", "Gaillard Camille", 4, 14),
    ("2024-10-01", "Picard Hugo", "Boucher Hélène", 5, 15),
    ("2024-10-12", "Lambert Pierre", "Perrin Louis", 1, 16),
    ("2024-10-27", "Girard Julie", "Dufour Nina", 2, 17),
    ("2024-11-06", "Faure Nicolas", "Blanc Aurélien", 3, 18),
    ("2024-11-21", "Marchand Elise", "Rodriguez Clara", 4, 19),
    ("2024-12-03", "Picard Hugo", "Lopez Esteban", 5, 20);


INSERT INTO Commande(com_date_commande, com_nom_medecin, com_nom_patient, com_quantite, com_prix, Id_Patient, Id_Ordonnance, Id_Pharmacie) VALUES
    ("2024-01-12", "Lambert Pierre", "Dupont Jean", 2, 5.00, 1, 1, 1),
    ("2024-02-06", "Girard Julie", "Martin Claire", 1, 3.40, 2, 2, 2),
    ("2024-03-22", "Faure Nicolas", "Robert Lucie", 3, 6.60, 3, 3, 3),
    ("2024-04-13", "Marchand Elise", "Thomas Eric", 1, 4.30, 4, 4, 4),
    ("2024-05-05", "Picard Hugo", "Bernard Julie", 2, 17.20, 5, 5, 5),
    ("2024-06-14", "Lambert Pierre", "Lefevre Marion", 1, 2.50, 6, 1, 1),
    ("2024-06-26", "Girard Julie", "Moreau Antoine", 2, 6.80, 7, 2, 2),
    ("2024-07-04", "Faure Nicolas", "Simon Patrick", 1, 3.40, 8, 3, 3),
    ("2024-07-19", "Marchand Elise", "Laurent Sophie", 3, 12.90, 9, 4, 4),
    ("2024-07-30", "Picard Hugo", "Gonzalez Aline", 2, 17.20, 10, 5, 5),
    ("2024-08-06", "Lambert Pierre", "Roussel Karim", 1, 2.50, 11, 1, 1),
    ("2024-08-20", "Girard Julie", "Fontaine Mélanie", 2, 6.80, 12, 2, 2),
    ("2024-09-03", "Faure Nicolas", "Caron Bruno", 1, 3.40, 13, 3, 3),
    ("2024-09-16", "Marchand Elise", "Gaillard Camille", 2, 8.60, 14, 4, 4),
    ("2024-10-02", "Picard Hugo", "Boucher Hélène", 3, 25.80, 15, 5, 5),
    ("2024-10-13", "Lambert Pierre", "Perrin Louis", 1, 2.50, 16, 1, 1),
    ("2024-10-28", "Girard Julie", "Dufour Nina", 2, 6.80, 17, 2, 2),
    ("2024-11-07", "Faure Nicolas", "Blanc Aurélien", 1, 3.40, 18, 3, 3),
    ("2024-11-22", "Marchand Elise", "Rodriguez Clara", 2, 8.60, 19, 4, 4),
    ("2024-12-04", "Picard Hugo", "Lopez Esteban", 3, 25.80, 20, 5, 5);


INSERT INTO contenir(Id_Commande, Id_Stock_Medicament, total_achete, prix_achat) VALUES
    (1, 1, 2, 5),
    (2, 2, 1, 3),
    (3, 3, 3, 7),
    (4, 4, 1, 4),
    (5, 5, 2, 17),
    (6, 1, 1, 2),
    (7, 2, 2, 7),
    (8, 3, 1, 3),
    (9, 4, 2, 8),
    (10, 5, 3, 26),
    (11, 1, 1, 2),
    (12, 2, 2, 7),
    (13, 3, 1, 3),
    (14, 4, 2, 8),
    (15, 5, 3, 26),
    (16, 6, 1, 5),
    (17, 7, 2, 6),
    (18, 8, 1, 4),
    (19, 9, 1, 7),
    (20, 10, 2, 12);

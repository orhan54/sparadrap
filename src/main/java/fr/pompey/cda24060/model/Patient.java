package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class Patient extends Personne {
    // Attributs
    private int Id_Patient;
    private String numeroSecuriteSociale;
    private Date patDateNaissance;

    // Formatter pour les dates au format dd/MM/yyyy
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Constructeur vide pour les DAO
     */
    public Patient() {
        super();
    }

    // Constructeur principal
    public Patient(String nom,
                   String prenom,
                   LocalDate dateNaissance,
                   Lieu lieu,
                   Mutuelle mutuelle,
                   Medecin medecin) throws SaisieException {
        super(nom, prenom, lieu, mutuelle, medecin);
        this.patDateNaissance = Date.valueOf(dateNaissance);
        this.numeroSecuriteSociale = generateNumSecu();
    }

    // Constructeur alternatif pour DAO (numéro de sécu existant)
    public Patient(String nom,
                   String prenom,
                   Date dateNaissance,
                   Lieu lieu,
                   Mutuelle mutuelle,
                   Medecin medecin,
                   String numeroSecu) throws SaisieException {
        super(nom, prenom, lieu, mutuelle, medecin);
        this.patDateNaissance = dateNaissance;
        this.numeroSecuriteSociale = numeroSecu;
    }

    // Génération d'un numéro de sécurité sociale aléatoire
    private static String generateNumSecu() {
        Random random = new Random();
        StringBuilder num = new StringBuilder();
        num.append(random.nextInt(2) + 1); // 1 ou 2 pour sexe
        for (int i = 0; i < 14; i++) {
            num.append(random.nextInt(10));
        }
        return num.toString();
    }

    // Getters et Setters

    /**
     * Gets id patient.
     *
     * @return the id patient
     */
    public int getId_Patient() {
        return Id_Patient;
    }

    /**
     * Sets id patient.
     *
     * @param id the id
     */
    public void setId_Patient(int id) {
        this.Id_Patient = id;
    }

    /**
     * Gets pat date naissance.
     *
     * @return the pat date naissance
     */
    public Date getPatDateNaissance() {
        return this.patDateNaissance;
    }

    /**
     * Gets date naissance formatée dd/MM/yyyy.
     *
     * @return the pat date naissance formatee
     */
    public String getPatDateNaissanceFormatee() {
        if (this.patDateNaissance != null) {
            return this.patDateNaissance.toLocalDate().format(DATE_FORMATTER);
        }
        return "";
    }

    /**
     * Sets pat date naissance.
     *
     * @param pDateNaissance the p date naissance
     * @throws SaisieException the saisie exception
     */
    public void setPatDateNaissance(Date pDateNaissance) throws SaisieException {
        if (pDateNaissance == null) {
            throw new SaisieException("Date de naissance invalide !");
        }
        this.patDateNaissance = pDateNaissance;
    }

    /**
     * Sets date naissance depuis format dd/MM/yyyy.
     *
     * @param dateString the date string
     * @throws SaisieException the saisie exception
     */
    public void setPatDateNaissanceFromString(String dateString) throws SaisieException {
        try {
            LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
            this.patDateNaissance = Date.valueOf(date);
        } catch (Exception e) {
            throw new SaisieException("Format de date invalide. Utilisez dd/MM/yyyy");
        }
    }

    /**
     * Gets pat nume secu.
     *
     * @return the pat nume secu
     */
    public String getPatNumeSecu() {
        return numeroSecuriteSociale;
    }

    /**
     * Sets pat num secu.
     *
     * @param numero the numero
     * @throws SaisieException the saisie exception
     */
    public void setPatNumSecu(String numero) throws SaisieException {
        if (numero == null || numero.length() != 15) {
            throw new SaisieException("Numéro de sécurité sociale invalide : " + numero);
        }
        this.numeroSecuriteSociale = numero;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Patient :\n");
        sb.append("- Nom : ").append(getNom()).append("\n");
        sb.append("- Prénom : ").append(getPrenom()).append("\n");
        sb.append("- Numéro de Sécurité Sociale : ").append(numeroSecuriteSociale).append("\n");
        sb.append("- Date de naissance : ").append(getPatDateNaissanceFormatee()).append("\n");

        sb.append("\n--- Adresse ---\n");
        if (getLieu() != null) {
            sb.append(getLieu().toString());
        } else {
            sb.append("Aucune adresse enregistrée\n");
        }

        sb.append("\n--- Mutuelle ---\n");
        if (getMutuelle() != null) {
            sb.append("- Mutuelle : ").append(getMutuelle().getNom()).append("\n");
        } else {
            sb.append("Aucune mutuelle enregistrée\n");
        }

        sb.append("\n--- Médecin référent ---\n");
        if (getMedecin() != null) {
            sb.append("- Nom du médecin : ").append(getMedecin().getNom()).append("\n");
        } else {
            sb.append("Aucun médecin déclaré\n");
        }

        return sb.toString();
    }
}
package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Patient extends Personne {
    // Attributs
    private int Id_Patient;
    private String numeroSecuriteSociale;
    private Date patDateNaissance;

    // Liste statique pour stocker tous les patients
    private static List<Patient> patients = new ArrayList<>();

    // Constructeur principal
    public Patient(String nom,
                   String prenom,
                   LocalDate dateNaissance,
                   Lieu lieu,
                   Mutuelle mutuelle,
                   Medecin medecin) throws SaisieException {

        super(nom, prenom, lieu, mutuelle, medecin);

        this.setPatDateNaissance(Date.valueOf(dateNaissance));
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

        this.setPatDateNaissance(Date.valueOf(dateNaissance.toLocalDate()));
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

    // Liste statique - getter
    public static List<Patient> getPatients() {
        return patients;
    }

    /**
     * Gets id patient.
     *
     * @return the id patient
     */
// Getters et Setters
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
     * Gets date naissance.
     *
     * @return the date naissance
     */
    public LocalDate getPatDateNaissance() {
        return this.patDateNaissance.toLocalDate();
    }

    /**
     * Sets date naissance.
     *
     * @param pDateNaissance the date naissance
     * @throws SaisieException the saisie exception
     */
    public void setPatDateNaissance(Date pDateNaissance) throws SaisieException {
        if (pDateNaissance == null) {
            throw new SaisieException("Date de naissance invalide !");
        }
        this.patDateNaissance = pDateNaissance;
    }

    /**
     * Gets numero securite sociale.
     *
     * @return the numero securite sociale
     */
    public String getPatNumeSecu() {
        return numeroSecuriteSociale;
    }

    /**
     * Sets numero securite sociale.
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
        sb.append("- Date de naissance : ").append(patDateNaissance).append("\n");

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

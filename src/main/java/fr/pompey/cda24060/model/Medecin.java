package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.numAgreementValide;

public class Medecin extends Personne {

    private int Id_Medecin;
    private String medNumeroAgreement;
    private List<Patient> patients = new ArrayList<>();

    // Liste statique pour tous les médecins
    private static List<Medecin> medecins = new ArrayList<>();

    /**
     * Constructeur vide pour les DAO
     */
    public Medecin() {
        super();
    }

    /**
     * Constructeur principal
     *
     * @param pMedNom             the p nom
     * @param pMedPrenom          the p prenom
     * @param pMedNumeroAgreement the p numero agreement
     * @param lieu             the lieu
     * @throws SaisieException the saisie exception
     */
    public Medecin(String pMedNom, String pMedPrenom, String pMedNumeroAgreement, Lieu lieu) throws SaisieException {
        super(pMedNom, pMedPrenom, lieu);
        this.setNumeroAgreement(pMedNumeroAgreement);

        // Ajouter ce médecin à la liste statique
        medecins.add(this);
    }

    // Getter statique pour la liste des médecins
    /**
     * Gets medecins.
     *
     * @return the medecins
     */
    public static List<Medecin> getMedecins() {
        return medecins;
    }

    // Getter & Setter
    /**
     * Gets id medecin.
     *
     * @return the id medecin
     */
    public int getId_Medecin() { return Id_Medecin; }

    /**
     * Sets id medecin.
     *
     * @param id_Medecin the id medecin
     */
    public void setId_Medecin(int id_Medecin) { Id_Medecin = id_Medecin; }

    /**
     * Gets numero agreement.
     *
     * @return the numero agreement
     */
    public String getMedNumeroAgreement() { return medNumeroAgreement; }

    /**
     * Sets numero agreement.
     *
     * @param pMedNumeroAgreement the p numero agreement
     * @throws SaisieException the saisie exception
     */
    public void setNumeroAgreement(String pMedNumeroAgreement) throws SaisieException {
        if (pMedNumeroAgreement == null || !numAgreementValide(pMedNumeroAgreement)) {
            throw new SaisieException("Erreur sur numéro d'agrément : " + pMedNumeroAgreement);
        }
        this.medNumeroAgreement = pMedNumeroAgreement;
    }

    /**
     * Gets patients.
     *
     * @return the patients
     */
    public List<Patient> getPatients() { return patients; }

    /**
     * Sets patients.
     *
     * @param patients the patients
     */
    public void setPatients(List<Patient> patients) { this.patients = patients; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Médecin :\n");
        sb.append("- Nom : ").append(getNom()).append("\n");
        sb.append("- Prénom : ").append(getPrenom()).append("\n");
        sb.append("- Numéro d'agrément : ").append(medNumeroAgreement).append("\n");
        if (getLieu() != null) {
            sb.append(getLieu().toString());
        }
        return sb.toString();
    }
}

package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.numAgreementValide;

public class Medecin extends Personne {

    private int Id_Medecin;
    private String numeroAgreement;
    private List<Patient> patients = new ArrayList<>();

    // ✅ Liste statique pour tous les médecins
    private static List<Medecin> medecins = new ArrayList<>();

    /**
     * Constructeur vide pour les DAO
     */
    public Medecin() {
        super();
    }

    /**
     * Constructeur principal
     */
    public Medecin(String pNom, String pPrenom, String pNumeroAgreement, Lieu lieu) throws SaisieException {
        super(pNom, pPrenom, lieu);
        this.setNumeroAgreement(pNumeroAgreement);

        // ✅ Ajouter ce médecin à la liste statique
        medecins.add(this);
    }

    // Getter & Setter
    public int getId_Medecin() { return Id_Medecin; }
    public void setId_Medecin(int id_Medecin) { Id_Medecin = id_Medecin; }

    public String getNumeroAgreement() { return numeroAgreement; }
    public void setNumeroAgreement(String pNumeroAgreement) throws SaisieException {
        if (pNumeroAgreement == null || !numAgreementValide(pNumeroAgreement)) {
            throw new SaisieException("Erreur sur numéro d'agrément : " + pNumeroAgreement);
        }
        this.numeroAgreement = pNumeroAgreement;
    }

    public List<Patient> getPatients() { return patients; }
    public void setPatients(List<Patient> patients) { this.patients = patients; }

    // Getter statique pour la liste des médecins
    public static List<Medecin> getMedecins() {
        return medecins;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Médecin :\n");
        sb.append("- Nom : ").append(getNom()).append("\n");
        sb.append("- Prénom : ").append(getPrenom()).append("\n");
        sb.append("- Numéro d'agrément : ").append(numeroAgreement).append("\n");
        if (getLieu() != null) {
            sb.append(getLieu().toString());
        }
        return sb.toString();
    }
}

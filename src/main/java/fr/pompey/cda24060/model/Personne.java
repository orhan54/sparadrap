package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

public abstract class Personne {
    private String nom;
    private String prenom;
    private Lieu lieu;
    private Mutuelle mutuelle;
    private Medecin medecin;

    /**
     * Constructeur vide pour les DAO
     */
    public Personne() {
        // Constructeur vide
    }

    /**
     * Constructeur pour Medecin (sans mutuelle ni medecin)
     */
    public Personne(String nom, String prenom, Lieu lieu) throws SaisieException {
        this.setNom(nom);
        this.setPrenom(prenom);
        this.setLieu(lieu);
    }

    /**
     * Constructeur complet pour Patient
     */
    public Personne(String nom, String prenom, Lieu lieu, Mutuelle mutuelle, Medecin medecin) throws SaisieException {
        this.setNom(nom);
        this.setPrenom(prenom);
        this.setLieu(lieu);
        this.setMutuelle(mutuelle);
        this.setMedecin(medecin);
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String pNom) throws SaisieException {
        if (pNom == null || !regexAlpha(pNom)) {
            throw new SaisieException("Erreur sur le nom : " + pNom);
        }
        this.nom = pNom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String pPrenom) throws SaisieException {
        if (pPrenom == null || !regexAlpha(pPrenom)) {
            throw new SaisieException("Erreur sur le prénom : " + pPrenom);
        }
        this.prenom = pPrenom;
    }

    public Lieu getLieu() {
        return lieu;
    }

    public void setLieu(Lieu pLieu) {
        this.lieu = pLieu;
    }

    public Mutuelle getMutuelle() {
        return mutuelle;
    }

    public void setMutuelle(Mutuelle pMutuelle) {
        this.mutuelle = pMutuelle;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin pMedecin) {
        this.medecin = pMedecin;
    }

    @Override
    public abstract String toString();
}
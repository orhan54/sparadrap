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

    /**
     * Gets nom.
     *
     * @return the nom
     */
    public String getNom() {
        return nom;
    }

    /**
     * Sets nom.
     *
     * @param pNom the p nom
     * @throws SaisieException the saisie exception
     */
    public void setNom(String pNom) throws SaisieException {
        if (pNom == null || !regexAlpha(pNom)) {
            throw new SaisieException("Erreur sur le nom : " + pNom);
        }
        this.nom = pNom;
    }

    /**
     * Gets prenom.
     *
     * @return the prenom
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Sets prenom.
     *
     * @param pPrenom the p prenom
     * @throws SaisieException the saisie exception
     */
    public void setPrenom(String pPrenom) throws SaisieException {
        if (pPrenom == null || !regexAlpha(pPrenom)) {
            throw new SaisieException("Erreur sur le prénom : " + pPrenom);
        }
        this.prenom = pPrenom;
    }

    /**
     * Gets lieu.
     *
     * @return the lieu
     */
    public Lieu getLieu() {
        return lieu;
    }

    /**
     * Sets lieu.
     *
     * @param pLieu the p lieu
     */
    public void setLieu(Lieu pLieu) {
        this.lieu = pLieu;
    }

    /**
     * Gets mutuelle.
     *
     * @return the mutuelle
     */
    public Mutuelle getMutuelle() {
        return mutuelle;
    }

    /**
     * Sets mutuelle.
     *
     * @param pMutuelle the p mutuelle
     */
    public void setMutuelle(Mutuelle pMutuelle) {
        this.mutuelle = pMutuelle;
    }

    /**
     * Gets medecin.
     *
     * @return the medecin
     */
    public Medecin getMedecin() {
        return medecin;
    }

    /**
     * Sets medecin.
     *
     * @param pMedecin the p medecin
     */
    public void setMedecin(Medecin pMedecin) {
        this.medecin = pMedecin;
    }

    @Override
    public abstract String toString();
}
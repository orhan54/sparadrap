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
     *
     * @param pNom    the p nom
     * @param pPrenom the p prenom
     * @param lieu    the lieu
     * @throws SaisieException the saisie exception
     */
    public Personne(String pNom, String pPrenom, Lieu lieu) throws SaisieException {
        this.setNom(pNom);
        this.setPrenom(pPrenom);
        this.setLieu(lieu);
    }

    /**
     * Constructeur complet pour Patient
     *
     * @param pNom     the p nom
     * @param pPrenom  the p prenom
     * @param lieu     the lieu
     * @param mutuelle the mutuelle
     * @param medecin  the medecin
     * @throws SaisieException the saisie exception
     */
    public Personne(String pNom, String pPrenom, Lieu lieu, Mutuelle mutuelle, Medecin medecin) throws SaisieException {
        this.setNom(pNom);
        this.setPrenom(pPrenom);
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
     * @param lieu the p lieu
     */
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
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
     * @param mutuelle the p mutuelle
     */
    public void setMutuelle(Mutuelle mutuelle) {
        this.mutuelle = mutuelle;
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
     * @param medecin the p medecin
     */
    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    @Override
    public abstract String toString();
}
package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

public class Pharmacie {
    // Attribut de la classe Pharmacie
    private int Id_Pharmacie;
    private String phaNom, phaPrenom;
    private Lieu lieu;

    // Constructeur de la classe Pharmacie

    /**
     * Instantiates a new Pharmacie.
     *
     * @param pPhaNom    the p pha nom
     * @param pPhaPrenom the p pha prenom
     * @param lieu       the lieu
     * @throws SaisieException the saisie exception
     */
    public Pharmacie(String pPhaNom, String pPhaPrenom, Lieu lieu) throws SaisieException {
        this.setPhaNom(pPhaNom);
        this.setPhaPrenom(pPhaPrenom);
        this.lieu = lieu;
    }

    public Pharmacie(String pPhaNom, String pPhaPrenom) throws SaisieException {
        this.setPhaNom(pPhaNom);
        this.setPhaPrenom(pPhaPrenom);
    }

    // Setters et Getters de la classe Pharmacie
    /**
     * Gets id pharmacie.
     *
     * @return the id pharmacie
     */
    public int getId_Pharmacie() {
        return Id_Pharmacie;
    }

    /**
     * Sets id pharmacie.
     *
     * @param id_Pharmacie the id pharmacie
     */
    public void setId_Pharmacie(int id_Pharmacie) {
        Id_Pharmacie = id_Pharmacie;
    }

    /**
     * Gets pha nom.
     *
     * @return the pha nom
     */
    public String getPhaNom() {
        return phaNom;
    }

    /**
     * Sets pha nom.
     *
     * @param pPhaNom the p pha nom
     * @throws SaisieException the saisie exception
     */
    public void setPhaNom(String pPhaNom) throws SaisieException {
        if (!regexAlpha(pPhaNom)) {
            throw new SaisieException("Erreur sur le nom du pharmacien : " + pPhaNom);
        } else {
            this.phaNom = pPhaNom;
        }
    }

    /**
     * Gets pha prenom.
     *
     * @return the pha prenom
     */
    public String getPhaPrenom() {
        return phaPrenom;
    }

    /**
     * Sets pha prenom.
     *
     * @param pPhaPrenom the p pha prenom
     * @throws SaisieException the saisie exception
     */
    public void setPhaPrenom(String pPhaPrenom) throws SaisieException {
        if (!regexAlpha(pPhaPrenom)) {
            throw new SaisieException("Erreur sur le prenom du pharmacien : " + pPhaPrenom);
        } else {
            this.phaPrenom = pPhaPrenom;
        }
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
     * @param lieu the lieu
     */
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    // StringBuilder pour afficher le toString de Pharmacie
    @Override
    public String toString() {
        StringBuilder sbo = new StringBuilder();
        sbo.append("Le nom du pharmacien est ").append(phaNom).append("\n");
        sbo.append("Le prenom du pharmacien est ").append(phaPrenom).append("\n");

        return sbo.toString();
    }
}

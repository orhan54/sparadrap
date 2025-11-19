package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

public class Pharmacie {
    // Attribut de la classe Pharmacie
    private int Id_Pharmacie;
    private String phaNom, phaPrenom;

    /**
     * Instantiates a new Pharmacie.
     *
     * @param pPhaNom    the p pha nom
     * @param pPhaPrenom the p pha prenom
     * @throws SaisieException the saisie exception
     */
// Constructeur de la classe Pharmacie
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
     * Gets prenom.
     *
     * @return the prenom
     */
    public String getPrenom() {
        return phaPrenom;
    }

    /**
     * Sets prenom.
     *
     * @param pPhaPrenom the p pha prenom
     * @throws SaisieException the saisie exception
     */
    public void setPhaPrenom(String pPhaPrenom) throws SaisieException {
        if(!regexAlpha(pPhaPrenom) && pPhaPrenom.isEmpty()){
            throw new SaisieException("Error sur le nom de la pharmacie : " + pPhaPrenom);
        }else{
            this.phaNom = pPhaPrenom;
        }
    }

    /**
     * Gets nom.
     *
     * @return the nom pharmacie
     */
    public String getPhaNom() {
        return this.phaNom;
    }

    /**
     * Sets nom.
     *
     * @param pPhaNom the p pha nom
     * @throws SaisieException the saisie exception
     */
    public void setPhaNom(String pPhaNom) throws SaisieException {
        if(!regexAlpha(pPhaNom) && pPhaNom.isEmpty()){
            throw new SaisieException("Erreur sur le nom de la pharmacie : " + pPhaNom);
        }else{
            this.phaNom = pPhaNom;
        }
    }

    // StringBuilder pour afficher le toString de Pharmacie
    @Override
    public String toString() {
        StringBuilder sbo = new StringBuilder();
        sbo.append("Le nom du pharmacien est ").append(this.phaNom).append("\n");
        sbo.append("Le prenom du pharmacien est ").append(this.phaPrenom).append("\n");

        return sbo.toString();
    }
}

package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

public class Pharmacie extends Personne {
    // Attribut de la classe Pharmacie
    private int Id_Pharmacie;

    // Constructeur de la classe Pharmacie

    /**
     * Instantiates a new Pharmacie.
     *
     * @param pNom    the p nom
     * @param pPrenom the p prenom
     * @param lieu    the lieu
     * @throws SaisieException the saisie exception
     */
    public Pharmacie(String pNom, String pPrenom, Lieu lieu) throws SaisieException {
        super(pNom, pPrenom, lieu);
    }

    /**
     * Instantiates a new Pharmacie.
     *
     * @param pNom    the p nom
     * @param pPrenom the p prenom
     * @throws SaisieException the saisie exception
     */
    public Pharmacie(String pNom, String pPrenom) throws SaisieException {
        super(pNom, pPrenom, null);
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

    // StringBuilder pour afficher le toString de Pharmacie
    @Override
    public String toString() {
        StringBuilder sbo = new StringBuilder();
        sbo.append("Le nom du pharmacien est ").append(getNom()).append("\n");
        sbo.append("Le prenom du pharmacien est ").append(getPrenom()).append("\n");

        return sbo.toString();
    }
}

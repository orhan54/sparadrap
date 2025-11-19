package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

/**
 * The type Personne.
 */
public class Personne {
    private String nom, prenom;
    private Lieu lieu;
    private Mutuelle mutuelle;
    private Medecin medecin;

    /**
     * Instantiates a new Personne.
     *
     * @param pNom     the nom
     * @param pPrenom  the prenom
     * @param lieu     the lieu
     * @param mutuelle the mutuelle
     * @throws SaisieException the saisie exception
     */
    // constructeur de lma classe personne
    public Personne(String pNom, String pPrenom, Lieu lieu,Mutuelle mutuelle) throws SaisieException {
        this.setNom(pNom);
        this.setPrenom(pPrenom);
        this.setLieu(lieu);
        this.setMutuelle(mutuelle);
        this.setMedecin(medecin);
    }

    /**
     * Instantiates a new Personne.
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
     * Instantiates a new Personne.
     *
     * @throws SaisieException the saisie exception
     */
    public Personne() throws SaisieException {}

    // Getters & Setters

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
     * @throws SaisieException the saisie exception
     */
    public void setNom(String pNom) throws SaisieException {
        if (!regexAlpha(pNom)){
            throw new SaisieException("Le nom n'est pas valide !");
        }else{
            this.nom = pNom;
        }
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
     * @throws SaisieException the saisie exception
     */
    public void setPrenom(String pPrenom) throws SaisieException {
        if (!regexAlpha(pPrenom)){
            throw new SaisieException("Le prenom n'est pas valide !");
        }else{
            this.prenom = pPrenom;
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
     * @param mutuelle the mutuelle
     */
    public void setMutuelle(Mutuelle mutuelle) {
        this.mutuelle = mutuelle;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    // StringBuilder pour afficher le toString de Personne
    @Override
    public String toString() {
        StringBuilder sbper = new StringBuilder();
        sbper.append("- Nom : ").append(getNom()).append("\n");
        sbper.append("- Prenom : ").append(getPrenom()).append("\n");
        if (getLieu() != null) {
            sbper.append(getLieu().toString());
        }
        if (getMutuelle() != null) {
            sbper.append(getMutuelle().getNom()).append("\n");
        }
        return sbper.toString();
    }
}

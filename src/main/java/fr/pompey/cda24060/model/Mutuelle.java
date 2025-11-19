package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.positifInt;
import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

public class Mutuelle {

    // Attribut de la classe Mutuelle
    private String mutNom;
    private double tauxPriseEnCharge;
    private int Id_Mutuelle ,mutNumDepartement;
    private Lieu lieu;

    // List des mutuelles
    private static List<Mutuelle> mutuelles = new ArrayList<Mutuelle>();

    /**
     * Instantiates a new Mutuelle.
     *
     * @param pMutNom               the nom mutuelle
     * @param pTauxPriseEnCharge the taux prise en charge
     * @param pNumDepartement       the departement
     * @param lieu               the Lieu lieu
     * @throws SaisieException   the saisie exception
     */
    // Constucteur qui extends de la classe Lieu
    public Mutuelle(String pMutNom, double pTauxPriseEnCharge, int pNumDepartement, Lieu lieu) throws SaisieException {
        this.setMutNom(pMutNom);
        this.setTauxPriseEnCharge(pTauxPriseEnCharge);
        this.setMutNumDepartement(pNumDepartement);
        this.setLieu(lieu);
    }

    // Getters et Setters


    /**
     * Gets id mutuelle.
     *
     * @return the id mutuelle
     */
    public int getId_Mutuelle() {
        return Id_Mutuelle;
    }

    /**
     * Sets id mutuelle.
     *
     * @param id_mutuelle the id mutuelle
     */
    public void setId_Mutuelle(int id_mutuelle) {
        Id_Mutuelle = id_mutuelle;
    }

    /**
     * Gets mutuelles.
     *
     * @return the mutuelles
     */
    // Afficher la list des Mutuelles
    public static List<Mutuelle> getMutuelles() {
        return mutuelles;
    }

    /**
     * Gets nom.
     *
     * @return the nom mutuelle
     */
    public  String getNom() {
        return this.mutNom;
    }

    /**
     * Sets nom.
     *
     * @param pMutNom the p mut nom
     * @throws SaisieException the saisie exception
     */
    public void setMutNom(String pMutNom) throws SaisieException {
        if (!regexAlpha(pMutNom) && !pMutNom.isEmpty()) {
            throw new SaisieException("Error sur le nom de la mutuelle : " + pMutNom);
        }else{
            this.mutNom = pMutNom;
        }
    }

    /**
     * Gets taux prise en charge.
     *
     * @return the taux prise en charge
     */
    public double getTauxPriseEnCharge() {
        return this.tauxPriseEnCharge;
    }

    /**
     * Sets taux prise en charge.
     *
     * @param pTauxPriseEnCharge the taux prise en charge
     * @throws SaisieException the saisie exception
     */
    public void setTauxPriseEnCharge(double pTauxPriseEnCharge) throws SaisieException {
        if (!positifInt(String.valueOf(pTauxPriseEnCharge)) && pTauxPriseEnCharge < 0){
            throw new SaisieException("Error sur le taux de prise en charge : " + pTauxPriseEnCharge);
        }else{
            this.tauxPriseEnCharge = pTauxPriseEnCharge;
        }
    }

    /**
     * Gets departement.
     *
     * @return the departement
     */
    public int getMutNumDepartement() {
        return this.mutNumDepartement;
    }

    /**
     * Sets departement.
     *
     * @param pNumDepartement      the departement
     * @throws SaisieException  the saisie exception
     */
    public void setMutNumDepartement(int pNumDepartement) throws SaisieException {
        if (!positifInt(String.valueOf(pNumDepartement)) && pNumDepartement < 0){
            throw new SaisieException("Error sur le numéro de departement : " + pNumDepartement);
        }else{
            this.mutNumDepartement = pNumDepartement;
        }
    }

    /**
     * Gets lieu.
     *
     * @return the Lieu lieu
     */
    public Lieu getLieu() {
        return lieu;
    }

    /**
     * Sets lieu.
     *
     * @param lieu the Lieu lieu
     */
    public void setLieu(Lieu lieu) {
        this.lieu = lieu;
    }

    // StringBuilder pour afficher le toString de Mutuelle
    @Override
    public String toString() {
        StringBuilder sbm = new StringBuilder();
        sbm.append("Mutuelle : ").append("\n");
        sbm.append("- Nom : ").append(this.getNom()).append("\n");
        if (getLieu() != null) {
            sbm.append(getLieu().toString());
        }
        sbm.append("- Departement : ").append(this.getMutNumDepartement()).append("\n");
        sbm.append("- Taux de prise en charge : ").append(this.getTauxPriseEnCharge()).append("\n");
        return sbm.toString();
    }
}

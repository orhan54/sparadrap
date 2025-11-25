package fr.pompey.cda24060.model;

import com.google.protobuf.StringValue;
import fr.pompey.cda24060.exception.SaisieException;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.*;

public class Stock_Medicament {
    // Attibut pour la classe Medicament
    private int Id_Stock_Medicament, medicQuantite;
    private String medicNom, medicCategorie;
    private Date medicDateEntreeStock, medicDateMiseEnService;
    private double medicPrixUnitaire;
    private Pharmacie pharmacie;

    // List des medicaments 
    private static List<Stock_Medicament> medicaments = new ArrayList<Stock_Medicament>();

    /**
     * Instantiates a new Medicament.
     *
     * @param pMedicQuantite          the p medic quantite
     * @param pMedicDateMiseEnService the p medic date mise en service
     * @param pMedicPrixUnitaire      the prix
     * @param pMedicNom               the nom
     * @param pMedicDateEntreeStock   the p medic date entree stock
     * @throws SaisieException the saisie exception
     */
// Constucteur de la classe Medicament
    public Stock_Medicament(String pMedicNom,
                            String pMedicCategorie,
                            int pMedicQuantite,
                            Date pMedicDateMiseEnService,
                            Date pMedicDateEntreeStock,
                            double pMedicPrixUnitaire,
                            Pharmacie pPharmacie
                            ) throws SaisieException {
        this.setMedicNom(pMedicNom);
        this.setMedicCategorie(pMedicCategorie);
        this.setMedicQuantite(pMedicQuantite);
        this.setDateMiseEnService(pMedicDateMiseEnService);
        this.setMedicDateEntreeStock(pMedicDateEntreeStock);
        this.setMedicPrixUnitaire(pMedicPrixUnitaire);
        this.setPharmacie(pPharmacie);
    }

    /**
     * Instantiates a new Stock medicament.
     *
     * @param pMedicNom               the p medic nom
     * @param pMedicQuantite          the p medic quantite
     * @param pMedicDateMiseEnService the p medic date mise en service
     * @param pMedicDateEntreeStock   the p medic date entree stock
     * @param pMedicPrixUnitaire      the p medic prix unitaire
     * @throws SaisieException the saisie exception
     */
    public Stock_Medicament(String pMedicNom, int pMedicQuantite ,
                            Date pMedicDateMiseEnService, Date pMedicDateEntreeStock,
                            double pMedicPrixUnitaire) throws SaisieException {
        this.setMedicNom(pMedicNom);
        this.setMedicQuantite(pMedicQuantite);
        this.setDateMiseEnService(pMedicDateMiseEnService);
        this.setMedicDateEntreeStock(pMedicDateEntreeStock);
        this.setMedicPrixUnitaire(pMedicPrixUnitaire);
    }

    // Getters et Setters

    /**
     * Gets id stock medicament.
     *
     * @return the id stock medicament
     */
    public int getId_Stock_Medicament() {
        return Id_Stock_Medicament;
    }

    /**
     * Sets id stock medicament.
     *
     * @param id_Stock_Medicament the id stock medicament
     */
    public void setId_Stock_Medicament(int id_Stock_Medicament) {
        Id_Stock_Medicament = id_Stock_Medicament;
    }

    /**
     * Gets medicaments.
     *
     * @return the medicaments
     */
    // Afficher la list des medicaments
    public static List<Stock_Medicament> getMedicaments() {
        return medicaments;
    }

    /**
     * Gets medic nom.
     *
     * @return the medic nom
     */
    public String getMedicNom() {
        return medicNom;
    }

    /**
     * Sets nom.
     *
     * @param pMedicNom the p medic nom
     * @throws SaisieException the saisie exception
     */
    public void setMedicNom(String pMedicNom) throws SaisieException {
        if (!regexAlpha(pMedicNom) && pMedicNom.isEmpty()) {
            throw new SaisieException("Error sur le nom : " + pMedicNom);
        }else {
            this.medicNom = pMedicNom;
        }
    }

    public String getMedicCategorie() {
        return medicCategorie;
    }

    public void setMedicCategorie(String pMedicCategorie) throws SaisieException {
        if (!regexAlpha(pMedicCategorie) && pMedicCategorie.isEmpty()) {
            throw new SaisieException("Error sur le nom de la categorie : " + pMedicCategorie);
        } else  {
            this.medicCategorie = pMedicCategorie;
        }
    }

    /**
     * Gets quantite.
     *
     * @return the quantite
     */
    public int getQuantite() {
        return this.medicQuantite;
    }

    /**
     * Sets quantite.
     *
     * @param pMedicQuantite the p medic quantite
     * @throws SaisieException the saisie exception
     */
    public void setMedicQuantite(int pMedicQuantite) throws SaisieException {
        if (!positifInt(String.valueOf(pMedicQuantite)) && pMedicQuantite < 0) {
            throw new SaisieException("Error sur la quantité des médicaments : " + pMedicQuantite);
        }else{
            this.medicQuantite = pMedicQuantite;
        }
    }

    /**
     * Gets date mise en service.
     *
     * @return the date mise en service
     */
    public Date getDateMiseEnService() {
        return this.medicDateMiseEnService;
    }

    /**
     * Sets date mise en service.
     *
     * @param pMedicDateMiseEnService    the date mise en service
     * @throws SaisieException      the saisie exception
     */
    public void setDateMiseEnService(Date pMedicDateMiseEnService) throws SaisieException {
        if(!dateValide(String.valueOf(pMedicDateMiseEnService))){
            throw new SaisieException("Error sur la date de mise en service : " + pMedicDateMiseEnService);
        }else{
            this.medicDateMiseEnService = pMedicDateMiseEnService;
        }
    }

    /**
     * Gets medic date entree stock.
     *
     * @return the medic date entre stock
     */
    public Date getMedicDateEntreeStock() {
        return this.medicDateEntreeStock;
    }

    /**
     * Sets medic date entree stock.
     *
     * @param pMedicDateEntreeStock the p medic date entre stock
     * @throws SaisieException the saisie exception
     */
    public void setMedicDateEntreeStock(Date pMedicDateEntreeStock) throws SaisieException {
        if(!dateValide(String.valueOf(pMedicDateEntreeStock))){
            throw new SaisieException("Error sur la date de mise en service : " + pMedicDateEntreeStock);
        }else{
            this.medicDateEntreeStock = pMedicDateEntreeStock;
        }
    }

    /**
     * Gets prix.
     *
     * @return the prix
     */
    public double getMedicPrixUnitaire() {
        return this.medicPrixUnitaire;
    }

    /**
     * Sets prix.
     *
     * @param pMedicPrixUnitaire             the prix
     * @throws SaisieException  the saisie exception
     */
    public void setMedicPrixUnitaire(double pMedicPrixUnitaire) throws SaisieException {
        if (!positifInt(String.valueOf(pMedicPrixUnitaire)) && pMedicPrixUnitaire < 0) {
            throw new SaisieException("Error sur le prix : " + pMedicPrixUnitaire);
        }else{
            this.medicPrixUnitaire = pMedicPrixUnitaire;
        }
    }

    /**
     * Gets pharmacie.
     *
     * @return the pharmacie
     */
    public Pharmacie getPharmacie() {
        return pharmacie;
    }

    /**
     * Sets pharmacie.
     *
     * @param pPharmacie the pharmacie
     */
    public void setPharmacie(Pharmacie pPharmacie) {
        this.pharmacie = pPharmacie;
    }

    // StringBiulder dans mon toString de la classe Medicament
    public String toString() {
        StringBuilder sbm = new StringBuilder();
        sbm.append("- Nom : ").append(this.medicNom).append("\n");
        sbm.append("- Categorie : ").append(this.medicCategorie).append("\n");
        sbm.append("- Quantité : ").append(this.medicQuantite).append("\n");
        sbm.append("- Date mise en service : ").append(this.medicDateMiseEnService).append("\n");
        sbm.append("- Date entree stock : ").append(this.medicDateEntreeStock).append("\n");
        sbm.append("- Prix unitaire : ").append(this.medicPrixUnitaire).append("\n");

        return sbm.toString();
    }
}

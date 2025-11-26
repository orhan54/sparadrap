package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.sql.Date;
import java.time.format.DateTimeFormatter;

public class Stock_Medicament {
    // Attributs pour la classe Medicament
    private int Id_Stock_Medicament, medicQuantite;
    private String medicNom, medicCategorie;
    private Date medicDateEntreeStock, medicDateMiseEnService;
    private double medicPrixUnitaire;
    private Pharmacie pharmacie;

    // Formatter pour les dates au format dd/MM/yyyy
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Constructeur de la classe Medicament
    public Stock_Medicament(String pMedicNom,
                            String pMedicCategorie,
                            int pMedicQuantite,
                            Date pMedicDateMiseEnService,
                            Date pMedicDateEntreeStock,
                            double pMedicPrixUnitaire,
                            Pharmacie pPharmacie) throws SaisieException {
        this.setMedicNom(pMedicNom);
        this.setMedicCategorie(pMedicCategorie);
        this.setMedicQuantite(pMedicQuantite);
        this.medicDateMiseEnService = pMedicDateMiseEnService;
        this.medicDateEntreeStock = pMedicDateEntreeStock;
        this.setMedicPrixUnitaire(pMedicPrixUnitaire);
        this.setPharmacie(pPharmacie);
    }

    public Stock_Medicament(String pMedicNom,
                            int pMedicQuantite,
                            Date pMedicDateMiseEnService,
                            Date pMedicDateEntreeStock,
                            double pMedicPrixUnitaire) throws SaisieException {
        this.setMedicNom(pMedicNom);
        this.setMedicQuantite(pMedicQuantite);
        this.medicDateMiseEnService = pMedicDateMiseEnService;
        this.medicDateEntreeStock = pMedicDateEntreeStock;
        this.setMedicPrixUnitaire(pMedicPrixUnitaire);
    }

    // Getters et Setters
    public int getId_Stock_Medicament() {
        return Id_Stock_Medicament;
    }

    public void setId_Stock_Medicament(int id_Stock_Medicament) {
        Id_Stock_Medicament = id_Stock_Medicament;
    }

    public String getMedicNom() {
        return medicNom;
    }

    public void setMedicNom(String pMedicNom) throws SaisieException {
        if (pMedicNom == null || pMedicNom.trim().isEmpty()) {
            throw new SaisieException("Nom du médicament invalide");
        }
        this.medicNom = pMedicNom;
    }

    public String getMedicCategorie() {
        return medicCategorie;
    }

    public void setMedicCategorie(String pMedicCategorie) throws SaisieException {
        if (pMedicCategorie == null || pMedicCategorie.trim().isEmpty()) {
            throw new SaisieException("Catégorie du médicament invalide");
        }
        this.medicCategorie = pMedicCategorie;
    }

    public int getQuantite() {
        return this.medicQuantite;
    }

    public void setMedicQuantite(int pMedicQuantite) throws SaisieException {
        if (pMedicQuantite < 0) {
            throw new SaisieException("Quantité invalide : " + pMedicQuantite);
        }
        this.medicQuantite = pMedicQuantite;
    }

    public Date getDateMiseEnService() {
        return this.medicDateMiseEnService;
    }

    public String getDateMiseEnServiceFormatee() {
        if (this.medicDateMiseEnService != null) {
            return this.medicDateMiseEnService.toLocalDate().format(DATE_FORMATTER);
        }
        return "";
    }

    public void setDateMiseEnService(Date pMedicDateMiseEnService) throws SaisieException {
        if (pMedicDateMiseEnService == null) {
            throw new SaisieException("Date de mise en service invalide");
        }
        this.medicDateMiseEnService = pMedicDateMiseEnService;
    }

    public Date getMedicDateEntreeStock() {
        return this.medicDateEntreeStock;
    }

    public String getMedicDateEntreeStockFormatee() {
        if (this.medicDateEntreeStock != null) {
            return this.medicDateEntreeStock.toLocalDate().format(DATE_FORMATTER);
        }
        return "";
    }

    public void setMedicDateEntreeStock(Date pMedicDateEntreeStock) throws SaisieException {
        if (pMedicDateEntreeStock == null) {
            throw new SaisieException("Date d'entrée en stock invalide");
        }
        this.medicDateEntreeStock = pMedicDateEntreeStock;
    }

    public double getMedicPrixUnitaire() {
        return this.medicPrixUnitaire;
    }

    public void setMedicPrixUnitaire(double pMedicPrixUnitaire) throws SaisieException {
        if (pMedicPrixUnitaire < 0) {
            throw new SaisieException("Prix invalide : " + pMedicPrixUnitaire);
        }
        this.medicPrixUnitaire = pMedicPrixUnitaire;
    }

    public Pharmacie getPharmacie() {
        return pharmacie;
    }

    public void setPharmacie(Pharmacie pPharmacie) {
        this.pharmacie = pPharmacie;
    }

    @Override
    public String toString() {
        StringBuilder sbm = new StringBuilder();
        sbm.append("- Nom : ").append(this.medicNom).append("\n");
        sbm.append("- Catégorie : ").append(this.medicCategorie).append("\n");
        sbm.append("- Quantité : ").append(this.medicQuantite).append("\n");
        sbm.append("- Date mise en service : ").append(getDateMiseEnServiceFormatee()).append("\n");
        sbm.append("- Date entrée stock : ").append(getMedicDateEntreeStockFormatee()).append("\n");
        sbm.append("- Prix unitaire : ").append(this.medicPrixUnitaire).append(" €\n");
        return sbm.toString();
    }
}
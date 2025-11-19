package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;
import java.util.ArrayList;
import java.util.List;
import static fr.pompey.cda24060.utility.RegexUtility.positifInt;

/**
 * The type Contenir.
 * Classe d'association entre Commande et StockMedicament
 */
public class Contenir {

    private Commande commande;
    private Stock_Medicament stockMedicament;
    private int totalAchete, prixAchat;
    private static List<Contenir> contenirs = new ArrayList<>();

    /**
     * Instantiates a new Contenir.
     *
     * @param commande          the commande
     * @param stockMedicament   the stock medicament
     * @param pTotalAchete      the total achete
     * @param pPrixAchat        the prix achat
     * @throws SaisieException  the saisie exception
     */
    public Contenir(Commande commande, Stock_Medicament stockMedicament,
                    int pTotalAchete, int pPrixAchat) throws SaisieException {
        this.setCommande(commande);
        this.setStockMedicament(stockMedicament);
        this.setTotalAchete(pTotalAchete);
        this.setPrixAchat(pPrixAchat);
        contenirs.add(this);
    }

    // Getters & Setters

    /**
     * Gets commande.
     *
     * @return the commande
     */
    public Commande getCommande() {
        return commande;
    }

    /**
     * Sets commande.
     *
     * @param commande the commande
     * @throws SaisieException the saisie exception
     */
    public void setCommande(Commande commande) throws SaisieException {
        if (!positifInt(String.valueOf(commande))) {
            throw new SaisieException("La commande ne peut pas être nulle");
        }
        this.commande = commande;
    }

    /**
     * Gets stock medicament.
     *
     * @return the stock medicament
     */
    public Stock_Medicament getStockMedicament() {
        return stockMedicament;
    }

    /**
     * Sets stock medicament.
     *
     * @param stockMedicament the stock medicament
     * @throws SaisieException the saisie exception
     */
    public void setStockMedicament(Stock_Medicament stockMedicament) throws SaisieException {
        if (stockMedicament == null) {
            throw new SaisieException("Le stock de médicament ne peut pas être nul");
        }
        this.stockMedicament = stockMedicament;
    }

    /**
     * Gets total achete.
     *
     * @return the total achete
     */
    public int getTotalAchete() {
        return totalAchete;
    }

    /**
     * Sets total achete.
     *
     * @param pTotalAchete the total achete
     * @throws SaisieException the saisie exception
     */
    public void setTotalAchete(int pTotalAchete) throws SaisieException {
        if (!positifInt(String.valueOf(pTotalAchete)) || pTotalAchete <= 0) {
            throw new SaisieException("Erreur sur le total acheté (doit être positif) : " + pTotalAchete);
        }
        this.totalAchete = pTotalAchete;
    }

    /**
     * Gets prix achat.
     *
     * @return the prix achat
     */
    public int getPrixAchat() {
        return prixAchat;
    }

    /**
     * Sets prix achat.
     *
     * @param pPrixAchat the prix achat
     * @throws SaisieException the saisie exception
     */
    public void setPrixAchat(int pPrixAchat) throws SaisieException {
        if (!positifInt(String.valueOf(pPrixAchat)) || pPrixAchat < 0) {
            throw new SaisieException("Erreur sur le prix d'achat (ne peut pas être négatif) : " + pPrixAchat);
        }
        this.prixAchat = pPrixAchat;
    }

    /**
     * Gets contenirs.
     *
     * @return the contenirs
     */
    public static List<Contenir> getContenirs() {
        return new ArrayList<>(contenirs);
    }

    /**
     * Calcule le montant total pour cette ligne de commande
     *
     * @return le montant total (quantité * prix unitaire)
     */
    public double getMontantTotal() {
        return totalAchete * prixAchat;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Contenir :\n");
        sb.append("- Commande ID : ").append(commande != null ? commande.getId_Commande() : "N/A").append("\n");
        sb.append("- Médicament : ").append(stockMedicament != null ? stockMedicament.getMedicNom() : "N/A").append("\n");
        sb.append("- Total acheté : ").append(totalAchete).append("\n");
        sb.append("- Prix d'achat unitaire : ").append(prixAchat).append("€\n");
        sb.append("- Montant total : ").append(String.format("%.2f", getMontantTotal())).append("€\n");
        return sb.toString();
    }
}
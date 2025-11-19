package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.*;

/**
 * The type Commande.
 */
public class Commande {
    public enum TypeAchat { DIRECT, ORDONNANCE }

    // Attributs
    private int Id_Commande, comQuantite;
    private Date comDateCommande;
    private TypeAchat typeAchat;
    private String comNomMedecin, comNomPatient;
    private List<Stock_Medicament> medicaments;
    private double comPrix;
    private Mutuelle mutuelle;
    private boolean mutTauxPriseEnCharge;

    private static List<Commande> commandes = new ArrayList<>();

    /**
     * Instantiates a new Commande.
     *
     * @param pComDateCommande      the date commande
     * @param pTypeAchat            the type achat
     * @param pComNomMedecin        the nom medecin
     * @param pComNomPatient        the nom patient
     * @param medicaments           the medicaments
     * @param pComQuantite          the pComQuantite
     * @param pComPrix              the prix
     * @param mutuelle              the mutuelle
     * @param pMutTauxPriseEnCharge the prise en charge mutuelle
     * @throws SaisieException the saisie exception
     */
// Constructeur avec liste de médicaments et mutuelle
    public Commande(Date pComDateCommande,
                    TypeAchat pTypeAchat,
                    String pComNomMedecin,
                    String pComNomPatient,
                    List<Stock_Medicament> medicaments,
                    int pComQuantite,
                    double pComPrix,
                    Mutuelle mutuelle,
                    boolean pMutTauxPriseEnCharge) throws SaisieException {
        this.setDateCommande(pComDateCommande);
        this.setTypeAchat(pTypeAchat);
        this.setComNomMedecin(pComNomMedecin);
        this.setComNomPatient(pComNomPatient);
        this.setMedicaments(medicaments);
        this.setComQuantite(pComQuantite);
        this.setComPrix(pComPrix);
        this.mutuelle = mutuelle;
        this.mutTauxPriseEnCharge = pMutTauxPriseEnCharge;

        // Ajout automatique dans la liste
        commandes.add(this);
    }

    /**
     * Instantiates a new Commande.
     *
     * @param pComDateCommande      the p date commande
     * @param pTypeAchat            the p type achat
     * @param pComNomMedecin        the nom medecin
     * @param pComNomPatient        the nom patient
     * @param medicament            the medicament
     * @param pComQuantite          the pComQuantite
     * @param pComPrix              the pComPrix
     * @param mutuelle              the mutuelle
     * @param pMutTauxPriseEnCharge the p mut taux prise en charge
     * @throws SaisieException the saisie exception
     */
// Constructeur avec un seul médicament et mutuelle
    public Commande(Date pComDateCommande,
                    TypeAchat pTypeAchat,
                    String pComNomMedecin,
                    String pComNomPatient,
                    Stock_Medicament medicament,
                    int pComQuantite,
                    double pComPrix,
                    Mutuelle mutuelle,
                    boolean pMutTauxPriseEnCharge) throws SaisieException {
        this.setDateCommande(pComDateCommande);
        this.setTypeAchat(pTypeAchat);
        this.setComNomMedecin(pComNomMedecin);
        this.setComNomPatient(pComNomPatient);
        this.medicaments = new ArrayList<>();
        if (medicament != null) this.medicaments.add(medicament);
        this.setComQuantite(pComQuantite);
        this.setComPrix(pComPrix);
        this.mutuelle = mutuelle;
        this.mutTauxPriseEnCharge = pMutTauxPriseEnCharge;

        commandes.add(this);
    }

    /**
     * Instantiates a new Commande.
     *
     * @param pComDateCommande the p date commande
     * @param pTypeAchat       the p type achat
     * @param pComNomMedecin   the nom medecin
     * @param pComNomPatient   the nom patient
     * @param medicaments      the medicaments
     * @param pComQuantite     the pComQuantite
     * @param pComPrix         the pComPrix
     * @throws SaisieException the saisie exception
     */
// Constructeurs sans mutuelle
    public Commande(Date pComDateCommande,
                    TypeAchat pTypeAchat,
                    String pComNomMedecin,
                    String pComNomPatient,
                    List<Stock_Medicament> medicaments,
                    int pComQuantite,
                    double pComPrix) throws SaisieException {
        this(pComDateCommande, pTypeAchat, pComNomMedecin, pComNomPatient, medicaments, pComQuantite, pComPrix, null, false);
    }

    /**
     * Instantiates a new Commande.
     *
     * @param pComDateCommande the p date commande
     * @param pTypeAchat       the p type achat
     * @param pComNomMedecin   the nom medecin
     * @param pComNomPatient   the nom patient
     * @param medicament       the medicament
     * @param pComQuantite     the pComQuantite
     * @param pComPrix         the prix
     * @throws SaisieException the saisie exception
     */
    public Commande(Date pComDateCommande,
                    TypeAchat pTypeAchat,
                    String pComNomMedecin,
                    String pComNomPatient,
                    Stock_Medicament medicament,
                    int pComQuantite,
                    double pComPrix) throws SaisieException {
        this(pComDateCommande, pTypeAchat, pComNomMedecin, pComNomPatient, medicament, pComQuantite, pComPrix, null, false);
    }

    /**
     * Instantiates a new Commande.
     *
     * @param pComDateCommande      the p date commande
     * @param pTypeAchat            the p type achat
     * @param pComNomMedecin        the nom medecin
     * @param pComNomPatient        the nom patient
     * @param medicaments           the medicaments
     * @param pComQuantite          the pComQuantite
     * @param pComPrix              the prix
     * @param pMutTauxPriseEnCharge the p mut taux prise en charge
     * @throws SaisieException the saisie exception
     */
// Constructeurs compatibilité avec boolean seulement
    public Commande(Date pComDateCommande,
                    TypeAchat pTypeAchat,
                    String pComNomMedecin,
                    String pComNomPatient,
                    List<Stock_Medicament> medicaments,
                    int pComQuantite,
                    double pComPrix,
                    boolean pMutTauxPriseEnCharge) throws SaisieException {
        this(pComDateCommande, pTypeAchat, pComNomMedecin, pComNomPatient, medicaments, pComQuantite, pComPrix, null, pMutTauxPriseEnCharge);
    }

    // Getters & Setters

    /**
     * Gets id commande.
     *
     * @return the id commande
     */
    public int getId_Commande() {
        return Id_Commande;

    }

    /**
     * Gets commandes.
     *
     * @return the commandes
     */
    // Getters et setters
    public static List<Commande> getCommandes() { return commandes; }

    /**
     * Gets date commande.
     *
     * @return the date commande
     */
    public Date getDateCommande() { return this.comDateCommande; }

    /**
     * Gets date commande creation.
     *
     * @return the date commande creation
     */
    public String getDateCommandeCreation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return comDateCommande.toLocalDate().format(formatter);
    }

    /**
     * Sets date commande.
     *
     * @param pComDateCommande the p date commande
     * @throws SaisieException the saisie exception
     */
    public void setDateCommande(Date pComDateCommande) throws SaisieException {
        String dateStr = pComDateCommande.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (!dateValide(dateStr)) {
            throw new SaisieException("Erreur sur le format de la date : " + dateStr);
        } else {
            this.comDateCommande = pComDateCommande;
        }
    }

    /**
     * Gets type achat.
     *
     * @return the type achat
     */
    public TypeAchat getTypeAchat() { return this.typeAchat; }

    /**
     * Sets type achat.
     *
     * @param pTypeAchat the p type achat
     */
    public void setTypeAchat(TypeAchat pTypeAchat) { this.typeAchat = pTypeAchat; }

    /**
     * Gets nom medecin.
     *
     * @return the nom medecin
     */
    public String getNomMedecin() { return comNomMedecin; }

    /**
     * Sets com nom medecin.
     *
     * @param pComNomMedecin the p com nom medecin
     * @throws SaisieException the saisie exception
     */
    public void setComNomMedecin(String pComNomMedecin) throws SaisieException {
        if (!regexAlpha(pComNomMedecin)) {
            throw new SaisieException("Erreur sur le nom du medecin " + pComNomMedecin);
        } else {
            this.comNomMedecin = pComNomMedecin;
        }
    }


    /**
     * Gets nom patient.
     *
     * @return the nom patient
     */
    public String getNomPatient() { return comNomPatient; }

    /**
     * Sets com nom patient.
     *
     * @param pComNomPatient the com nom patient
     * @throws SaisieException the saisie exception
     */
    public void setComNomPatient(String pComNomPatient) throws SaisieException {
        if(!regexAlpha(pComNomPatient)){
            throw new SaisieException("Erreur sur nom du patient " + pComNomPatient);
        }else {
            this.comNomPatient = pComNomPatient;
        }
    }

    /**
     * Gets medicaments.
     *
     * @return the medicaments
     */
    public List<Stock_Medicament> getMedicaments() { return new ArrayList<>(this.medicaments); }

    /**
     * Sets medicaments.
     *
     * @param medicaments the medicaments
     */
    public void setMedicaments(List<Stock_Medicament> medicaments) {
        this.medicaments = new ArrayList<>();
        if (medicaments != null && !medicaments.isEmpty()) this.medicaments.addAll(medicaments);
    }

    /**
     * Ajouter medicament.
     *
     * @param medicament the medicament
     */
    public void ajouterMedicament(Stock_Medicament medicament) {
        if (medicament != null) this.medicaments.add(medicament);
    }

    /**
     * Gets quantite.
     *
     * @return the quantite
     */
    public int getQuantite() { return comQuantite; }

    /**
     * Sets com quantite.
     *
     * @param pComQuantite the p com quantite
     * @throws SaisieException the saisie exception
     */
    public void setComQuantite(int pComQuantite) throws SaisieException {
        if(!positifInt(String.valueOf(pComQuantite))){
            throw new SaisieException("Erreur sur le quantite " + pComQuantite);
        }else{
            this.comQuantite = pComQuantite;
        }
    }

    /**
     * Gets prix.
     *
     * @return the prix
     */
    public double getPrix() { return comPrix; }

    public void setComPrix(double pComPrix) throws SaisieException {
        if(pComPrix < 0){
            throw new SaisieException("Erreur sur le prix " + pComPrix);
        }else{
            this.comPrix = pComPrix;
        }
    }

    /**
     * Gets mutuelle.
     *
     * @return the mutuelle
     */
    public Mutuelle getMutuelle() { return mutuelle; }

    /**
     * Sets mutuelle.
     *
     * @param mutuelle the mutuelle
     */
    public void setMutuelle(Mutuelle mutuelle) { this.mutuelle = mutuelle; }

    /**
     * Gets taux mutuelle.
     *
     * @return the taux mutuelle
     */
    public double getComTauxPriseEnCharge() {
        if (mutuelle != null) return mutuelle.getTauxPriseEnCharge() / 100.0;
        return 0.0;
    }

    /**
     * Gets prix total.
     *
     * @return the prix total
     */
    public double getPrixTotal() {
        double total = 0.0;
        for (Stock_Medicament med : medicaments) total += med.getMedicPrixUnitaire() * med.getQuantite();
        return total;
    }

    /**
     * Gets prix total apres deduction.
     *
     * @return the prix total apres deduction
     */
    public double getPrixTotalApresDeduction() {
        double prixTotal = getPrixTotal();
        if (typeAchat == TypeAchat.ORDONNANCE && mutTauxPriseEnCharge && mutuelle != null)
            return prixTotal * (1 - getComTauxPriseEnCharge());
        return prixTotal;
    }

    /**
     * Gets montant deduction mutuelle.
     *
     * @return the montant deduction mutuelle
     */
    public double getMontantDeductionMutuelle() {
        if (typeAchat == TypeAchat.ORDONNANCE && mutTauxPriseEnCharge && mutuelle != null)
            return getPrixTotal() * getComTauxPriseEnCharge();
        return 0.0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Commande").append('\n');
        sb.append("- Date commande : ").append(getDateCommandeCreation()).append('\n');
        sb.append("- Type achat : ").append(typeAchat).append('\n');
        sb.append("- Nom medecin : ").append(comNomMedecin).append('\n');
        sb.append("- Nom patient : ").append(comNomPatient).append('\n');
        sb.append("- Médicaments : ").append('\n');
        if (medicaments.isEmpty()) sb.append("  Aucun médicament dans la commande").append('\n');
        else {
            for (int i = 0; i < medicaments.size(); i++) {
                sb.append("  ").append(i + 1).append(". ");
                sb.append("Nom: ").append(medicaments.get(i).getMedicNom()).append('\n');
                sb.append(", Quantité: ").append(medicaments.get(i).getQuantite()).append('\n');
                sb.append(", Prix unitaire: ").append(medicaments.get(i).getMedicPrixUnitaire()).append("€");
                sb.append('\n');
            }
        }
        sb.append("- Quantite totale : ").append(comQuantite).append('\n');
        sb.append("- Prix : ").append(comPrix).append("€").append('\n');
        sb.append("- Prix total calculé : ").append(String.format("%.2f", getPrixTotal())).append("€").append('\n');

        if (typeAchat == TypeAchat.ORDONNANCE) {
            sb.append("- Prise en charge mutuelle : ").append(mutTauxPriseEnCharge ? "Oui" : "Non").append('\n');
            if (mutTauxPriseEnCharge && mutuelle != null) {
                sb.append("- Mutuelle : ").append(mutuelle.getNom()).append('\n');
                sb.append("- Taux de prise en charge : ").append(String.format("%.1f", mutuelle.getTauxPriseEnCharge())).append("%").append('\n');
                sb.append("- Déduction mutuelle : ").append(String.format("%.2f", getMontantDeductionMutuelle())).append("€").append('\n');
                sb.append("- Prix à payer : ").append(String.format("%.2f", getPrixTotalApresDeduction())).append("€").append('\n');
            }
        }
        return sb.toString();
    }
}

package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.dateValide;
import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

/**
 * The type Ordonnance.
 */
public class Ordonnance {
    // Attributs pour la classe Ordonnance
    private int Id_Ordonnance;
    private LocalDateTime dateOrdonnance;
    private String date, nomMedecin, nomPatient;

    // Liste des médicaments pour cette ordonnance
    private List<Stock_Medicament> medicaments;

    // Liste statique de toutes les ordonnances
    private static List<Ordonnance> ordonnances = new ArrayList<>();

    /**
     * Constructeur pour la classe Ordonnance
     *
     * @param pOrdoDate        the pOrdoDate
     * @param pNomMedecin le nom du médecin
     * @param pNomPatient le nom du patient
     * @param medicaments la liste des médicaments
     * @throws SaisieException exception de saisie
     */
    public Ordonnance(String pOrdoDate, String pNomMedecin, String pNomPatient, List<Stock_Medicament> medicaments) throws SaisieException {
        this.setDate(pOrdoDate);
        //this.dateOrdonnance = LocalDateTime.now();  // *** mise en commentaire pour tester les differentes dates de initialisation ***
        this.setNomMedecin(pNomMedecin);
        this.setNomPatient(pNomPatient);

        // Initialiser la liste des médicaments
        this.medicaments = new ArrayList<>();
        if (medicaments != null && !medicaments.isEmpty()) {
            this.medicaments.addAll(medicaments);
        }

        // Ajouter cette ordonnance à la liste statique
        Ordonnance.ordonnances.add(this);
    }

    // Méthodes pour gérer les médicaments

    /**
     *
     * @return une copie de la liste des médicaments
     */
    public List<Stock_Medicament> getMedicaments() {
        return new ArrayList<>(this.medicaments);
    }

    // Méthodes statiques

    public static List<Ordonnance> getOrdonnances() {
        return new ArrayList<>(ordonnances);
    }

    // Getters et Setters


    /**
     * Gets id ordonnance.
     *
     * @return the id ordonnance
     */
    public int getId_Ordonnance() {
        return Id_Ordonnance;
    }


    /**
     * Gets date ordonnance.
     *
     * @return the date ordonnance
     */
    public LocalDateTime getDateOrdonnance() {
        return this.dateOrdonnance;
    }

    /**
     * Gets date ordonnance creation formatée.
     *
     * @return the date ordonnance creation formatter
     */
    public String getDateOrdonnanceCreation() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateOrdonnance.format(formatter);
    }

    /**
     * Sets date.
     *
     * @param date the date
     * @throws SaisieException the saisie exception
     */
    public void setDate(String date) throws SaisieException {
        if (!dateValide(date)) {
            throw new SaisieException("Le date n'est pas valide.");
        }else{
            this.date = date;
        }
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public String getDate() {
        return this.date;
    }

    /**
     * Gets nom medecin.
     *
     * @return the nom medecin
     */
    public String getNomMedecin() {
        return this.nomMedecin;
    }

    /**
     * Sets nom medecin.
     *
     * @param pNomMedecin       the nom medecin
     * @throws SaisieException  the saisie exception
     */
    public void setNomMedecin(String pNomMedecin) throws SaisieException {
        if (!regexAlpha(pNomMedecin) && !pNomMedecin.isEmpty()) {
            throw new SaisieException("Erreur sur le nom du médecin : " + pNomMedecin);
        } else {
            this.nomMedecin = pNomMedecin;
        }
    }

    /**
     * Gets nom patient.
     *
     * @return the nom patient
     */
    public String getNomPatient() {
        return this.nomPatient;
    }

    /**
     * Sets nom patient.
     *
     * @param pNomPatient       the nom patient
     * @throws SaisieException  the saisie exception
     */
    public void setNomPatient(String pNomPatient) throws SaisieException {
        if (!regexAlpha(pNomPatient) && !pNomPatient.isEmpty()) {
            throw new SaisieException("Erreur sur le nom du patient : " + pNomPatient);
        } else {
            this.nomPatient = pNomPatient;
        }
    }

    @Override
    public String toString() {
        StringBuilder sbo = new StringBuilder();
        sbo.append("\nOrdonnance :\n");
        sbo.append("- Date ordonnance : ").append(date).append("\n");
        sbo.append("- Nom médecin : ").append(nomMedecin).append("\n");
        sbo.append("- Nom patient : ").append(nomPatient).append("\n");
        sbo.append("Liste des médicaments :\n");

        if (medicaments.isEmpty()) {
            sbo.append("  Aucun médicament prescrit\n");
        } else {
            for (int i = 0; i < medicaments.size(); i++) {
                sbo.append("  ").append(i + 1).append(". ").append(medicaments.get(i)).append("\n");
            }
        }

        return sbo.toString();
    }
}
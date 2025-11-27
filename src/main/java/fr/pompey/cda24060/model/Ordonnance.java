package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static fr.pompey.cda24060.utility.RegexUtility.regexAlpha;

public class Ordonnance {
    private int Id_Ordonnance;
    private Date date;
    private String nomMedecin, nomPatient;
    private Medecin medecin;
    private Patient patient;
    private List<Stock_Medicament> medicaments;

    // Formatter pour les dates au format dd/MM/yyyy
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Ordonnance(Date date, String pNomMedecin, String pNomPatient,
                      Medecin medecin, Patient patient) throws SaisieException {
        this.date = date;
        this.setNomMedecin(pNomMedecin);
        this.setNomPatient(pNomPatient);
        this.medecin = medecin;
        this.patient = patient;
        this.medicaments = new ArrayList<>();
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
     * Sets id ordonnance.
     *
     * @param id_Ordonnance the id ordonnance
     */
    public void setId_Ordonnance(int id_Ordonnance) {
        Id_Ordonnance = id_Ordonnance;
    }

    /**
     * Gets date.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Retourne la date formatée au format dd/MM/yyyy
     *
     * @return the date formatee
     */
    public String getDateFormatee() {
        if (this.date != null) {
            return this.date.toLocalDate().format(DATE_FORMATTER);
        }
        return "";
    }

    /**
     * Sets date.
     *
     * @param date the date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Gets nom medecin.
     *
     * @return the nom medecin
     */
    public String getNomMedecin() {
        return nomMedecin;
    }

    /**
     * Sets nom medecin.
     *
     * @param pNomMedecin the nom medecin
     */
    public void setNomMedecin(String pNomMedecin) throws SaisieException {
        if(!regexAlpha(pNomMedecin)) {
            throw new SaisieException("Erreur sur le nom du medecin : ");
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
        return nomPatient;
    }

    /**
     * Sets nom patient.
     *
     * @param pNomPatient the nom patient
     */
    public void setNomPatient(String pNomPatient) throws SaisieException {
        if(!regexAlpha(pNomPatient)) {
            throw new SaisieException("Erreur sur le nom du patient : ");
        } else {
            this.nomPatient = pNomPatient;
        }
    }

    /**
     * Gets medecin.
     *
     * @return the medecin
     */
    public Medecin getMedecin() {
        return medecin;
    }

    /**
     * Sets medecin.
     *
     * @param medecin the medecin
     */
    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    /**
     * Gets patient.
     *
     * @return the patient
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * Sets patient.
     *
     * @param patient the patient
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    /**
     * Gets medicaments.
     *
     * @return the medicaments
     */
    public List<Stock_Medicament> getMedicaments() {
        return medicaments;
    }

    /**
     * Sets medicaments.
     *
     * @param medicaments the medicaments
     */
    public void setMedicaments(List<Stock_Medicament> medicaments) {
        this.medicaments = medicaments;
    }

    /**
     * Ajouter medicament.
     *
     * @param medicament the medicament
     */
    public void ajouterMedicament(Stock_Medicament medicament) {
        this.medicaments.add(medicament);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Ordonnance n°").append(Id_Ordonnance).append("\n");
        sb.append("Date : ").append(getDateFormatee()).append("\n");
        sb.append("Médecin : ").append(nomMedecin).append("\n");
        sb.append("Patient : ").append(nomPatient).append("\n");
        sb.append("Médicaments prescrits :\n");

        if (medicaments.isEmpty()) {
            sb.append("  - Aucun médicament\n");
        } else {
            for (Stock_Medicament m : medicaments) {
                sb.append("  - ").append(m.getMedicNom())
                        .append(" (").append(m.getQuantite()).append(")\n");
            }
        }

        return sb.toString();
    }
}
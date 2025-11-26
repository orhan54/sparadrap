package fr.pompey.cda24060.model;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Ordonnance {
    private int Id_Ordonnance;
    private Date date;
    private String nomMedecin;
    private String nomPatient;
    private Medecin medecin;
    private Patient patient;
    private List<Stock_Medicament> medicaments;

    // Formatter pour les dates au format dd/MM/yyyy
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Ordonnance(Date date, String nomMedecin, String nomPatient,
                      Medecin medecin, Patient patient) {
        this.date = date;
        this.nomMedecin = nomMedecin;
        this.nomPatient = nomPatient;
        this.medecin = medecin;
        this.patient = patient;
        this.medicaments = new ArrayList<>();
    }

    // Getters et Setters
    public int getId_Ordonnance() {
        return Id_Ordonnance;
    }

    public void setId_Ordonnance(int id_Ordonnance) {
        Id_Ordonnance = id_Ordonnance;
    }

    public Date getDate() {
        return date;
    }

    /**
     * Retourne la date formatée au format dd/MM/yyyy
     */
    public String getDateFormatee() {
        if (this.date != null) {
            return this.date.toLocalDate().format(DATE_FORMATTER);
        }
        return "";
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getNomMedecin() {
        return nomMedecin;
    }

    public void setNomMedecin(String nomMedecin) {
        this.nomMedecin = nomMedecin;
    }

    public String getNomPatient() {
        return nomPatient;
    }

    public void setNomPatient(String nomPatient) {
        this.nomPatient = nomPatient;
    }

    public Medecin getMedecin() {
        return medecin;
    }

    public void setMedecin(Medecin medecin) {
        this.medecin = medecin;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public List<Stock_Medicament> getMedicaments() {
        return medicaments;
    }

    public void setMedicaments(List<Stock_Medicament> medicaments) {
        this.medicaments = medicaments;
    }

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
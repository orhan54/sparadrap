package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Ordonnance;
import fr.pompey.cda24060.model.Medecin;
import fr.pompey.cda24060.model.Patient;
import fr.pompey.cda24060.model.Stock_Medicament;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceDAO extends InterfaceDAO<Ordonnance> {

    @Override
    public Ordonnance create(Ordonnance ordonnance) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Ordonnance (ordo_date, ordo_nom_medecin, ordo_nom_patient, Id_Medecin, Id_Patient) VALUES (?, ?, ?, ?, ?)");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDate(1, ordonnance.getDate());
            stmt.setString(2, ordonnance.getNomMedecin());
            stmt.setString(3, ordonnance.getNomPatient());
            stmt.setInt(4, ordonnance.getMedecin().getId_Medecin());
            stmt.setInt(5, ordonnance.getPatient().getId_Patient());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création de l'ordonnance, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ordonnance.setId_Ordonnance(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création de l'ordonnance, aucun ID généré.");
                }
            }

            return ordonnance;
        }
    }

    @Override
    public Ordonnance getById(int id) throws SQLException, SaisieException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.*, m.med_nom, m.med_prenom, p.pat_nom, p.pat_prenom ");
        sql.append("FROM Ordonnance o ");
        sql.append("LEFT JOIN Medecin m ON o.Id_Medecin = m.Id_Medecin ");
        sql.append("LEFT JOIN Patient p ON o.Id_Patient = p.Id_Patient ");
        sql.append("WHERE o.id_Ordonnance = ?");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql))) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrdonnanceFromResultSet(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Ordonnance> getAll() throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.*, m.med_nom, m.med_prenom, p.pat_nom, p.pat_prenom ");
        sql.append("FROM Ordonnance o ");
        sql.append("LEFT JOIN Medecin m ON o.Id_Medecin = m.Id_Medecin ");
        sql.append("LEFT JOIN Patient p ON o.Id_Patient = p.Id_Patient");
        sql.toString();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(String.valueOf(sql))) {

            while (rs.next()) {
                ordonnances.add(extractOrdonnanceFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des ordonnances : " + e.getMessage());
        }
        return ordonnances;
    }

    /**
     * Récupère les ordonnances par nom de médecin
     */
    public List<Ordonnance> getByNomMedecin(String nomMedecin) throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.*, m.med_nom, m.med_prenom, p.pat_nom, p.pat_prenom ");
        sql.append("FROM Ordonnance o ");
        sql.append("LEFT JOIN Medecin m ON o.Id_Medecin = m.Id_Medecin ");
        sql.append("LEFT JOIN Patient p ON o.Id_Patient = p.Id_Patient ");
        sql.append("WHERE o.ordo_nom_medecin = ?");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql))) {
            stmt.setString(1, nomMedecin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(extractOrdonnanceFromResultSet(rs));
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des ordonnances : " + e.getMessage());
        }
        return ordonnances;
    }

    @Override
    public boolean update(Ordonnance ordonnance) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Ordonnance SET ordo_date = ?, ordo_nom_medecin = ?, ordo_nom_patient = ?, ");
        sql.append("Id_Medecin = ?, Id_Patient = ? WHERE Id_Ordonnance = ?");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql))) {
            stmt.setDate(1, ordonnance.getDate());
            stmt.setString(2, ordonnance.getNomMedecin());
            stmt.setString(3, ordonnance.getNomPatient());
            stmt.setInt(4, ordonnance.getMedecin().getId_Medecin());
            stmt.setInt(5, ordonnance.getPatient().getId_Patient());
            stmt.setInt(6, ordonnance.getId_Ordonnance());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM Ordonnance WHERE Id_Ordonnance = ?");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql))) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Récupère les médicaments d'une ordonnance
     */
    public List<Stock_Medicament> getMedicamentsByOrdonnance(int idOrdonnance) throws SQLException {
        List<Stock_Medicament> medicaments = new ArrayList<>();

        // Si vous avez une table de liaison ordonnance_medicament
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT sm.* FROM Stock_Medicament AS sm ");
        sql.append("INNER JOIN ordonnance_medicament AS om ON sm.Id_Stock_Medicament = om.Id_Stock_Medicament ");
        sql.append("WHERE om.Id_Ordonnance = ?");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql))) {
            stmt.setInt(1, idOrdonnance);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Créer un médicament basique
                    Stock_Medicament medicament = new Stock_Medicament(
                            rs.getString("medic_nom"),
                            rs.getInt("medic_quantite"),
                            rs.getDate("medic_date_mise_en_service"),
                            rs.getDate("medic_date_entree_stock"),
                            rs.getDouble("medic_prix_unitaire")
                    );
                    medicament.setId_Stock_Medicament(rs.getInt("Id_Stock_Medicament"));
                    medicaments.add(medicament);
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des médicaments : " + e.getMessage());
        }

        return medicaments;
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }

    /**
     * Extrait une ordonnance depuis un ResultSet
     */
    private Ordonnance extractOrdonnanceFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        // Créer un médecin basique (vous pouvez enrichir avec LieuDAO si nécessaire)
        Medecin medecin = new Medecin();
        medecin.setId_Medecin(rs.getInt("Id_Medecin"));
        medecin.setNom(rs.getString("med_nom"));
        medecin.setPrenom(rs.getString("med_prenom"));

        // Créer un patient basique
        Patient patient = new Patient();
        patient.setId_Patient(rs.getInt("Id_Patient"));
        patient.setNom(rs.getString("pat_nom"));
        patient.setPrenom(rs.getString("pat_prenom"));

        // Créer l'ordonnance avec java.sql.Date
        Ordonnance ordonnance = new Ordonnance(
                rs.getDate("ordo_date"), // java.sql.Date directement du ResultSet
                rs.getString("ordo_nom_medecin"),
                rs.getString("ordo_nom_patient"),
                medecin,
                patient
        );
        ordonnance.setId_Ordonnance(rs.getInt("Id_Ordonnance"));

        return ordonnance;
    }
}
package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Ordonnance;
import fr.pompey.cda24060.model.Stock_Medicament;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des ordonnances
 */
public class OrdonnanceDAO extends InterfaceDAO<Ordonnance> {

    @Override
    public Ordonnance create(Ordonnance ordonnance) throws SQLException {
        String sql = "INSERT INTO Ordonnance (ordo_date, ordo_nom_medecin, ordo_nom_patient, Id_Medecin, Id_Patient) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Convertir la date String en Date SQL
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate localDate = java.time.LocalDate.parse(ordonnance.getDate(), formatter);

            stmt.setDate(1, Date.valueOf(localDate));
            stmt.setString(2, ordonnance.getNomMedecin());
            stmt.setString(3, ordonnance.getNomPatient());
            // Note: Id_Medecin et Id_Patient devront être passés séparément ou ajoutés à la classe Ordonnance
            stmt.setInt(4, 1); // Valeur temporaire - à adapter selon votre logique
            stmt.setInt(5, 1); // Valeur temporaire - à adapter selon votre logique

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
    public Ordonnance getById(int id) throws SQLException {
        String sql = "SELECT o.Id_Ordonnance, o.ordo_date, o.ordo_nom_medecin, o.ordo_nom_patient " +
                "FROM Ordonnance AS o " +
                "WHERE o.Id_Ordonnance = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractOrdonnanceFromResultSet(rs);
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération de l'ordonnance : " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Ordonnance> getAll() throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT o.Id_Ordonnance, o.ordo_date, o.ordo_nom_medecin, o.ordo_nom_patient " +
                "FROM Ordonnance AS o " +
                "ORDER BY o.ordo_date DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ordonnances.add(extractOrdonnanceFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des ordonnances : " + e.getMessage());
        }

        return ordonnances;
    }

    /**
     * Récupérer toutes les ordonnances d'un médecin spécifique par son nom
     * @param nomMedecin Le nom complet du médecin (format: "Nom Prénom")
     * @return Liste des ordonnances du médecin
     * @throws SQLException
     */
    public List<Ordonnance> getByNomMedecin(String nomMedecin) throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT o.Id_Ordonnance, o.ordo_date, o.ordo_nom_medecin, o.ordo_nom_patient " +
                "FROM Ordonnance AS o " +
                "WHERE o.ordo_nom_medecin = ? " +
                "ORDER BY o.ordo_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomMedecin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(extractOrdonnanceFromResultSet(rs));
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des ordonnances du médecin : " + e.getMessage());
        }

        return ordonnances;
    }

    /**
     * Récupérer toutes les ordonnances d'un patient spécifique par son nom
     * @param nomPatient Le nom complet du patient (format: "Nom Prénom")
     * @return Liste des ordonnances du patient
     * @throws SQLException
     */
    public List<Ordonnance> getByNomPatient(String nomPatient) throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT o.Id_Ordonnance, o.ordo_date, o.ordo_nom_medecin, o.ordo_nom_patient " +
                "FROM Ordonnance AS o " +
                "WHERE o.ordo_nom_patient = ? " +
                "ORDER BY o.ordo_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, nomPatient);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(extractOrdonnanceFromResultSet(rs));
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des ordonnances du patient : " + e.getMessage());
        }

        return ordonnances;
    }

    /**
     * Récupérer toutes les ordonnances d'un médecin par son ID
     * @param idMedecin L'identifiant du médecin
     * @return Liste des ordonnances du médecin
     * @throws SQLException
     */
    public List<Ordonnance> getByMedecinId(int idMedecin) throws SQLException {
        List<Ordonnance> ordonnances = new ArrayList<>();
        String sql = "SELECT o.Id_Ordonnance, o.ordo_date, o.ordo_nom_medecin, o.ordo_nom_patient " +
                "FROM Ordonnance AS o " +
                "WHERE o.Id_Medecin = ? " +
                "ORDER BY o.ordo_date DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idMedecin);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ordonnances.add(extractOrdonnanceFromResultSet(rs));
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des ordonnances du médecin : " + e.getMessage());
        }

        return ordonnances;
    }

    @Override
    public boolean update(Ordonnance ordonnance) throws SQLException {
        String sql = "UPDATE Ordonnance SET ordo_date = ?, ordo_nom_medecin = ?, ordo_nom_patient = ? WHERE Id_Ordonnance = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Convertir la date String en Date SQL
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate localDate = java.time.LocalDate.parse(ordonnance.getDate(), formatter);

            stmt.setDate(1, Date.valueOf(localDate));
            stmt.setString(2, ordonnance.getNomMedecin());
            stmt.setString(3, ordonnance.getNomPatient());
            stmt.setInt(4, ordonnance.getId_Ordonnance());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Ordonnance WHERE Id_Ordonnance = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Extraire un objet Ordonnance depuis un ResultSet
     */
    private Ordonnance extractOrdonnanceFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        // Convertir la date SQL en String au format dd/MM/yyyy
        Date sqlDate = rs.getDate("ordo_date");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String dateStr = sqlDate.toLocalDate().format(formatter);

        // Créer l'ordonnance avec une liste de médicaments vide
        // Les médicaments devront être chargés séparément si nécessaire
        Ordonnance ordonnance = new Ordonnance(
                dateStr,
                rs.getString("ordo_nom_medecin"),
                rs.getString("ordo_nom_patient"),
                new ArrayList<Stock_Medicament>()
        );

        ordonnance.setId_Ordonnance(rs.getInt("Id_Ordonnance"));

        return ordonnance;
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }
}
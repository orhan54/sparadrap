package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.dataBase.Singleton;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Medecin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des médecins
 */
public class MedecinDAO implements InterfaceDAO<Medecin> {

    private Connection connection;

    public MedecinDAO() throws SQLException, ClassNotFoundException, java.io.IOException {
        this.connection = Singleton.getInstanceDB();
    }

    /**
     * Créer un médecin dans la base
     */
    @Override
    public Medecin create(Medecin medecin) throws SQLException {
        String sql = "INSERT INTO Medecin (med_nom, med_prenom, med_numero_agreement, Id_Lieu) VALUES (?, ?, ?, ?)";

        try {
            // Créer d'abord le lieu
            LieuDAO lieuDAO = new LieuDAO();
            Lieu lieu = lieuDAO.create(medecin.getLieu());
            medecin.setLieu(lieu);

            // Ensuite créer le médecin
            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, medecin.getNom());
                stmt.setString(2, medecin.getPrenom());
                stmt.setString(3, medecin.getNumeroAgreement());
                stmt.setInt(4, lieu.getId_Lieu());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la création du médecin, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        medecin.setId_Medecin(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Échec de la création du médecin, aucun ID généré.");
                    }
                }

                return medecin;
            }
        } catch (ClassNotFoundException | java.io.IOException e) {
            throw new SQLException("Erreur lors de l'initialisation de LieuDAO : " + e.getMessage());
        }
    }

    /**
     * Récupérer un médecin par son ID
     */
    @Override
    public Medecin getById(int id) throws SQLException {
        String sql = "SELECT m.Id_Medecin, m.med_nom, m.med_prenom, m.med_numero_agreement, " +
                "l.Id_Lieu, l.lieu_adresse, l.lieu_email, l.lieu_telephone, l.lieu_ville, l.lieu_cp " +
                "FROM Medecin AS m " +
                "INNER JOIN Lieu AS l ON m.Id_Lieu = l.Id_Lieu " +
                "WHERE m.Id_Medecin = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractMedecinFromResultSet(rs);
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération du médecin : " + e.getMessage());
        }
        return null;
    }

    /**
     * Récupérer tous les médecins
     */
    @Override
    public List<Medecin> getAll() throws SQLException {
        List<Medecin> medecins = new ArrayList<>();
        String sql = "SELECT m.Id_Medecin, m.med_nom, m.med_prenom, m.med_numero_agreement, " +
                "l.Id_Lieu, l.lieu_adresse, l.lieu_email, l.lieu_telephone, l.lieu_ville, l.lieu_cp " +
                "FROM Medecin AS m " +
                "INNER JOIN Lieu AS l ON m.Id_Lieu = l.Id_Lieu " +
                "ORDER BY m.med_nom, m.med_prenom";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                medecins.add(extractMedecinFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des médecins : " + e.getMessage());
        }

        return medecins;
    }

    /**
     * Mettre à jour un médecin existant
     */
    @Override
    public boolean update(Medecin medecin) throws SQLException {
        String sql = "UPDATE Medecin SET med_nom = ?, med_prenom = ?, med_numero_agreement = ?, Id_Lieu = ? WHERE Id_Medecin = ?";

        try {
            // Mettre à jour d'abord le lieu
            LieuDAO lieuDAO = new LieuDAO();
            lieuDAO.update(medecin.getLieu());

            // Ensuite mettre à jour le médecin
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, medecin.getNom());
                stmt.setString(2, medecin.getPrenom());
                stmt.setString(3, medecin.getNumeroAgreement());
                stmt.setInt(4, medecin.getLieu().getId_Lieu());
                stmt.setInt(5, medecin.getId_Medecin());

                int affectedRows = stmt.executeUpdate();
                return affectedRows > 0;
            }
        } catch (ClassNotFoundException | java.io.IOException e) {
            throw new SQLException("Erreur lors de l'initialisation de LieuDAO : " + e.getMessage());
        }
    }

    /**
     * Supprimer un médecin
     */
    @Override
    public boolean delete(int id) throws SQLException {
        // Récupérer le médecin pour obtenir l'ID du lieu
        Medecin medecin = getById(id);
        if (medecin == null) {
            return false;
        }
        int idLieu = medecin.getLieu().getId_Lieu();

        // Supprimer le médecin
        String sqlMed = "DELETE FROM Medecin WHERE Id_Medecin = ?";
        try (PreparedStatement stmtMed = connection.prepareStatement(sqlMed)) {
            stmtMed.setInt(1, id);
            int affectedRows = stmtMed.executeUpdate();
            if (affectedRows == 0) return false;
        }

        // Supprimer le lieu
        String sqlLieu = "DELETE FROM Lieu WHERE Id_Lieu = ?";
        try (PreparedStatement stmtLieu = connection.prepareStatement(sqlLieu)) {
            stmtLieu.setInt(1, idLieu);
            stmtLieu.executeUpdate();
        }

        return true;
    }

    /**
     * Extraire un objet Medecin depuis un ResultSet
     */
    private Medecin extractMedecinFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        Lieu lieu = new Lieu(
                rs.getString("lieu_adresse"),
                rs.getString("lieu_email"),
                rs.getString("lieu_telephone"),
                rs.getString("lieu_ville"),
                rs.getInt("lieu_cp")
        );
        lieu.setId_Lieu(rs.getInt("Id_Lieu"));

        Medecin medecin = new Medecin(
                rs.getString("med_nom"),
                rs.getString("med_prenom"),
                rs.getString("med_numero_agreement"),
                lieu
        );
        medecin.setId_Medecin(rs.getInt("Id_Medecin"));
        return medecin;
    }
}

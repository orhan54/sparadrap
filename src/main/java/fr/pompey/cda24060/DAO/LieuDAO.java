package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.dataBase.Singleton;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des lieux en base de données
 */
public class LieuDAO implements InterfaceDAO<Lieu> {

    private Connection connection;

    public LieuDAO() throws SQLException, ClassNotFoundException, java.io.IOException {
        this.connection = Singleton.getInstanceDB();
    }

    /**
     * Créer un nouveau lieu dans la base de données
     */
    @Override
    public Lieu create(Lieu lieu) throws SQLException {
        String sql = "INSERT INTO Lieu (lieu_adresse, lieu_email, lieu_telephone, lieu_ville, lieu_cp) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, lieu.getAdresse());
            stmt.setString(2, lieu.getEmail());
            stmt.setString(3, lieu.getTelephone());
            stmt.setString(4, lieu.getVille());
            stmt.setInt(5, lieu.getCodePostal());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du lieu, aucune ligne affectée.");
            }

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    lieu.setId_Lieu(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création du lieu, aucun ID généré.");
                }
            }

            return lieu;
        }
    }

    /**
     * Récupérer un lieu par son ID
     */
    @Override
    public Lieu getById(int id) throws SQLException {
        String sql = "SELECT Id_Lieu, lieu_adresse, lieu_email, lieu_telephone, lieu_ville, lieu_cp FROM Lieu WHERE Id_Lieu = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractLieuFromResultSet(rs);
                }
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération du lieu : " + e.getMessage());
        }

        return null;
    }

    /**
     * Récupérer tous les lieux
     */
    @Override
    public List<Lieu> getAll() throws SQLException {
        List<Lieu> lieux = new ArrayList<>();
        String sql = "SELECT Id_Lieu, lieu_adresse, lieu_email, lieu_telephone, lieu_ville, lieu_cp FROM Lieu ORDER BY lieu_ville";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lieux.add(extractLieuFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des lieux : " + e.getMessage());
        }

        return lieux;
    }

    /**
     * Mettre à jour un lieu existant
     */
    @Override
    public boolean update(Lieu lieu) throws SQLException {
        String sql = "UPDATE Lieu SET lieu_adresse = ?, lieu_email = ?, lieu_telephone = ?, lieu_ville = ?, lieu_cp = ? WHERE Id_Lieu = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, lieu.getAdresse());
            stmt.setString(2, lieu.getEmail());
            stmt.setString(3, lieu.getTelephone());
            stmt.setString(4, lieu.getVille());
            stmt.setInt(5, lieu.getCodePostal());
            stmt.setInt(6, lieu.getId_Lieu());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Supprimer un lieu par son ID
     */
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM Lieu WHERE Id_Lieu = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Extraire un objet Lieu depuis un ResultSet
     */
    private Lieu extractLieuFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        Lieu lieu = new Lieu(
                rs.getString("lieu_adresse"),
                rs.getString("lieu_email"),
                rs.getString("lieu_telephone"),
                rs.getString("lieu_ville"),
                rs.getInt("lieu_cp")
        );
        lieu.setId_Lieu(rs.getInt("Id_Lieu"));
        return lieu;
    }
}
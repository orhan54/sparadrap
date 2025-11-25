package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Pharmacie;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PharmacieDAO extends InterfaceDAO<Pharmacie> {
    @Override
    public Pharmacie create(Pharmacie pharmacie) throws SQLException, IOException, ClassNotFoundException {
        String sql = "INSERT INTO Pharmaci (pha_nom, pha_prenom, Id_Lieu) VALUES (?, ?, ?)";

        // Créer d'abord le lieu
        LieuDAO lieuDAO = new LieuDAO();
        Lieu lieu = lieuDAO.create(pharmacie.getLieu());
        pharmacie.setLieu(lieu);

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, pharmacie.getNom());
            stmt.setString(2, pharmacie.getPrenom());
            stmt.setInt(3, lieu.getId_Lieu());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                throw  new SQLException("Échec de la création du pharmacien, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pharmacie.setId_Pharmacie(generatedKeys.getInt(1));
                }else{
                    throw new SQLException("Échec de la création du pharmacien, aucun ID généré.");
                }
            }
        }

        return pharmacie;
    }

    @Override
    public Pharmacie getById(int id) throws SQLException, SaisieException {
        String sql = "SELECT p.Id_Pharmacie, p.pha_nom, p.pha_prenom " +
                "l.Id_Lieu, l.lieu_adresse, l.lieu_email, l.lieu_telephone, l.lieu_ville, l.lieu_cp " +
                "FROM Pharmacie AS p " +
                "INNER JOIN Lieu AS l ON l.Id_Lieu = p.Id_lieu " +
                "WHERE p.Id_Pharmacie = ? ";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractPharmacieFromResultSet(rs);
                }
            }
        } catch (SaisieException e) {
            throw new SaisieException("Erreur lors de la récupération du pharmacien : " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Pharmacie> getAll() throws SQLException {
        List<Pharmacie> pharmacies = new ArrayList<>();
        String sql = "SELECT p.Id_Pharmacie, p.pha_nom, p.pha_prenom " +
                "l.Id_Lieu, l.lieu_adresse, l.lieu_email, l.lieu_telephone, l.lieu_ville, l.lieu_cp " +
                "FROM Pharmacie AS p " +
                "INNER JOIN Lieu AS l ON l.Id_Lieu = p.Id_lieu " +
                "Order BY p.pha_nom, p.pha_prenom";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pharmacies.add((Pharmacie) stmt.executeQuery(sql));
            }
        }

        return pharmacies;
    }

    @Override
    public boolean update(Pharmacie pharmacie) throws SQLException {
        String sql = "UPDATE Pharmacie SET pha_nom = ?, pha_prenom = ? , Id_Lieu = ? WHERE Id_Pharmacie = ?";

        // Mettre à jour d'abord le lieu
        LieuDAO lieuDAO = new LieuDAO();
        lieuDAO.update(pharmacie.getLieu());

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, pharmacie.getNom());
            stmt.setString(2, pharmacie.getPrenom());
            stmt.setInt(3, pharmacie.getLieu().getId_Lieu());
            stmt.setInt(4, pharmacie.getId_Pharmacie());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException, SaisieException {
        // Récupérer le pharmacien pour obtenir l'ID du lieu
        Pharmacie pharmacie = getById(id);
        if(pharmacie == null){
            return false;
        }
        int idLieu = pharmacie.getLieu().getId_Lieu();

        // Supprimer le pharmacien
        String sqlPha = "DELETE FROM Pharmacie WHERE Id_Pharmacie = ? ";
        try (PreparedStatement stmtPha = connection.prepareStatement(sqlPha)) {
            stmtPha.setInt(1, id);
            int affectedRows = stmtPha.executeUpdate();
            if(affectedRows == 0) return false;
        }

        // Supprimer le lieu
        String sqlLieu = "DELETE FROM Lieu WHERE Id_Lieu = ? ";
        try (PreparedStatement stmtLieu = connection.prepareStatement(sqlLieu)) {
            stmtLieu.setInt(1, id);
            stmtLieu.executeUpdate();
        }

        return true;
    }

    private Pharmacie extractPharmacieFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        Lieu lieu = new Lieu(
                rs.getString("lieu_adresse"),
                rs.getString("lieu_email"),
                rs.getString("lieu_telephone"),
                rs.getString("lieu_ville"),
                rs.getInt("lieu_cp")
        );
        lieu.setId_Lieu(rs.getInt("Id_Lieu"));

        Pharmacie pharmacie = new Pharmacie(
                rs.getString("pha_nom"),
                rs.getString("pha_prenom"),
                lieu
        );
        pharmacie.setId_Pharmacie(rs.getInt("Id_Pharmacie"));
        return pharmacie;
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }

}

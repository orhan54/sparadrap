package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Mutuelle;
import fr.pompey.cda24060.model.Lieu;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MutuelleDAO extends InterfaceDAO<Mutuelle> {

    @Override
    public Mutuelle create(Mutuelle mutuelle) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Mutuelle (mut_nom, mut_taux_prise_en_charge, mut_num_departement, Id_Lieu) VALUES (?, ?, ?, ?)");
        sql.toString();

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql), Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mutuelle.getMutNom());
            ps.setDouble(2, mutuelle.getMutTauxPriseEnCharge());
            ps.setInt(3, mutuelle.getMutNumDepartement());
            ps.setInt(4, mutuelle.getLieu().getId_Lieu());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        mutuelle.setId_Mutuelle(generatedKeys.getInt(1));
                    }
                }
            }
            return mutuelle;
        }
    }

    @Override
    public Mutuelle getById(int id) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.*, l.* FROM Mutuelle m ");
        sql.append("JOIN Lieu l ON m.Id_Lieu = l.Id_Lieu ");
        sql.append("WHERE m.Id_Mutuelle = ?");
        sql.toString();

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql))) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractMutuelleFromResultSet(rs);
                }
            } catch (SaisieException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public List<Mutuelle> getAll() throws SQLException {
        List<Mutuelle> mutuelles = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.*, l.* FROM Mutuelle m ");
        sql.append("JOIN Lieu l ON m.Id_Lieu = l.Id_Lieu");
        sql.toString();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(String.valueOf(sql))) {

            while (rs.next()) {
                mutuelles.add(extractMutuelleFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new RuntimeException(e);
        }
        return mutuelles;
    }

    @Override
    public boolean update(Mutuelle mutuelle) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Mutuelle SET mut_nom = ?, mut_taux_prise_en_charge = ?, ");
        sql.append("mut_num_departement = ?, Id_Lieu = ? WHERE Id_Mutuelle = ?");
        sql.toString();

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql))) {
            ps.setString(1, mutuelle.getMutNom());
            ps.setDouble(2, mutuelle.getMutTauxPriseEnCharge());
            ps.setInt(3, mutuelle.getMutNumDepartement());
            ps.setInt(4, mutuelle.getLieu().getId_Lieu());
            ps.setInt(5, mutuelle.getId_Mutuelle());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM Mutuelle WHERE Id_Mutuelle = ?");
        sql.toString();

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql))) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    /**
     * Rechercher des mutuelles par département
     * @param numDepartement Le numéro du département
     * @return Liste des mutuelles du département
     * @throws SQLException
     */
    public List<Mutuelle> getByDepartement(int numDepartement) throws SQLException {
        List<Mutuelle> mutuelles = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT m.*, l.* FROM Mutuelle m ");
        sql.append("JOIN Lieu l ON m.Id_Lieu = l.Id_Lieu ");
        sql.append("WHERE m.mut_num_departement = ?");
        sql.toString();

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql))) {
            ps.setInt(1, numDepartement);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mutuelles.add(extractMutuelleFromResultSet(rs));
                }
            } catch (SaisieException e) {
                throw new RuntimeException(e);
            }
        }
        return mutuelles;
    }

    private Mutuelle extractMutuelleFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        // Extraction du Lieu
        Lieu lieu = new Lieu(
                rs.getString("lieu_adresse"),
                rs.getString("lieu_email"),
                rs.getString("lieu_telephone"),
                rs.getString("lieu_ville"),
                rs.getInt("lieu_cp")
        );
        lieu.setId_Lieu(rs.getInt("Id_Lieu"));

        // Création de la Mutuelle
        Mutuelle mutuelle = new Mutuelle(
                rs.getString("mut_nom"),
                rs.getInt("mut_taux_prise_en_charge"),
                rs.getInt("mut_num_departement"),
                lieu
        );
        mutuelle.setId_Mutuelle(rs.getInt("Id_Mutuelle"));

        return mutuelle;
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }

}
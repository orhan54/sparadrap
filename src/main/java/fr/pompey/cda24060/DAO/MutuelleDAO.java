package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.dataBase.Singleton;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Mutuelle;
import fr.pompey.cda24060.model.Lieu;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MutuelleDAO implements InterfaceDAO<Mutuelle> {

    private Connection connection;

    public MutuelleDAO() throws SQLException, IOException, ClassNotFoundException {
        this.connection = Singleton.getInstanceDB();
    }

    @Override
    public Mutuelle create(Mutuelle mutuelle) throws SQLException {
        String query = "INSERT INTO Mutuelle (mut_nom, mut_taux_prise_en_charge, mut_num_departement, Id_Lieu) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, mutuelle.getNom());
            ps.setDouble(2, mutuelle.getTauxPriseEnCharge());
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
        String query = "SELECT m.*, l.* FROM Mutuelle m " +
                "JOIN Lieu l ON m.Id_Lieu = l.Id_Lieu " +
                "WHERE m.Id_Mutuelle = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
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
        String query = "SELECT m.*, l.* FROM Mutuelle m " +
                "JOIN Lieu l ON m.Id_Lieu = l.Id_Lieu";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

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
        String query = "UPDATE Mutuelle SET mut_nom = ?, mut_taux_prise_en_charge = ?, " +
                "mut_num_departement = ?, Id_Lieu = ? WHERE Id_Mutuelle = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, mutuelle.getNom());
            ps.setDouble(2, mutuelle.getTauxPriseEnCharge());
            ps.setInt(3, mutuelle.getMutNumDepartement());
            ps.setInt(4, mutuelle.getLieu().getId_Lieu());
            ps.setInt(5, mutuelle.getId_Mutuelle());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM Mutuelle WHERE Id_Mutuelle = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
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
        String query = "SELECT m.*, l.* FROM Mutuelle m " +
                "JOIN Lieu l ON m.Id_Lieu = l.Id_Lieu " +
                "WHERE m.mut_num_departement = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
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
}
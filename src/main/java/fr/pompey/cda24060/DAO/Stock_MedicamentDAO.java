package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Pharmacie;
import fr.pompey.cda24060.model.Stock_Medicament;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Stock_MedicamentDAO extends InterfaceDAO<Stock_Medicament> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Stock_Medicament create(Stock_Medicament Stock_Medicament) throws SQLException, IOException, ClassNotFoundException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO STOCK_MEDICAMENT(medic_nom, medic_categorie, medic_quantite, ");
        sql.append("medic_date_mise_en_service, medic_date_entree_stock, medic_prix_unitaire, Id_Pharmacie) VALUES (?,?,?,?,?,?,?);");
        sql.toString();

        // Créer d'abord le pharmacien
        PharmacieDAO pharmacieDAO = new PharmacieDAO();
        Pharmacie pharmacie = pharmacieDAO.create(Stock_Medicament.getPharmacie());
        Stock_Medicament.setPharmacie(pharmacie);

        // Ensuite créer le stock medicament
        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, Stock_Medicament.getMedicNom());
            stmt.setString(2, Stock_Medicament.getMedicCategorie());
            stmt.setInt(3, Stock_Medicament.getQuantite());
            stmt.setDate(4, Stock_Medicament.getDateMiseEnService());
            stmt.setDate(5, Stock_Medicament.getMedicDateEntreeStock());
            stmt.setDouble(6, Stock_Medicament.getMedicPrixUnitaire());
            stmt.setInt(7, pharmacie.getId_Pharmacie());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Échec de la création du stock médicament, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    Stock_Medicament.setId_Stock_Medicament(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la création du stock médicament, aucun ID généré.");
                }
            }
        }

        return Stock_Medicament;
    }

    @Override
    public Stock_Medicament getById(int id) throws SQLException, SaisieException {
        StringBuilder sql = new StringBuilder();
        sql.append(" stck.Id_Stock_Medicament, stck.medic_nom, stck.medic_categorie, stck.medic_quantite, ");
        sql.append("stck.medic_date_mise_en_service, stck.medic_date_entree_stock, stck.medic_prix_unitaire, ");
        sql.append("p.Id_Pharmacie, p.pha_nom, p.pha_prenom ");
        sql.append(" FROM Stock_Medicament AS stck ");
        sql.append(" INNER JOIN Pharmacie AS p ON p.Id_Pharmacie = stck.Id_Pharmacie ");
        sql.append("WHERE stck.Id_Stock_Medicament = ?");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractStockMedicamentFromResultSet(rs);
                }
            }
        } catch (SaisieException e) {
            throw new SaisieException("Erreur lors de la récupération du stock médicament : " + e.getMessage());
        }

        return null;
    }

    @Override
    public List<Stock_Medicament> getAll() throws SQLException {
        List<Stock_Medicament> stockMedicaments = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT stck.Id_Stock_Medicament, stck.medic_nom, stck.medic_categorie, ");
        sql.append("stck.medic_quantite, stck.medic_date_mise_en_service, stck.medic_date_entree_stock, ");
        sql.append("stck.medic_prix_unitaire, p.Id_Pharmacie, p.pha_nom, p.pha_prenom ");
        sql.append("FROM Stock_Medicament AS stck ");
        sql.append("INNER JOIN Pharmacie AS p ON p.Id_Pharmacie = stck.Id_Pharmacie");
        sql.toString();

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql), Statement.RETURN_GENERATED_KEYS)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                stockMedicaments.add(extractStockMedicamentFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des stocks médicaments : " + e.getMessage());
        }

        return stockMedicaments;
    }

    @Override
    public boolean update(Stock_Medicament Stock_Medicament) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("Update Stock_Medicament Set medic_nom =?, medic_categorie=?, medic_quantite=?, ");
        sql.append("medic_date_mise_en_service=?, medic_date_entree_stock=?, medic_prix_unitaire=? ");
        sql.append("WHERE Id_Stock_Medicament = ?");
        sql.toString();

        // Ensuite mettre à jour le stock medicament
        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql))) {
            stmt.setString(1, Stock_Medicament.getMedicNom());
            stmt.setString(2, Stock_Medicament.getMedicCategorie());
            stmt.setInt(3, Stock_Medicament.getQuantite());
            stmt.setDate(4, Stock_Medicament.getDateMiseEnService());
            stmt.setDate(5, Stock_Medicament.getMedicDateEntreeStock());
            stmt.setDouble(6, Stock_Medicament.getMedicPrixUnitaire());
            stmt.setInt(7, Stock_Medicament.getId_Stock_Medicament());

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException, SaisieException {
        // Récuperer le stock medicament pour obtenir l'ID du pharmacien
        Stock_Medicament Stock_Medicament = getById(id);
        if (Stock_Medicament == null) {
            return false;
        }

        // Supprimer le medicament dans le stock
        String sql = "DELETE FROM Stock_Medicament WHERE Id_Stock_Medicament = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) return false;
        }

        return true;
    }

    private Stock_Medicament extractStockMedicamentFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        Pharmacie pharmacie = new Pharmacie(
                rs.getString("pha_nom"),
                rs.getString("pha_prenom")
        );
        pharmacie.setId_Pharmacie(rs.getInt("Id_Pharmacie"));

        Stock_Medicament Stock_Medicament = new Stock_Medicament(
                rs.getString("medic_nom"),
                rs.getString("medic_categorie"),
                rs.getInt("medic_quantite"),
                rs.getDate("medic_date_mise_en_service"),
                rs.getDate("medic_date_entree_stock"),
                rs.getDouble("medic_prix_unitaire"),
                pharmacie
        );
        Stock_Medicament.setId_Stock_Medicament(rs.getInt("Id_Stock_Medicament"));
        return Stock_Medicament;
    }

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }
}
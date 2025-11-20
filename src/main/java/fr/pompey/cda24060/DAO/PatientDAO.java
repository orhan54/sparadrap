package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.dataBase.Singleton;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Patient;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Mutuelle;
import fr.pompey.cda24060.model.Medecin;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO implements InterfaceDAO<Patient> {

    private Connection connection;

    public PatientDAO() throws SQLException, IOException, ClassNotFoundException {
        this.connection = Singleton.getInstanceDB();
    }

    @Override
    public Patient create(Patient patient) throws SQLException {
        String sql = "INSERT INTO Patient (pat_nom, pat_prenom, pat_num_secu, pat_date_naissance, Id_Lieu, Id_Mutuelle, Id_Medecin) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            // Créer d'abord le lieu
            LieuDAO lieuDAO = new LieuDAO();
            Lieu lieu = lieuDAO.create(patient.getLieu());
            patient.setLieu(lieu);

            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, patient.getNom());
                stmt.setString(2, patient.getPrenom());
                stmt.setString(3, patient.getPatNumeSecu());
                stmt.setDate(4, Date.valueOf(patient.getPatDateNaissance()));
                stmt.setInt(5, patient.getLieu().getId_Lieu());
                stmt.setInt(6, patient.getMutuelle().getId_Mutuelle());
                stmt.setInt(7, patient.getMedecin().getId_Medecin());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Échec de la création du patient, aucune ligne affectée.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        patient.setId_Patient(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Échec de la création du médecin, aucun ID généré.");
                    }
                }

                return patient;
            }
        } catch (ClassNotFoundException | java.io.IOException e) {
            throw new SQLException("Erreur lors de l'initialisation de LieuDAO : " + e.getMessage());
        }
    }

    @Override
    public Patient getById(int id) throws SQLException {
        String query = "SELECT p.*, l.*, mut.*, m.* " +
                "FROM Patient p " +
                "JOIN Lieu l ON p.Id_Lieu = l.Id_Lieu " +
                "JOIN Mutuelle mut ON p.Id_Mutuelle = mut.Id_Mutuelle " +
                "LEFT JOIN Medecin m ON p.Id_Medecin = m.Id_Medecin " +
                "WHERE p.Id_Patient = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractPatientFromResultSet(rs);
                }
            } catch (SaisieException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public List<Patient> getAll() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT p.*, l.*, mut.*, m.*" +
                "FROM Patient AS p " +
                "JOIN Lieu AS l ON p.Id_Lieu = l.Id_Lieu " +
                "JOIN Mutuelle AS mut ON p.Id_Mutuelle = mut.Id_Mutuelle " +
                "JOIN Medecin AS m ON p.Id_Medecin = m.Id_Medecin";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                patients.add(extractPatientFromResultSet(rs));
            }
        } catch (SaisieException e) {
            throw new SQLException("Erreur lors de la récupération des patients : " + e.getMessage());
        }
        return patients;
    }

    @Override
    public boolean update(Patient patient) throws SQLException {
        String query = "UPDATE Patient SET pat_nom = ?, pat_prenom = ?, pat_num_secu = ?, " +
                "pat_date_naissance = ?, Id_Lieu = ?, Id_Mutuelle = ?, Id_Medecin = ? WHERE Id_Patient = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setString(3, patient.getPatNumeSecu());
            ps.setDate(4, Date.valueOf(patient.getPatDateNaissance()));
            ps.setInt(5, patient.getLieu().getId_Lieu());
            ps.setInt(6, patient.getMutuelle().getId_Mutuelle());
            if (patient.getMedecin() != null) {
                ps.setInt(7, patient.getMedecin().getId_Medecin());
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setInt(8, patient.getId_Patient());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM Patient WHERE Id_Patient = ?";

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Patient extractPatientFromResultSet(ResultSet rs) throws SQLException, SaisieException {
        // ----- LIEU -----
        Lieu lieu = new Lieu(
                rs.getString("lieu_adresse"),
                rs.getString("lieu_email"),
                rs.getString("lieu_telephone"),
                rs.getString("lieu_ville"),
                rs.getInt("lieu_cp")
        );
        lieu.setId_Lieu(rs.getInt("Id_Lieu"));

        // ----- MUTUELLE -----
        Mutuelle mutuelle = new Mutuelle(
                rs.getString("mut_nom"),
                rs.getInt("mut_taux_prise_en_charge"),
                rs.getInt("mut_num_departement"),
                lieu
        );
        mutuelle.setId_Mutuelle(rs.getInt("Id_Mutuelle"));

        // ----- PATIENT -----
        Medecin medecin = new Medecin(
            rs.getString("med_nom"),
            rs.getString("med_prenom"),
            rs.getString("med_numero_agreement"),
            lieu
        );
        medecin.setId_Medecin(rs.getInt("Id_Medecin"));

        // ----- PATIENT -----
        Patient patient = new Patient(
            rs.getString("pat_nom"),
            rs.getString("pat_prenom"),
            rs.getDate("pat_date_naissance").toLocalDate(),
            lieu,
            mutuelle,
            medecin
        );
        patient.setId_Patient(rs.getInt("Id_Patient"));

        return patient;
    }
}

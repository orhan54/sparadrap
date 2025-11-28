package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Patient;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Mutuelle;
import fr.pompey.cda24060.model.Medecin;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PatientDAO extends InterfaceDAO<Patient> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Patient create(Patient patient) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO Patient (pat_nom, pat_prenom, pat_num_secu, pat_date_naissance, Id_Lieu, Id_Mutuelle, Id_Medecin) VALUES (?, ?, ?, ?, ?, ?, ?)");
        sql.toString();

        // Créer d'abord le lieu
        LieuDAO lieuDAO = new LieuDAO();
        Lieu lieu = lieuDAO.create(patient.getLieu());
        patient.setLieu(lieu);

        try (PreparedStatement stmt = connection.prepareStatement(String.valueOf(sql), Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, patient.getNom());
            stmt.setString(2, patient.getPrenom());
            stmt.setString(3, patient.getPatNumeSecu());
            stmt.setDate(4, Date.valueOf(String.valueOf(patient.getPatDateNaissance())));
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
    }

    @Override
    public Patient getById(int id) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, l.*, mut.*, m.* ");
        sql.append("FROM Patient AS p ");
        sql.append("JOIN Lieu AS l ON p.Id_Lieu = l.Id_Lieu ");
        sql.append("JOIN Mutuelle AS mut ON p.Id_Mutuelle = mut.Id_Mutuelle ");
        sql.append("LEFT JOIN Medecin AS m ON p.Id_Medecin = m.Id_Medecin ");
        sql.append("WHERE p.Id_Patient = ?");
        sql.toString();

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql))) {
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
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, l.*, mut.*, m.* " );
        sql.append("FROM Patient AS p ");
        sql.append("JOIN Lieu AS l ON p.Id_Lieu = l.Id_Lieu ");
        sql.append("JOIN Mutuelle AS mut ON p.Id_Mutuelle = mut.Id_Mutuelle ");
        sql.append("JOIN Medecin AS m ON p.Id_Medecin = m.Id_Medecin");
        sql.toString();

        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(String.valueOf(sql))) {

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
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE Patient SET pat_nom = ?, pat_prenom = ?, pat_num_secu = ?, ");
        sql.append("pat_date_naissance = ?, Id_Lieu = ?, Id_Mutuelle = ?, Id_Medecin = ? WHERE Id_Patient = ?");
        sql.toString();

        // Mettre à jour d'abord le lieu
        LieuDAO lieuDAO = new LieuDAO();
        lieuDAO.update(patient.getLieu());

        try (PreparedStatement ps = connection.prepareStatement(String.valueOf(sql))) {
            ps.setString(1, patient.getNom());
            ps.setString(2, patient.getPrenom());
            ps.setString(3, patient.getPatNumeSecu());
            ps.setDate(4, Date.valueOf(patient.getPatDateNaissance().toLocalDate()));
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
        Connection conn = null;
        try {
            conn = connection;
            conn.setAutoCommit(false); // Démarrer une transaction

            // 1. Récupérer l'Id_Lieu du patient avant suppression
            StringBuilder getLieuQuery = new StringBuilder();
            getLieuQuery.append("SELECT Id_Lieu FROM Patient WHERE Id_Patient = ?");
            getLieuQuery.toString();
            int idLieu = 0;

            try (PreparedStatement ps = conn.prepareStatement(String.valueOf(getLieuQuery))) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idLieu = rs.getInt("Id_Lieu");
                    } else {
                        conn.rollback();
                        return false; // Patient n'existe pas
                    }
                }
            }

            // 2. Supprimer le patient (Id_Mutuelle sera mis à NULL automatiquement)
            StringBuilder deletePatientQuery = new StringBuilder();
            deletePatientQuery.append("DELETE FROM Patient WHERE Id_Patient = ?");
            deletePatientQuery.toString();

            try (PreparedStatement ps = conn.prepareStatement(String.valueOf(deletePatientQuery))) {
                ps.setInt(1, id);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }

            // 3. Supprimer le lieu associé
            StringBuilder deleteLieuQuery = new StringBuilder();
            deleteLieuQuery.append("DELETE FROM Lieu WHERE Id_Lieu = ?");
            deleteLieuQuery.toString();

            try (PreparedStatement ps = conn.prepareStatement(String.valueOf(deleteLieuQuery))) {
                ps.setInt(1, idLieu);
                ps.executeUpdate();
            }

            conn.commit(); // Valider la transaction
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Annuler en cas d'erreur
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Erreur lors de la suppression du patient : " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Restaurer le mode auto-commit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
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

        // ----- MEDECIN -----
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

    @Override
    public void closeConnection() throws SQLException {
        super.closeConnection();
    }
}
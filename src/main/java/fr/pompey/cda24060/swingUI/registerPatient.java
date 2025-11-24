package fr.pompey.cda24060.swingUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fr.pompey.cda24060.DAO.MedecinDAO;
import fr.pompey.cda24060.DAO.MutuelleDAO;
import fr.pompey.cda24060.DAO.PatientDAO;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Medecin;
import fr.pompey.cda24060.model.Mutuelle;
import fr.pompey.cda24060.model.Patient;
import fr.pompey.cda24060.utility.RegexUtility;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class registerPatient extends JFrame {
    private JPanel contentPane;
    private JPanel logoRegisterClient;
    private JPanel mainRegisterClient;
    private JPanel footerRegisterClient;
    private JButton buttonRetourRegisterClient;
    private JButton buttonValideRegisterClient;
    private JTextField textFieldRegisterNom;
    private JTextField textFieldRegisterPrenom;
    private JTextField textFieldRegisterAdresse;
    private JTextField textFieldRegisterCodePostal;
    private JTextField textFieldRegisterVille;
    private JTextField textFieldRegisterTel;
    private JTextField textFieldRegisterEmail;
    private JTextField textFieldregisterNumSecu;
    private JTextField textFieldRegisterDateNaissance;
    private JLabel titreRegister;
    private JButton quitterButton;
    private JComboBox<String> comboBoxNomMedecin;
    private JComboBox<String> comboBoxMutuelle;
    private JFrame previousFrame;
    private MedecinDAO medecinDAO;
    private MutuelleDAO mutuelleDAO;
    private PatientDAO patientDAO;

    // Patient en cours (null = création, sinon update)
    private Patient currentPatient;

    // Listes chargées depuis la BDD
    private List<Medecin> medecins;
    private List<Mutuelle> mutuelles;

    /**
     * Constructeur pour la création d'un nouveau patient
     */
    public registerPatient(JFrame previousFrame) {
        this.previousFrame = previousFrame;

        // Initialiser les DAO
        try {
            this.medecinDAO = new MedecinDAO();
            this.mutuelleDAO = new MutuelleDAO();
            this.patientDAO = new PatientDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

        initUI();
        chargerDonneesDepuisBDD();
        remplirComboBox();

        // Actions boutons
        buttonRetourRegisterClient.addActionListener(e -> retour());
        buttonValideRegisterClient.addActionListener(e -> {
            try {
                valider();
            } catch (SaisieException ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
            }
        });
        quitterButton.addActionListener(e -> quitter());
    }

    /**
     * Constructeur pour l'édition d'un patient existant
     */
    public registerPatient(Patient patient, JFrame previousFrame) {
        this.previousFrame = previousFrame;

        // Initialiser les DAO
        try {
            this.medecinDAO = new MedecinDAO();
            this.mutuelleDAO = new MutuelleDAO();
            this.patientDAO = new PatientDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

        initUI();
        chargerDonneesDepuisBDD();
        remplirComboBox();

        this.currentPatient = patient;

        if (patient != null) {
            textFieldRegisterNom.setText(patient.getNom());
            textFieldRegisterPrenom.setText(patient.getPrenom());
            textFieldRegisterAdresse.setText(patient.getLieu().getAdresse());
            textFieldRegisterCodePostal.setText(String.valueOf(patient.getLieu().getCodePostal()));
            textFieldRegisterVille.setText(patient.getLieu().getVille());
            textFieldRegisterTel.setText(patient.getLieu().getTelephone());
            textFieldRegisterEmail.setText(patient.getLieu().getEmail());
            textFieldregisterNumSecu.setText(patient.getPatNumeSecu());
            textFieldRegisterDateNaissance.setText(String.valueOf(patient.getPatDateNaissance()));

            // Sélectionner la mutuelle
            if (patient.getMutuelle() != null) {
                comboBoxMutuelle.setSelectedItem(patient.getMutuelle().getNom());
            }

            // Sélectionner le médecin
            if (patient.getMedecin() != null) {
                comboBoxNomMedecin.setSelectedItem(patient.getMedecin().getNom() + " " + patient.getMedecin().getPrenom());
            }
        }

        // Actions boutons
        buttonRetourRegisterClient.addActionListener(e -> retour());
        buttonValideRegisterClient.addActionListener(e -> {
            try {
                valider();
            } catch (SaisieException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur sur la validation de la mise à jour patient : " + ex.getMessage());
            }
        });
        quitterButton.addActionListener(e -> quitter());
    }

    /**
     * Charger les médecins et mutuelles depuis la BDD
     */
    private void chargerDonneesDepuisBDD() {
        try {
            if (medecinDAO != null) {
                medecins = medecinDAO.getAll();
                System.out.println("Chargement de " + medecins.size() + " médecins depuis la BDD");
            }

            if (mutuelleDAO != null) {
                mutuelles = mutuelleDAO.getAll();
                System.out.println("Chargement de " + mutuelles.size() + " mutuelles depuis la BDD");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Initialisation de la fenêtre
     */
    private void initUI() {
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/miniLogo.png")));
        Dimension dimension = new Dimension(1600, 1000);

        this.setTitle("Sparadrap");
        this.setIconImage(imageIcon.getImage());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(dimension);
        this.setResizable(false);
        this.setContentPane(contentPane);

        this.pack();
        this.setLocationRelativeTo(null);

        // Gestionnaire pour la croix (X)
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                retour();
            }
        });
    }

    /**
     * Remplir les JComboBox avec les listes de médecins et mutuelles
     */
    private void remplirComboBox() {
        comboBoxNomMedecin.removeAllItems();
        comboBoxNomMedecin.addItem("Choisir un médecin");
        comboBoxNomMedecin.setSelectedIndex(0);

        if (medecins != null) {
            for (Medecin med : medecins) {
                comboBoxNomMedecin.addItem(med.getNom() + " " + med.getPrenom());
            }
        }

        comboBoxMutuelle.removeAllItems();
        comboBoxMutuelle.addItem("Choisir une mutuelle");
        comboBoxMutuelle.setSelectedIndex(0);

        if (mutuelles != null) {
            for (Mutuelle mut : mutuelles) {
                comboBoxMutuelle.addItem(mut.getNom());
            }
        }
    }

    /**
     * Retour à la fenêtre précédente
     */
    private void retour() {
        if (previousFrame != null) {
            previousFrame.setVisible(true);
        }
        this.dispose();
    }

    /**
     * Validation du formulaire
     */
    private void valider() throws SaisieException {
        try {
            // --------------- RÉCUPÉRATION DES CHAMPS ---------------
            String nom = textFieldRegisterNom.getText().trim();
            String prenom = textFieldRegisterPrenom.getText().trim();
            String adresse = textFieldRegisterAdresse.getText().trim();
            String ville = textFieldRegisterVille.getText().trim();
            String tel = textFieldRegisterTel.getText().trim();
            String email = textFieldRegisterEmail.getText().trim();
            String numSecu = textFieldregisterNumSecu.getText().trim();
            String dateNaissanceStr = textFieldRegisterDateNaissance.getText().trim();
            String codePostalText = textFieldRegisterCodePostal.getText().trim();

            // --------------- VALIDATION DES CHAMPS ---------------
            if (nom.isEmpty() || prenom.isEmpty() || adresse.isEmpty() ||
                    ville.isEmpty() || tel.isEmpty() || email.isEmpty() ||
                    numSecu.isEmpty() || dateNaissanceStr.isEmpty() || codePostalText.isEmpty()) {
                throw new SaisieException("Tous les champs obligatoires doivent être remplis !");
            }

            // Validation avec regex
            if (!RegexUtility.regexAlpha(nom)) {
                throw new SaisieException("Nom invalide !");
            }
            if (!RegexUtility.regexAlpha(prenom)) {
                throw new SaisieException("Prénom invalide !");
            }
            if (!RegexUtility.validateAdresse(adresse)) {
                throw new SaisieException("Adresse invalide !");
            }
            if (!RegexUtility.validatePhone(tel)) {
                throw new SaisieException("Numéro de téléphone invalide !");
            }
            if (!RegexUtility.validate(email)) {
                throw new SaisieException("Email invalide !");
            }
            if (numSecu.length() != 15) {
                throw new SaisieException("Le numéro de sécurité sociale doit contenir 15 chiffres !");
            }

            int codePostal;
            try {
                codePostal = Integer.parseInt(codePostalText);
            } catch (NumberFormatException e) {
                throw new SaisieException("Code postal invalide ! Veuillez saisir un nombre.");
            }

            Date dateNaissance;
            try {
                dateNaissance = Date.valueOf(dateNaissanceStr);
            } catch (IllegalArgumentException e) {
                throw new SaisieException("Format de date invalide ! Utilisez le format YYYY-MM-DD");
            }

            // --------------- RÉCUPÉRATION MUTUELLE ET MÉDECIN ---------------
            String mutuelleNom = (String) comboBoxMutuelle.getSelectedItem();
            String medecinNomComplet = (String) comboBoxNomMedecin.getSelectedItem();

            if (mutuelleNom == null || mutuelleNom.equals("Choisir une mutuelle")) {
                throw new SaisieException("Veuillez sélectionner une mutuelle !");
            }

            if (medecinNomComplet == null || medecinNomComplet.equals("Choisir un médecin")) {
                throw new SaisieException("Veuillez sélectionner un médecin !");
            }

            Mutuelle mutuelleChoisie = null;
            for (Mutuelle m : mutuelles) {
                if (m.getNom().equals(mutuelleNom)) {
                    mutuelleChoisie = m;
                    break;
                }
            }

            Medecin medecinChoisi = null;
            for (Medecin med : medecins) {
                String nomComplet = med.getNom() + " " + med.getPrenom();
                if (nomComplet.equals(medecinNomComplet)) {
                    medecinChoisi = med;
                    break;
                }
            }

            if (mutuelleChoisie == null) {
                throw new SaisieException("Mutuelle non trouvée !");
            }

            if (medecinChoisi == null) {
                throw new SaisieException("Médecin non trouvé !");
            }

            // Instanciation du DAO
            PatientDAO patientDAO = new PatientDAO();

            // -------- MODE UPDATE --------
            if (currentPatient != null) {
                currentPatient.setNom(nom);
                currentPatient.setPrenom(prenom);
                currentPatient.setPatNumSecu(numSecu);
                currentPatient.setPatDateNaissance(dateNaissance);

                Lieu lieu = currentPatient.getLieu();
                lieu.setAdresse(adresse);
                lieu.setEmail(email);
                lieu.setTelephone(tel);
                lieu.setVille(ville);
                lieu.setCodePostal(codePostal);

                currentPatient.setMutuelle(mutuelleChoisie);
                currentPatient.setMedecin(medecinChoisi);

                // Mise à jour via DAO
                patientDAO.update(currentPatient);

                JOptionPane.showMessageDialog(this,
                        "Patient mis à jour avec succès !",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);

            } else {
                // -------- MODE CREATION --------
                Lieu lieu = new Lieu(adresse, email, tel, ville, codePostal);
                Patient patient = new Patient(nom, prenom, dateNaissance.toLocalDate(),
                        lieu, mutuelleChoisie, medecinChoisi);

                // Création via DAO
                Patient createdPatient = patientDAO.create(patient);

                if (createdPatient != null) {
                    JOptionPane.showMessageDialog(this,
                            "Nouveau patient ajouté avec succès !",
                            "Succès",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    throw new SaisieException("Échec de la création du patient");
                }
            }

            // Retour vers consulterPatient
            if (previousFrame instanceof consulterPatient) {
                ((consulterPatient) previousFrame).rafraichirAffichage();
            }

            if (previousFrame != null) {
                previousFrame.setVisible(true);
            }
            this.dispose();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de l'enregistrement : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Quitter l'application
     */
    private void quitter() {
        int reponse = JOptionPane.showConfirmDialog(registerPatient.this,
                "Voulez-vous quitter l'application ?", "Quitter",
                JOptionPane.YES_NO_OPTION);
        if (reponse == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        contentPane = new JPanel();
        contentPane.setLayout(new GridLayoutManager(6, 1, new Insets(0, 0, 0, 0), -1, -1));
        logoRegisterClient = new JPanel();
        logoRegisterClient.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(logoRegisterClient, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        logoRegisterClient.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        titreRegister = new JLabel();
        Font titreRegisterFont = this.$$$getFont$$$("Cooper Black", -1, 28, titreRegister.getFont());
        if (titreRegisterFont != null) titreRegister.setFont(titreRegisterFont);
        titreRegister.setText("Pharmacie Sparadrap");
        panel1.add(titreRegister, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        logoRegisterClient.add(panel2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/image/pharmacy.png")));
        label1.setText("");
        panel2.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        mainRegisterClient = new JPanel();
        mainRegisterClient.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(mainRegisterClient, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(6, 1, new Insets(10, 10, 10, 10), -1, -1));
        mainRegisterClient.add(panel3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Nom              ");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterNom = new JTextField();
        panel4.add(textFieldRegisterNom, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel5, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Ville               ");
        panel5.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterVille = new JTextField();
        panel5.add(textFieldRegisterVille, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Code postal ");
        panel6.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterCodePostal = new JTextField();
        panel6.add(textFieldRegisterCodePostal, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel7, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Adresse       ");
        panel7.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterAdresse = new JTextField();
        panel7.add(textFieldRegisterAdresse, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Prenom        ");
        panel8.add(label6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterPrenom = new JTextField();
        panel8.add(textFieldRegisterPrenom, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel9, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Téléphone   ");
        panel9.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterTel = new JTextField();
        panel9.add(textFieldRegisterTel, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(5, 1, new Insets(10, 10, 10, 10), -1, -1));
        mainRegisterClient.add(panel10, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel11, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label8 = new JLabel();
        label8.setText("Mutuelle                    ");
        panel11.add(label8, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxMutuelle = new JComboBox();
        panel11.add(comboBoxMutuelle, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel12, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label9 = new JLabel();
        label9.setText("Date de naissance ");
        panel12.add(label9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterDateNaissance = new JTextField();
        panel12.add(textFieldRegisterDateNaissance, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label10 = new JLabel();
        label10.setText("Numéro sécu           ");
        panel13.add(label10, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldregisterNumSecu = new JTextField();
        panel13.add(textFieldregisterNumSecu, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label11 = new JLabel();
        label11.setText("Email                          ");
        panel14.add(label11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldRegisterEmail = new JTextField();
        panel14.add(textFieldRegisterEmail, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel15, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label12 = new JLabel();
        label12.setText("Medecin                    ");
        panel15.add(label12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxNomMedecin = new JComboBox();
        panel15.add(comboBoxNomMedecin, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        footerRegisterClient = new JPanel();
        footerRegisterClient.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(footerRegisterClient, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        footerRegisterClient.add(panel16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        footerRegisterClient.add(panel17, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        footerRegisterClient.add(panel18, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label13 = new JLabel();
        label13.setIcon(new ImageIcon(getClass().getResource("/image/right.png")));
        label13.setText("Sparadrap 2025");
        panel18.add(label13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel19, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel21, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel22, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonRetourRegisterClient = new JButton();
        buttonRetourRegisterClient.setIcon(new ImageIcon(getClass().getResource("/image/previous.png")));
        buttonRetourRegisterClient.setText("Retour");
        panel22.add(buttonRetourRegisterClient, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel23, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonValideRegisterClient = new JButton();
        buttonValideRegisterClient.setIcon(new ImageIcon(getClass().getResource("/image/accept.png")));
        buttonValideRegisterClient.setText("valider");
        panel23.add(buttonValideRegisterClient, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel24, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label14 = new JLabel();
        Font label14Font = this.$$$getFont$$$(null, -1, 16, label14.getFont());
        if (label14Font != null) label14.setFont(label14Font);
        label14.setText("Enregistrer un client");
        panel24.add(label14, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel25, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        quitterButton = new JButton();
        quitterButton.setIcon(new ImageIcon(getClass().getResource("/image/switch.png")));
        quitterButton.setText("Quitter");
        panel25.add(quitterButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return contentPane;
    }
}
package fr.pompey.cda24060.swingUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fr.pompey.cda24060.DAO.MedecinDAO;
import fr.pompey.cda24060.model.Medecin;
import fr.pompey.cda24060.model.Ordonnance;
import fr.pompey.cda24060.model.Patient;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class consulterMedecin extends JFrame {
    private JPanel contentPane;
    private JLabel titreMenu;
    private JTable tableMedecin;
    private JButton modifierButton;
    private JButton supprimerButton;
    private JButton quitterButton;
    private JButton retourButton;
    private JComboBox<String> comboBoxMedecin;
    private JButton creerUnMedecinButton;
    private JComboBox<String> comboBoxInformation;
    private JTable tableFiltreInformation;
    private JLabel titreFiltreInfo;
    private String selectedValue;
    private JFrame previousFrame;
    private MedecinDAO medecinDAO;

    private DefaultTableModel tableModelMedecin;

    private String[] HEADER_PATIENT = new String[]{
            "Nom", "Prenom", "Adresse", "Code postal", "Ville",
            "Téléphone", "Email", "Numero sécurité social", "Date de naissance",
            "Mutuelle", "Medecin"
    };

    private String[] HEADER_ORDONNANCE = new String[]{
            "Date", "Nom médecin", "Nom patient", "Liste des médicaments"
    };

    public consulterMedecin(JFrame previousFrame) {
        this.previousFrame = previousFrame;

        // Initialiser le DAO
        try {
            this.medecinDAO = new MedecinDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/miniLogo.png")));
        Dimension dimension = new Dimension(1600, 1000);

        // Attributs JFrame
        this.setTitle("Sparadrap");
        this.setIconImage(imageIcon.getImage());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(dimension);
        this.setResizable(false);
        this.setContentPane(contentPane);

        // Table médecin
        String[] colonnes = {"Nom", "Prénom", "Adresse", "Code postal", "Ville", "Téléphone", "Email", "Numéro d'agrément"};
        tableModelMedecin = new DefaultTableModel(colonnes, 0);
        tableMedecin.setModel(tableModelMedecin);

        // ComboBox Information
        comboBoxInformation.addItem("Choisir le filtre...");
        comboBoxInformation.addItem("Liste des patients du médecin");
        comboBoxInformation.addItem("Liste des ordonnances du médecin");
        comboBoxInformation.setSelectedIndex(0);

        // Charger les médecins depuis la BDD
        chargerMedecinsDepuisBDD();

        // Listeners
        remplirComboBox();

        comboBoxInformation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayInformation();
            }
        });

        retourButton.addActionListener(e -> retour());
        quitterButton.addActionListener(e -> quitter());
        creerUnMedecinButton.addActionListener(e -> creerMedecin());
        modifierButton.addActionListener(e -> updateMedecin());
        supprimerButton.addActionListener(e -> deleteMedecin());

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
     * Charge les médecins depuis la base de données et met à jour la liste statique
     */
    private void chargerMedecinsDepuisBDD() {
        try {
            if (medecinDAO != null) {
                List<Medecin> medecinsFromDB = medecinDAO.getAll();

                // Vider la liste statique
                Medecin.getMedecins().clear();

                // Ajouter tous les médecins de la BDD
                Medecin.getMedecins().addAll(medecinsFromDB);

                System.out.println("Chargement de " + medecinsFromDB.size() + " médecins depuis la BDD");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors du chargement des médecins: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Rafraîchit l'affichage du comboBox après une modification
     */
    public void rafraichirAffichage() {
        // Recharger depuis la BDD
        chargerMedecinsDepuisBDD();

        // Remplir à nouveau le comboBox
        remplirComboBox();

        // Réinitialiser le tableau
        tableModelMedecin.setRowCount(0);
        selectedValue = null;
    }

    // Affichage dynamique Patients / Ordonnances
    public void displayInformation() {
        String selectedMedecin = (String) comboBoxMedecin.getSelectedItem();
        int selectedInfo = comboBoxInformation.getSelectedIndex();

        if (selectedInfo == 0 || selectedMedecin == null || selectedMedecin.equals("Choisir un médecin")) {
            titreFiltreInfo.setText("Choisir un filtre...");
            return;
        }

        Medecin medecinChoisi = Medecin.getMedecins().stream()
                .filter(m -> (m.getNom() + " " + m.getPrenom()).equals(selectedMedecin))
                .findFirst()
                .orElse(null);

        if (medecinChoisi == null) {
            return;
        }

        if (selectedInfo == 1) {
            // Patients du médecin
            titreFiltreInfo.setText("Liste des patients du médecin : " + selectedMedecin);

            List<Patient> patientsMedecin = Patient.getPatients().stream()
                    .filter(p -> p.getMedecin() != null && p.getMedecin().equals(medecinChoisi))
                    .collect(Collectors.toList());

            configureTable(HEADER_PATIENT);
            constructDataTable(patientsMedecin);

        } else if (selectedInfo == 2) {
            // Ordonnances du médecin
            titreFiltreInfo.setText("Liste des ordonnances du médecin : " + selectedMedecin);

            List<Ordonnance> ordonnancesMedecin = Ordonnance.getOrdonnances().stream()
                    .filter(o -> o.getNomMedecin().equals(medecinChoisi.getNom() + " " + medecinChoisi.getPrenom()))
                    .collect(Collectors.toList());

            configureTable(HEADER_ORDONNANCE);
            constructDataTable(ordonnancesMedecin);
        }
    }

    // Préparer le modèle de table
    private void configureTable(String[] header) {
        TableModel model = new DefaultTableModel(header, 0);
        this.tableFiltreInformation.setModel(model);
        this.tableFiltreInformation.revalidate();
        this.tableFiltreInformation.repaint();
    }

    // Remplir le tableau avec les données
    private <T> void constructDataTable(List<T> dataListe) {
        DefaultTableModel model = (DefaultTableModel) this.tableFiltreInformation.getModel();
        model.setRowCount(0);

        for (T obj : dataListe) {
            if (obj instanceof Patient) {
                Patient p = (Patient) obj;
                model.addRow(new Object[]{
                        p.getNom(),
                        p.getPrenom(),
                        p.getLieu().getAdresse(),
                        p.getLieu().getCodePostal(),
                        p.getLieu().getVille(),
                        p.getLieu().getTelephone(),
                        p.getLieu().getEmail(),
                        p.getPatNumeSecu(),
                        p.getPatDateNaissance(),
                        p.getMutuelle() != null ? p.getMutuelle().getNom() : "",
                        p.getMedecin() != null ? p.getMedecin().getNom() + " " + p.getMedecin().getPrenom() : ""
                });
            } else if (obj instanceof Ordonnance) {
                Ordonnance o = (Ordonnance) obj;

                String medicamentsStr = o.getMedicaments().stream()
                        .map(m -> m.getMedicNom() + " (" + m.getQuantite() + ")")
                        .collect(Collectors.joining(", "));

                model.addRow(new Object[]{
                        o.getDate(),
                        o.getNomMedecin(),
                        o.getNomPatient(),
                        medicamentsStr
                });
            }
        }
    }

    // Remplir la comboBox médecin
    private void remplirComboBox() {
        comboBoxMedecin.removeAllItems();

        comboBoxMedecin.addItem("Choisir un médecin");
        comboBoxMedecin.setSelectedIndex(0);

        for (Medecin m : Medecin.getMedecins()) {
            comboBoxMedecin.addItem(m.getNom() + " " + m.getPrenom());
        }

        comboBoxMedecin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = (String) comboBoxMedecin.getSelectedItem();
                selectedValue = selected;
                tableModelMedecin.setRowCount(0);

                for (Medecin m : Medecin.getMedecins()) {
                    if (selectedValue != null && selectedValue.equals(m.getNom() + " " + m.getPrenom())) {
                        tableModelMedecin.addRow(new Object[]{
                                m.getNom(),
                                m.getPrenom(),
                                m.getLieu().getAdresse(),
                                m.getLieu().getCodePostal(),
                                m.getLieu().getVille(),
                                m.getLieu().getTelephone(),
                                m.getLieu().getEmail(),
                                m.getNumeroAgreement()
                        });
                    }
                }
            }
        });
    }

    // Appel de la view pour créer un médecin
    private void creerMedecin() {
        registerMedecin registerMedecin = new registerMedecin(this);
        registerMedecin.setVisible(true);
        this.setVisible(false);
    }

    // Update un médecin
    private void updateMedecin() {
        try {
            if (selectedValue == null || selectedValue.equals("Choisir un médecin")) {
                JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un médecin à modifier",
                        "Attention",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            for (Medecin m : Medecin.getMedecins()) {
                if (selectedValue.equals(m.getNom() + " " + m.getPrenom())) {
                    registerMedecin updateMedecin = new registerMedecin(m, this);
                    updateMedecin.setVisible(true);
                    this.setVisible(false);
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error updateMedecin: " + e.getMessage());
        }
    }

    // Delete un médecin
    private void deleteMedecin() {
        int selectedRow = tableMedecin.getSelectedRow();

        if (selectedRow >= 0 && selectedValue != null && !selectedValue.equals("Choisir un médecin")) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce médecin ?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                Medecin medecinToRemove = null;
                for (Medecin m : Medecin.getMedecins()) {
                    if (selectedValue.equals(m.getNom() + " " + m.getPrenom())) {
                        medecinToRemove = m;
                        break;
                    }
                }

                if (medecinToRemove != null) {
                    try {
                        // Supprimer de la BDD
                        boolean deleted = medecinDAO.delete(medecinToRemove.getId_Medecin());

                        if (deleted) {
                            // Supprimer de la liste statique
                            Medecin.getMedecins().remove(medecinToRemove);

                            // Rafraîchir l'affichage
                            rafraichirAffichage();

                            JOptionPane.showMessageDialog(this,
                                    "Médecin supprimé avec succès",
                                    "Succès",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this,
                                    "Erreur lors de la suppression du médecin",
                                    "Erreur",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (SQLException e) {
                        JOptionPane.showMessageDialog(this,
                                "Erreur lors de la suppression: " + e.getMessage(),
                                "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un médecin à supprimer",
                    "Attention",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // Retour de la page
    private void retour() {
        if (previousFrame != null) {
            previousFrame.setVisible(true);
        }
        this.dispose();
    }

    // Quitter l'application
    private void quitter() {
        int reponse = JOptionPane.showConfirmDialog(consulterMedecin.this,
                "Voulez-vous quitter l'application ?",
                "Quitter",
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
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        titreMenu = new JLabel();
        Font titreMenuFont = this.$$$getFont$$$("Cooper Black", -1, 28, titreMenu.getFont());
        if (titreMenuFont != null) titreMenu.setFont(titreMenuFont);
        titreMenu.setText("Pharmacie Sparadrap");
        panel2.add(titreMenu, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/image/pharmacy.png")));
        label1.setText("");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/image/right.png")));
        label2.setText("Sparadrap 2025");
        panel4.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(null, "Information du medecin", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel7.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableMedecin = new JTable();
        scrollPane1.setViewportView(tableMedecin);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        creerUnMedecinButton = new JButton();
        creerUnMedecinButton.setText("Créer un Médecin");
        panel9.add(creerUnMedecinButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel10, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        supprimerButton = new JButton();
        supprimerButton.setText("Supprimer");
        panel10.add(supprimerButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel11, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        modifierButton = new JButton();
        modifierButton.setText("Modifier");
        panel11.add(modifierButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel12, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel12.setBorder(BorderFactory.createTitledBorder(null, "Option filtre du médecin", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel12.add(scrollPane2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tableFiltreInformation = new JTable();
        scrollPane2.setViewportView(tableFiltreInformation);
        titreFiltreInfo = new JLabel();
        titreFiltreInfo.setText("");
        panel12.add(titreFiltreInfo, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 30, 0), -1, -1));
        panel6.add(panel13, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel14, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel15, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel17, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel18, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        retourButton = new JButton();
        retourButton.setIcon(new ImageIcon(getClass().getResource("/image/previous.png")));
        retourButton.setText("Retour");
        panel18.add(retourButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        quitterButton = new JButton();
        quitterButton.setIcon(new ImageIcon(getClass().getResource("/image/switch.png")));
        quitterButton.setText("Quitter");
        panel18.add(quitterButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(2, 3, new Insets(0, 150, 0, 0), -1, -1));
        contentPane.add(panel19, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 100), -1, -1));
        panel19.add(panel20, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxInformation = new JComboBox();
        panel20.add(comboBoxInformation, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel21, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxMedecin = new JComboBox();
        panel21.add(comboBoxMedecin, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Medecin");
        panel19.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("filtre patient et ordonnance du médecin");
        panel19.add(label4, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel22, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
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
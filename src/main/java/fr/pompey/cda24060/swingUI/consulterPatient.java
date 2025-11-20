package fr.pompey.cda24060.swingUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fr.pompey.cda24060.DAO.PatientDAO;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Medecin;
import fr.pompey.cda24060.model.Patient;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
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

public class consulterPatient extends JFrame {
    private JPanel contentPane;
    private JLabel titreMenu;
    private JTable tablePatient;
    private JButton modifierButton;
    private JButton supprimerButton;
    private JButton retourButton;
    private JButton quitterButton;
    private JButton créerUnCompteButton;
    private JComboBox comboBoxClient;
    private String selectedValue;
    private JFrame previousFrame;
    private PatientDAO patientDAO;

    private DefaultTableModel tableModelPatient;

    public consulterPatient(JFrame previousFrame) throws SaisieException {
        this.previousFrame = previousFrame;

        // Initialiser la DAO
        try {
            this.patientDAO = new PatientDAO();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Erreur de connexion à la base de données: " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }

        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/miniLogo.png")));
        Dimension dimension = new Dimension(1600, 1000);

        //les attributs
        this.setTitle("Sparadrap");
        this.setIconImage(imageIcon.getImage());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(dimension);
        this.setResizable(false);
        this.setContentPane(contentPane);

        String[] colonnes = {"Nom", "Prenom", "Adresse", "Code postal", "Ville", "Téléphone", "Email", "Numero sécurité social", "Date de naissance", "Mutuelle", "Medecin"};
        tableModelPatient = new DefaultTableModel(colonnes, 0);
        tablePatient.setModel(tableModelPatient);

        // Charger les patients depuis la BDD
        chargerPatientsDepuisBDD();

        // Ajout list item dans combobox
        remplirComboBox();

        this.pack();
        this.setLocationRelativeTo(null);

        // Gestionnaire pour la croix (X)
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                retour();
            }
        });

        créerUnCompteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addClient();
            }
        });

        modifierButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateClient();
            }
        });

        supprimerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClient();
            }
        });

        retourButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retour();
            }
        });

        quitterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                quitter();
            }
        });
    }

    /**
     * Charge les patients depuis la base de données et met à jour la liste statique
     */
    private void chargerPatientsDepuisBDD() {
        try {
            if (patientDAO != null) {
                List<Patient> patientsFromDB = patientDAO.getAll();

                // Vider la liste statique
                Patient.getPatients().clear();

                // Ajouter tous les médecins de la BDD
                Patient.getPatients().addAll(patientsFromDB);

                System.out.println("Chargement de " + patientsFromDB.size() + " médecins depuis la BDD");
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
    public void rafraichirAffichage() throws SaisieException {
        // Recharger depuis la BDD
        chargerPatientsDepuisBDD();

        // Remplir à nouveau le comboBox
        remplirComboBox();

        // Réinitialiser le tableau
        tableModelPatient.setRowCount(0);
        selectedValue = null;
    }

    private void remplirComboBox() throws SaisieException {
        comboBoxClient.removeAllItems();

        comboBoxClient.addItem("Choisir un client");
        comboBoxClient.setSelectedItem(0);

        for (Patient p : Patient.getPatients()) {
            comboBoxClient.addItem(p.getNom() + " " + p.getPrenom());
        }

        comboBoxClient.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                e.getSource();

                String selected = (String) comboBoxClient.getSelectedItem();
                //System.out.println("Vous avez séléctionné : " + selected);
                selectedValue = selected;

                if (selected.equals(comboBoxClient.getSelectedItem())) {
                    tableModelPatient.setRowCount(0);

                    for (Patient p : Patient.getPatients()) {
                        if (selected.equals(p.getNom() + " " + p.getPrenom())) {
                            tableModelPatient.addRow(new Object[]{
                                    p.getNom(),
                                    p.getPrenom(),
                                    p.getLieu().getAdresse(),
                                    p.getLieu().getCodePostal(),
                                    p.getLieu().getVille(),
                                    p.getLieu().getTelephone(),
                                    p.getLieu().getEmail(),
                                    p.getPatNumeSecu(),
                                    p.getPatDateNaissance(),
                                    p.getMutuelle().getNom(),
                                    p.getMedecin().getNom() + " " + p.getMedecin().getPrenom()
                            });
                        }
                    }
                }
            }
        });
    }

    // Appel de la view pour créer un client
    private void addClient() {
        try {
            registerPatient registerClient = new registerPatient(this);
            registerClient.setVisible(true);
            this.setVisible(false); // Cache la fenêtre consulterClient
        } catch (Exception e) {
            System.out.println("Erreur sur la vue créer un client" + e.getMessage());
        }
    }

    // Maj d'un client
    private void updateClient() {
        try {
            String selectedClient = comboBoxClient.getSelectedItem().toString();
            for (Patient p : Patient.getPatients()) {
                if (selectedClient.equals(p.getNom() + " " + p.getPrenom())) {
                    registerPatient updateClient = new registerPatient(p, this);
                    updateClient.setVisible(true);
                    this.setVisible(false); // Cache la fenêtre consulterClient
                    System.out.println(p);
                }
            }
        } catch (Exception e) {
            System.out.println("Error sur le lancement de la view update client : " + e.getMessage());
        }
    }

    // delete client
    private void deleteClient() {
        int selectedRow = tablePatient.getSelectedRow();

        if (selectedRow >= 0 && selectedValue != null && !selectedValue.equals("Choisir un patient")) {
            int confirmation = JOptionPane.showConfirmDialog(this,
                    "Êtes-vous sûr de vouloir supprimer ce patient?",
                    "Confirmation",
                    JOptionPane.YES_NO_OPTION);

            if (confirmation == JOptionPane.YES_OPTION) {
                Patient patientToRemove = null;
                for (Patient p : Patient.getPatients()) {
                    if (selectedValue.equals(p.getNom() + " " + p.getPrenom())) {
                        patientToRemove = p;
                        break;
                    }
                }

                if (patientToRemove != null) {
                    try {
                        // Supprimer de la BDD
                        boolean deleted = patientDAO.delete(patientToRemove.getId_Patient());

                        if (deleted) {
                            // Supprimer de la liste statique
                            Patient.getPatients().remove(patientToRemove);

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
                    } catch (SaisieException e) {
                        throw new RuntimeException(e);
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

    private void retour() {
        if (previousFrame != null) {
            previousFrame.setVisible(true); // réaffiche la fenêtre précédente
        }
        this.dispose(); // ferme la fenêtre actuelle
    }

    private void quitter() {
        int reponse = JOptionPane.showConfirmDialog(consulterPatient.this, "Voulez-vous quitter l'application ?", "Quitter", JOptionPane.YES_NO_OPTION);
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
        panel6.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel5.add(panel6, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(null, "Consulte client", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel7.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tablePatient = new JTable();
        scrollPane1.setViewportView(tablePatient);
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel8.add(panel9, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        créerUnCompteButton = new JButton();
        créerUnCompteButton.setText("Créer un compte");
        panel9.add(créerUnCompteButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
        panel12.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel12, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel14, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel15, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        retourButton = new JButton();
        retourButton.setIcon(new ImageIcon(getClass().getResource("/image/previous.png")));
        retourButton.setText("Retour");
        panel15.add(retourButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        quitterButton = new JButton();
        quitterButton.setIcon(new ImageIcon(getClass().getResource("/image/switch.png")));
        quitterButton.setText("Quitter");
        panel15.add(quitterButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel16, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel17, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(panel18, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(panel19, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel17.add(panel20, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Client");
        panel20.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxClient = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        comboBoxClient.setModel(defaultComboBoxModel1);
        panel20.add(comboBoxClient, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
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
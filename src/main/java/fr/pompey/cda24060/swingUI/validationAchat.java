package fr.pompey.cda24060.swingUI;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import fr.pompey.cda24060.DAO.MedecinDAO;
import fr.pompey.cda24060.DAO.PatientDAO;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class validationAchat extends JFrame {
    private JPanel contentPane;
    private JButton btnRetourAchat;
    private JButton btnValiderAchat;
    private JButton buttonQuitterAchat;
    private JLabel titreValideAchat;
    private JTextField inputQuantiteMedic;
    private JButton btnAjouterMedicamentList;
    private JButton btnDelete;
    private JTable tableMedic;
    private JTable tableMedicDispo;
    private JLabel titreTypeLabel;
    private JComboBox<String> comboBoxPatient;
    private JComboBox<String> comboBoxMedicament;
    private JComboBox<String> comboBoxMedecin;
    private JTextField textFieldPrixTotalPayer;
    private JCheckBox checkBoxMutuelle;
    private JComboBox<String> comboBoxMutuelle;
    private JLabel labelPrixTotal;
    private JLabel labelDeductionMutuelle;
    private JLabel labelPrixAPayer;
    private JLabel labelTauxMutuelle;
    private JFrame previousFrame;
    private DefaultTableModel tableModelCommande;
    private DefaultTableModel tableModelMedicDispo;
    private List<Stock_Medicament> medicamentsCommande = new ArrayList<>();
    private List<Integer> quantitesMedicaments = new ArrayList<>();

    public validationAchat(String typeAchat, JFrame previousFrame) {
        this.previousFrame = previousFrame;

        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/image/miniLogo.png")));
        Dimension dimension = new Dimension(1600, 1000);
        this.setTitle("Sparadrap");
        this.setIconImage(imageIcon.getImage());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.setPreferredSize(dimension);
        this.setResizable(false);
        this.setContentPane(contentPane);
        initializeNewComponents();
        // Afficher le type d'achat
        if (typeAchat.equalsIgnoreCase("direct")) {
            titreTypeLabel.setText("DIRECT");
            setMutuelleComponentsVisible(false);
        } else if (typeAchat.equalsIgnoreCase("ordonnance")) {
            titreTypeLabel.setText("ORDONNANCE");
            setMutuelleComponentsVisible(true);
            checkBoxMutuelle.setSelected(true); // auto-activer mutuelle pour ordonnance
        }
        // Tableaux pour les medicaments disponibles
        String[] colonne = {"Quantité", "Date mise en service", "Prix", "Categorie", "Nom"};
        tableModelMedicDispo = new DefaultTableModel(colonne, 0);
        tableMedicDispo.setModel(tableModelMedicDispo);
        // Tableaux pour passer la commande des médicaments
        String[] colonnes = {"Nom medicament", "Quantite", "Prix unitaire", "Prix total"};
        tableModelCommande = new DefaultTableModel(colonnes, 0);
        tableMedic.setModel(tableModelCommande);
        afficherListeMedicDispo();
        remplirComboBoxMedecin();
        remplirComboBoxClient();
        remplirComboBoxMedicament();
        remplirComboBoxMutuelle();
        this.pack();
        this.setLocationRelativeTo(null);
        btnValiderAchat.setEnabled(false);
        // Gestionnaire pour la croix (X)
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                retour();
            }
        });
        // Listeners
        btnRetourAchat.addActionListener(e -> retour());
        btnValiderAchat.addActionListener(e -> {
            try {
                valider();
            } catch (SaisieException ex) {
                throw new RuntimeException(ex);
            }
        });
        buttonQuitterAchat.addActionListener(e -> quitter());
        btnAjouterMedicamentList.addActionListener(e -> {
            try {
                ajouterMedicamentAuPanier();
            } catch (SaisieException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erreur lors de l'ajout : " + ex.getMessage(),
                        "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        btnDelete.addActionListener(e -> supprimerMedicamentDuPanier());
        // Mutuelle
        checkBoxMutuelle.addActionListener(e -> mettreAJourAffichagePrix());
        comboBoxMutuelle.addActionListener(e -> mettreAJourAffichagePrix());
        // Met à jour le prix initial
        mettreAJourAffichagePrix();
    }

    private void initializeNewComponents() {
        checkBoxMutuelle = new JCheckBox("Prise en charge mutuelle");
        checkBoxMutuelle.setSelected(false);
        comboBoxMutuelle = new JComboBox<>();
        comboBoxMutuelle.setEnabled(false);
        labelPrixTotal = new JLabel("Prix total : 0,00€");
        labelDeductionMutuelle = new JLabel("Déduction mutuelle : 0,00€");
        labelPrixAPayer = new JLabel("Prix à payer : 0,00€");
        labelTauxMutuelle = new JLabel("Taux : 0%");
    }

    private void setMutuelleComponentsVisible(boolean visible) {
        if (checkBoxMutuelle != null) checkBoxMutuelle.setVisible(visible);
        if (comboBoxMutuelle != null) comboBoxMutuelle.setVisible(visible);
        if (labelDeductionMutuelle != null) labelDeductionMutuelle.setVisible(visible);
        if (labelTauxMutuelle != null) labelTauxMutuelle.setVisible(visible);
    }

    private void remplirComboBoxMutuelle() {
        comboBoxMutuelle.removeAllItems();
        comboBoxMutuelle.addItem("-- Sélectionner une mutuelle --");
        for (Mutuelle mutuelle : Mutuelle.getMutuelles()) {
            String item = mutuelle.getNom() + " (" + String.format("%.1f", mutuelle.getTauxPriseEnCharge()) + "%)";
            comboBoxMutuelle.addItem(item);
        }
    }

    private void mettreAJourAffichagePrix() {
        double prixTotal = calculerPrixTotalPanier();
        double deduction = 0.0;
        double prixAPayer = prixTotal;
        String tauxText = "Taux : 0%";
        // Réduction mutuelle 30% si ordonnance
        if ("ORDONNANCE".equalsIgnoreCase(titreTypeLabel.getText()) && checkBoxMutuelle.isSelected()) {
            deduction = prixTotal * 0.30;
            prixAPayer = prixTotal - deduction;
            tauxText = "Taux : 30%";
        }
        labelPrixTotal.setText(String.format("Prix total : %.2f€", prixTotal));
        labelDeductionMutuelle.setText(String.format("Déduction mutuelle : %.2f€", deduction));
        labelPrixAPayer.setText(String.format("Prix à payer : %.2f€", prixAPayer));
        labelTauxMutuelle.setText(tauxText);
        textFieldPrixTotalPayer.setText(String.format("%.2f", prixAPayer));
    }

    private double calculerPrixTotalPanier() {
        double total = 0.0;
        for (int i = 0; i < medicamentsCommande.size(); i++) {
            total += medicamentsCommande.get(i).getMedicPrixUnitaire() * quantitesMedicaments.get(i);
        }
        return total;
    }

    private void remplirComboBoxMedecin() {
        comboBoxMedecin.removeAllItems();
        for (Medecin medecin : Medecin.getMedecins()) {
            comboBoxMedecin.addItem(medecin.getNom() + " " + medecin.getPrenom());
        }
    }

    private void remplirComboBoxClient() {
        comboBoxPatient.removeAllItems();
        for (Patient patient : Patient.getPatients()) {
            comboBoxPatient.addItem(patient.getNom() + " " + patient.getPrenom());
        }
    }

    private void remplirComboBoxMedicament() {
        comboBoxMedicament.removeAllItems();
        for (Stock_Medicament medicament : Stock_Medicament.getMedicaments()) {
            comboBoxMedicament.addItem(medicament.getMedicNom());
        }
    }

    private void ajouterMedicamentAuPanier() throws SaisieException {
        String nomMedic = comboBoxMedicament.getSelectedItem().toString().trim();
        int quantite;
        try {
            quantite = Integer.parseInt(inputQuantiteMedic.getText().trim());
            if (quantite <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "La quantité doit être un nombre entier positif", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Stock_Medicament medicamentTrouve = null;
        for (Stock_Medicament medicament : Stock_Medicament.getMedicaments()) {
            if (medicament.getMedicNom().equalsIgnoreCase(nomMedic)) {
                medicamentTrouve = medicament;
                break;
            }
        }

        if (medicamentTrouve == null) {
            JOptionPane.showMessageDialog(this, "Médicament introuvable", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier si la quantité demandée est disponible
        if (medicamentTrouve.getQuantite() < quantite) {
            JOptionPane.showMessageDialog(this,
                    "Quantité insuffisante en stock !\n" +
                            "Quantité disponible : " + medicamentTrouve.getQuantite(),
                    "Erreur de stock", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Vérifier si le médicament est déjà dans le panier
        boolean dejaPresent = false;
        for (int i = 0; i < medicamentsCommande.size(); i++) {
            if (medicamentsCommande.get(i).getMedicNom().equalsIgnoreCase(nomMedic)) {
                // Mettre à jour la quantité
                int nouvelleQuantite = quantitesMedicaments.get(i) + quantite;
                // Vérifier si la nouvelle quantité est disponible
                if (medicamentTrouve.getQuantite() < nouvelleQuantite) {
                    JOptionPane.showMessageDialog(this,
                            "Quantité totale insuffisante en stock !\n" +
                                    "Quantité disponible : " + medicamentTrouve.getQuantite(),
                            "Erreur de stock", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                quantitesMedicaments.set(i, nouvelleQuantite);
                dejaPresent = true;
                break;
            }
        }

        // Si pas déjà présent, ajouter le médicament
        if (!dejaPresent) {
            Stock_Medicament medicamentCommande = new Stock_Medicament(
                    0,
                    medicamentTrouve.getDateMiseEnService(),
                    medicamentTrouve.getMedicPrixUnitaire(),
                    medicamentTrouve.getMedicNom(),
                    medicamentTrouve.getMedicDateEntreeStock()
            );
            medicamentsCommande.add(medicamentCommande);
            quantitesMedicaments.add(quantite);
        }

        rafraichirTableauPanier();
        inputQuantiteMedic.setText("");
        btnValiderAchat.setEnabled(!medicamentsCommande.isEmpty());
        mettreAJourAffichagePrix();
        JOptionPane.showMessageDialog(this, "Médicament ajouté au panier !", "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    private void valider() throws SaisieException {
        if (medicamentsCommande.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucun médicament dans le panier", "Panier vide", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (comboBoxMedecin.getSelectedItem() == null || comboBoxPatient.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médecin et un patient", "Information manquante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérifier une dernière fois les stocks avant validation
        for (int i = 0; i < medicamentsCommande.size(); i++) {
            Stock_Medicament medCommande = medicamentsCommande.get(i);
            int quantiteCommande = quantitesMedicaments.get(i);
            Stock_Medicament medStock = null;
            for (Stock_Medicament medicament : Stock_Medicament.getMedicaments()) {
                if (medicament.getMedicNom().equalsIgnoreCase(medCommande.getMedicNom())) {
                    medStock = medicament;
                    break;
                }
            }
            if (medStock == null || medStock.getQuantite() < quantiteCommande) {
                JOptionPane.showMessageDialog(this,
                        "Quantité insuffisante pour " + medCommande.getMedicNom() + "\n" +
                                "Quantité disponible : " + (medStock != null ? medStock.getQuantite() : 0),
                        "Erreur de stock", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // Décrémenter les stocks
        for (int i = 0; i < medicamentsCommande.size(); i++) {
            Stock_Medicament medCommande = medicamentsCommande.get(i);
            int quantiteCommande = quantitesMedicaments.get(i);
            for (Stock_Medicament medicament : Stock_Medicament.getMedicaments()) {
                if (medicament.getMedicNom().equalsIgnoreCase(medCommande.getMedicNom())) {
                    medicament.setMedicQuantite(medicament.getQuantite() - quantiteCommande);
                    break;
                }
            }
        }

        // Calcul du prix total et création de la commande
        double prixTotal = calculerPrixTotalPanier();
        double deduction = 0.0;
        double prixAPayer = prixTotal;
        boolean priseEnCharge = false;
        if ("ORDONNANCE".equalsIgnoreCase(titreTypeLabel.getText()) && checkBoxMutuelle.isSelected()) {
            deduction = prixTotal * 0.30;
            prixAPayer = prixTotal - deduction;
            priseEnCharge = true;
        }

        List<Stock_Medicament> medicamentsAvecQuantites = new ArrayList<>();
        for (int i = 0; i < medicamentsCommande.size(); i++) {
            Stock_Medicament medOriginal = medicamentsCommande.get(i);
            int quantite = quantitesMedicaments.get(i);
            Stock_Medicament medAvecQte = new Stock_Medicament(
                    quantite,
                    medOriginal.getDateMiseEnService(),
                    medOriginal.getMedicPrixUnitaire(),
                    medOriginal.getMedicNom(),
                    medOriginal.getMedicDateEntreeStock()
            );
            medicamentsAvecQuantites.add(medAvecQte);
        }

        Commande.TypeAchat typeAchat = "DIRECT".equalsIgnoreCase(titreTypeLabel.getText())
                ? Commande.TypeAchat.DIRECT
                : Commande.TypeAchat.ORDONNANCE;
        int quantiteTotale = quantitesMedicaments.stream().mapToInt(Integer::intValue).sum();
        Commande commande = new Commande(
                new Date(System.currentTimeMillis()),
                typeAchat,
                comboBoxMedecin.getSelectedItem().toString(),
                comboBoxPatient.getSelectedItem().toString(),
                medicamentsAvecQuantites,
                quantiteTotale,
                prixTotal,
                null,
                priseEnCharge
        );

        String message = "Commande validée !\n" +
                "Prix total : " + String.format("%.2f€", prixTotal) + "\n";
        if (priseEnCharge) {
            message += "Prise en charge mutuelle : 30%\n";
        }
        message += "Prix à payer : " + String.format("%.2f€", prixAPayer);
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
        Menu menu = new Menu();
        menu.setVisible(true);
        this.dispose();
    }

    private void rafraichirTableauPanier() {
        tableModelCommande.setRowCount(0);
        for (int i = 0; i < medicamentsCommande.size(); i++) {
            Stock_Medicament med = medicamentsCommande.get(i);
            int qty = quantitesMedicaments.get(i);
            double prixTotal = med.getMedicPrixUnitaire() * qty;
            tableModelCommande.addRow(new Object[]{
                    med.getMedicNom(),
                    qty,
                    String.format("%.2f€", med.getMedicPrixUnitaire()),
                    String.format("%.2f€", prixTotal)
            });
        }
    }

    private void afficherListeMedicDispo() {
        tableModelMedicDispo.setRowCount(0);
        if (Stock_Medicament.getMedicaments().isEmpty()) {
            tableModelMedicDispo.addRow(new Object[]{"-", "Aucun medicament", "", "", ""});
        } else {
            for (Stock_Medicament medicaments : Stock_Medicament.getMedicaments()) {
                tableModelMedicDispo.addRow(new Object[]{
                        medicaments.getQuantite(),
                        medicaments.getDateMiseEnService(),
                        String.format("%.2f€", medicaments.getMedicPrixUnitaire()),
                        medicaments.getMedicNom(),
                });
            }
        }
    }

    private void supprimerMedicamentDuPanier() {
        int selectedRow = tableMedic.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < medicamentsCommande.size()) {
            medicamentsCommande.remove(selectedRow);
            quantitesMedicaments.remove(selectedRow);
            rafraichirTableauPanier();
            btnValiderAchat.setEnabled(!medicamentsCommande.isEmpty());
            mettreAJourAffichagePrix();
            JOptionPane.showMessageDialog(this, "Médicament retiré du panier", "Information", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un médicament à supprimer", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void retour() {
        int reponse = JOptionPane.showConfirmDialog(this, "Des médicaments sont dans le panier. Voulez-vous vraiment annuler ?", "Confirmation", JOptionPane.YES_NO_OPTION);
        if (reponse == JOptionPane.YES_OPTION) {
            if (previousFrame != null) {
                previousFrame.setVisible(true);
            }
            this.dispose();
        }
    }

    private void quitter() {
        int reponse = JOptionPane.showConfirmDialog(this, "Voulez-vous quitter l'application ?", "Quitter", JOptionPane.YES_NO_OPTION);
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
        contentPane.setLayout(new GridLayoutManager(7, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        titreValideAchat = new JLabel();
        Font titreValideAchatFont = this.$$$getFont$$$("Cooper Black", -1, 28, titreValideAchat.getFont());
        if (titreValideAchatFont != null) titreValideAchat.setFont(titreValideAchatFont);
        titreValideAchat.setText("Pharmacie Sparadrap");
        panel2.add(titreValideAchat, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setIcon(new ImageIcon(getClass().getResource("/image/pharmacy.png")));
        label1.setText("");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel4, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel5.add(scrollPane1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane1.setBorder(BorderFactory.createTitledBorder(null, "Préparation commande", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tableMedic = new JTable();
        scrollPane1.setViewportView(tableMedic);
        final JScrollPane scrollPane2 = new JScrollPane();
        panel5.add(scrollPane2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        scrollPane2.setBorder(BorderFactory.createTitledBorder(null, "Medicament disponible", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, null));
        tableMedicDispo = new JTable();
        scrollPane2.setViewportView(tableMedicDispo);
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel6, new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel8 = new JPanel();
        panel8.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel8, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel9 = new JPanel();
        panel9.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel6.add(panel9, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setIcon(new ImageIcon(getClass().getResource("/image/right.png")));
        label2.setText("Sparadrap 2025");
        panel9.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel10 = new JPanel();
        panel10.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel10, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel11 = new JPanel();
        panel11.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel11, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel12 = new JPanel();
        panel12.setLayout(new GridLayoutManager(1, 2, new Insets(0, 10, 0, 0), -1, -1));
        panel11.add(panel12, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel13 = new JPanel();
        panel13.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel13, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Nom du medecin");
        panel13.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxMedecin = new JComboBox();
        panel13.add(comboBoxMedecin, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel14 = new JPanel();
        panel14.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel12.add(panel14, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Nom du patient");
        panel14.add(label4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboBoxPatient = new JComboBox();
        panel14.add(comboBoxPatient, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel15 = new JPanel();
        panel15.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel10.add(panel15, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel16 = new JPanel();
        panel16.setLayout(new GridLayoutManager(2, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel15.add(panel16, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Saisir le nom du médicament");
        panel16.add(label5, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Saisir la quantité");
        panel16.add(label6, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        inputQuantiteMedic = new JTextField();
        panel16.add(inputQuantiteMedic, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnAjouterMedicamentList = new JButton();
        btnAjouterMedicamentList.setIcon(new ImageIcon(getClass().getResource("/image/carre.png")));
        btnAjouterMedicamentList.setText("");
        panel16.add(btnAjouterMedicamentList, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel17 = new JPanel();
        panel17.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel17, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        comboBoxMedicament = new JComboBox();
        panel16.add(comboBoxMedicament, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel18 = new JPanel();
        panel18.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel16.add(panel18, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel19 = new JPanel();
        panel19.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel19, new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel20 = new JPanel();
        panel20.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel20, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel21 = new JPanel();
        panel21.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel20.add(panel21, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel22 = new JPanel();
        panel22.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel20.add(panel22, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel23 = new JPanel();
        panel23.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel20.add(panel23, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel24 = new JPanel();
        panel24.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel24, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnRetourAchat = new JButton();
        btnRetourAchat.setIcon(new ImageIcon(getClass().getResource("/image/previous.png")));
        btnRetourAchat.setText("Retour");
        panel24.add(btnRetourAchat, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel25 = new JPanel();
        panel25.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel25, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnValiderAchat = new JButton();
        btnValiderAchat.setIcon(new ImageIcon(getClass().getResource("/image/accept.png")));
        btnValiderAchat.setText("Valider");
        panel25.add(btnValiderAchat, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel26 = new JPanel();
        panel26.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel23.add(panel26, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        btnDelete = new JButton();
        btnDelete.setIcon(new ImageIcon(getClass().getResource("/image/delete.png")));
        btnDelete.setText("Supprimer");
        panel26.add(btnDelete, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel27 = new JPanel();
        panel27.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel19.add(panel27, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        buttonQuitterAchat = new JButton();
        buttonQuitterAchat.setIcon(new ImageIcon(getClass().getResource("/image/switch.png")));
        buttonQuitterAchat.setText("Quitter");
        panel27.add(buttonQuitterAchat, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel28 = new JPanel();
        panel28.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel28, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        titreTypeLabel = new JLabel();
        Font titreTypeLabelFont = this.$$$getFont$$$(null, -1, 24, titreTypeLabel.getFont());
        if (titreTypeLabelFont != null) titreTypeLabel.setFont(titreTypeLabelFont);
        titreTypeLabel.setText("");
        panel28.add(titreTypeLabel, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel29 = new JPanel();
        panel29.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        contentPane.add(panel29, new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel30 = new JPanel();
        panel30.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel29.add(panel30, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel31 = new JPanel();
        panel31.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel29.add(panel31, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel32 = new JPanel();
        panel32.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel29.add(panel32, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel33 = new JPanel();
        panel33.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel32.add(panel33, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label7 = new JLabel();
        label7.setText("Montant total à payer :");
        panel33.add(label7, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel34 = new JPanel();
        panel34.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel32.add(panel34, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel35 = new JPanel();
        panel35.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel34.add(panel35, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel36 = new JPanel();
        panel36.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel34.add(panel36, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        textFieldPrixTotalPayer = new JTextField();
        panel34.add(textFieldPrixTotalPayer, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
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

package fr.pompey.cda24060.controller;

import fr.pompey.cda24060.model.*;
import fr.pompey.cda24060.swingUI.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            logger.error("Exception non gérée dans le thread {} : {}", thread.getName(), throwable.getMessage(), throwable);
        });

        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());


            SwingUtilities.invokeLater(() -> {
                try {
                    Menu myMenu = new Menu();
                    myMenu.setVisible(true);
                    logger.info("L'application Swing Sparadrap a démarré avec succès.");
                } catch (Exception e) {
                    logger.error("Erreur lors de l'affichage de la vue Swing : {}", e.getMessage(), e);
                }
            });

        } catch (Exception e) {
            logger.error("Erreur au démarrage de l'application : {}", e.getMessage(), e);
        }
    }

}
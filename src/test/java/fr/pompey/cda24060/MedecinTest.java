package fr.pompey.cda24060;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Medecin;

import static org.junit.jupiter.api.Assertions.*;

class MedecinTest {

    private Medecin medecin;
    private Lieu lieu;

    @BeforeEach
    void setUp() throws SaisieException {
        // Création d'un lieu valide pour le médecin
        lieu = new Lieu(
                "10 rue de Paris",
                "testmedecin@example.com",
                "+33123456789",
                "Paris",
                75001
        );

        // Création d'un médecin valide
        medecin = new Medecin("Dupont", "Jean", "12345678901", lieu);
    }

    @Test
    void testGetMedecins() {
        // La liste statique doit être accessible et contenir au moins le médecin créé
        assertNotNull(Medecin.getMedecins());
        assertTrue(Medecin.getMedecins().contains(medecin));
    }

    @Test
    void testGetNumeroAgreement() {
        assertEquals("12345678901", medecin.getMedNumeroAgreement());
    }

    @Test
    void testSetNumeroAgreement() throws SaisieException {
        medecin.setNumeroAgreement("98765432100");
        assertEquals("98765432100", medecin.getMedNumeroAgreement());
    }

    @Test
    void testSetNumeroAgreementInvalide() {
        Exception exception = assertThrows(SaisieException.class, () -> {
            medecin.setNumeroAgreement("ABC!"); // numéro invalide
        });
        assertTrue(exception.getMessage().contains("Erreur sur numéro d'agrément"));
    }

    @Test
    void testToString() {
        String str = medecin.toString();
        assertTrue(str.contains("Dupont"));
        assertTrue(str.contains("Jean"));
        assertTrue(str.contains("12345678901"));
        assertTrue(str.contains("Paris")); // contenu du lieu
    }
}

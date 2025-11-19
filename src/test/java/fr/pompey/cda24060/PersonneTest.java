package fr.pompey.cda24060;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;
import fr.pompey.cda24060.model.Mutuelle;
import fr.pompey.cda24060.model.Personne;

import static org.junit.jupiter.api.Assertions.*;

class PersonneTest {

    private Personne personne;
    private Lieu lieu;
    private Mutuelle mutuelle;

    @BeforeEach
    void setUp() throws SaisieException {
        // Création d'un Lieu valide
        lieu = new Lieu(
                "10 rue de Paris",
                "test@example.com",
                "+33123456789",
                "Paris",
                75001
        );

        // Création d'une Mutuelle valide
        mutuelle = new Mutuelle(
                "MutuelleTest",
                30.0,
                75,
                lieu
        );

        // Création d'une Personne avec le constructeur 3 paramètres
        personne = new Personne("Dupont", "Jean", lieu);
        personne.setMutuelle(mutuelle); // association d'une mutuelle
    }

    @Test
    void testGetNom() throws SaisieException {
        assertEquals("Dupont", personne.getNom());
    }

    @Test
    void testSetNom() throws SaisieException {
        personne.setNom("Martin");
        assertEquals("Martin", personne.getNom());
    }

    @Test
    void testGetPrenom() throws SaisieException {
        assertEquals("Jean", personne.getPrenom());
    }

    @Test
    void testSetPrenom() throws SaisieException {
        personne.setPrenom("Pierre");
        assertEquals("Pierre", personne.getPrenom());
    }

    @Test
    void testGetLieu() {
        assertEquals(lieu, personne.getLieu());
    }

    @Test
    void testSetLieu() throws SaisieException {
        Lieu nouveauLieu = new Lieu(
                "20 avenue Lyon",
                "lyon@example.com",
                "+33987654321",
                "Lyon",
                69001
        );
        personne.setLieu(nouveauLieu);
        assertEquals(nouveauLieu, personne.getLieu());
    }

    @Test
    void testGetMutuelle() {
        assertEquals(mutuelle, personne.getMutuelle());
    }

    @Test
    void testSetMutuelle() throws SaisieException {
        Mutuelle nouvelleMutuelle = new Mutuelle(
                "MutuelleTest",
                50.0,
                69,
                lieu
        );
        personne.setMutuelle(nouvelleMutuelle);
        assertEquals(nouvelleMutuelle, personne.getMutuelle());
    }

    @Test
    void testNomInvalide() {
        Exception exception = Assertions.<SaisieException>assertThrows(SaisieException.class, () -> {
            personne.setNom("Dupont123"); // nom invalide
        });
        assertTrue(exception.getMessage().contains("Le nom n'est pas valide"));
    }

    @Test
    void testPrenomInvalide() {
        Exception exception = Assertions.<SaisieException>assertThrows(SaisieException.class, () -> {
            personne.setPrenom("Jean!"); // prénom invalide
        });
        assertTrue(exception.getMessage().contains("Le prenom n'est pas valide"));
    }

    @Test
    void testToString() {
        String str = personne.toString();
        assertTrue(str.contains("Dupont"));
        assertTrue(str.contains("Jean"));
        assertTrue(str.contains("Paris")); // contenu du lieu
        assertTrue(str.contains("MutuelleTest")); // contenu de la mutuelle
    }
}

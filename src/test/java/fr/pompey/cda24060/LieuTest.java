package fr.pompey.cda24060;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Lieu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LieuTest {

    Lieu lieu;

    @BeforeEach
    void setUp() throws SaisieException {
        lieu = new Lieu("2 rue de la paix", "test@test.fr", "+33383819000", "pam", 54700);
    }

    @AfterEach
    void tearDown() {
        lieu = null;
    }

    @Test
    public void testConstructeurLieuArgsValide() throws SaisieException {
        Lieu testLieu = new Lieu("2 rue de la paix", "test@test.fr",  "+33383819000", "pam", 54700);
        assertEquals("2 rue de la paix", testLieu.getAdresse());
        assertEquals("test@test.fr", testLieu.getEmail());
        assertEquals("+33383819000", testLieu.getTelephone());
        assertEquals("pam", testLieu.getVille());
        assertEquals(54700, testLieu.getCodePostal());
    }

    @Test
    void getAdresse() {
        assertEquals("2 rue de la paix", lieu.getAdresse());
    }

    @Test
    void setAdresseValide() throws SaisieException {
        lieu.setAdresse("15 Avenue Victor Hugo");
        assertEquals("15 Avenue Victor Hugo", lieu.getAdresse());
    }

    @Test
    void setAdresseInvalide() {
        assertThrows(SaisieException.class, () -> {
            lieu.setAdresse("rue de la paix"); // manque le numéro de la rue
        });
    }

    @Test
    void getEmail() {
        assertEquals("test@test.fr", lieu.getEmail());
    }

    @Test
    void setEmailValide() throws SaisieException {
        lieu.setEmail("new@test.fr");
        assertEquals("new@test.fr", lieu.getEmail());
    }

    @Test
    void setEmailInvalide() {
        assertThrows(SaisieException.class, () -> {
            lieu.setEmail("emailInvalide"); // email invalide
        });
    }

    @Test
    void getTelephone() {
        assertEquals("+33383819000", lieu.getTelephone());
    }

    @Test
    void setTelephoneValide() throws SaisieException {
        lieu.setTelephone("+33612345678");
        assertEquals("+33612345678", lieu.getTelephone());
    }

    @Test
    void setTelephoneInvalide() {
        assertThrows(SaisieException.class, () -> {
            lieu.setTelephone("123ABC"); // pas un format correct (ex: +33612345678)
        });
    }

    @Test
    void getVille() {
        assertEquals("pam", lieu.getVille());
    }

    @Test
    void setVille() throws SaisieException {
        lieu.setVille("Metz");
        assertEquals("Metz", lieu.getVille());
    }

    @Test
    void getCodePostal() {
        assertEquals(54700, lieu.getCodePostal());
    }

    @Test
    void setCodePostalValide() throws SaisieException {
        lieu.setCodePostal(57000);
        assertEquals(57000, lieu.getCodePostal());
    }

    @Test
    void setCodePostalInvalide() {
        assertThrows(SaisieException.class, () -> {
            lieu.setCodePostal(123); // code postal trop court besoin de 5 chiffres
        });
    }
}
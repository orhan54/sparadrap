package fr.pompey.cda24060.model;

import fr.pompey.cda24060.exception.SaisieException;
import static fr.pompey.cda24060.utility.RegexUtility.*;

/**
 * The type Lieu.
 */
public class Lieu {

    // attributs
    private int Id_Lieu, codePostal;
    private String adresse, email, telephone, ville;

    /**
     * Instantiates a new Lieu.
     *
     * @param pAdresse          the adresse
     * @param pEmail            the email
     * @param pTelephone        the telephone
     * @param pVille            the ville
     * @param pCodePostal       the code postal
     * @throws SaisieException  the saisie exception
     */
    // constructeur de la classe Lieu
    public Lieu(String pAdresse, String pEmail, String pTelephone, String pVille, int pCodePostal) throws SaisieException {
        this.setAdresse(pAdresse);
        this.setEmail(pEmail);
        this.setTelephone(pTelephone);
        this.setVille(pVille);
        this.setCodePostal(pCodePostal);
    }

    // Getters & Setters

    /**
     * Gets id lieu.
     *
     * @return the id lieu
     */
    public int getId_Lieu() {
        return Id_Lieu;
    }

    /**
     * Sets id lieu.
     *
     * @param id_Lieu the id lieu
     */
    public void setId_Lieu(int id_Lieu) {
        Id_Lieu = id_Lieu;
    }

    /**
     * Gets adresse.
     *
     * @return the adresse
     */
    public String getAdresse() {
        return this.adresse;
    }

    /**
     * Sets adresse.
     *
     * @throws SaisieException the saisie exception
     */
    public void setAdresse(String pAdresse) throws SaisieException {
        if (!validateAdresse(pAdresse)) {
            throw new SaisieException("Erreur adresse invalide : " + pAdresse);
        }
        this.adresse = pAdresse;
    }

    /**
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return this.email;
    }

    /**
     * Sets email.
     *
     * @param pEmail            the email
     * @throws SaisieException  the saisie exception
     */
    public void setEmail(String pEmail) throws SaisieException {
        if (!validate(pEmail)) {
            throw new SaisieException("Erreur sur adresse email : " + pEmail);
        }
        this.email = pEmail;
    }

    /**
     * Gets telephone.
     *
     * @return the telephone
     */
    public String getTelephone() {
        return this.telephone;
    }

    /**
     * Sets telephone.
     *
     * @param pTelephone        the telephone
     * @throws SaisieException  the saisie exception
     */
    public void setTelephone(String pTelephone) throws SaisieException {
        if (!validatePhone(pTelephone)) {
            throw new SaisieException("Erreur sur téléphone : " + pTelephone);
        }
        this.telephone = pTelephone;
    }

    /**
     * Gets ville.
     *
     * @return the ville
     */
    public String getVille() {
        return this.ville;
    }

    /**
     * Sets ville.
     *
     * @param pVille            the ville
     * @throws SaisieException  the saisie exception
     */
    public void setVille(String pVille) throws SaisieException {
        if (!regexAlpha(pVille) || pVille.isEmpty()) {
            throw new SaisieException("Erreur sur le nom de la ville : " + pVille);
        }
        this.ville = pVille;
    }

    /**
     * Gets code postal.
     *
     * @return the code postal
     */
    public int getCodePostal() {
        return this.codePostal;
    }

    /**
     * Sets code postal.
     *
     * @param pCodePostal       the code postal
     * @throws SaisieException  the saisie exception
     */
    public void setCodePostal(int pCodePostal) throws SaisieException {
        String codeStr = String.valueOf(pCodePostal);
        if (!positifInt(codeStr) || codeStr.length() != 5) {
            throw new SaisieException("Erreur sur code postal : " + pCodePostal);
        }
        this.codePostal = pCodePostal;
    }

    // StringBuilder pour afficher le toString de Lieu
    @Override
    public String toString() {
        StringBuilder sbl = new StringBuilder();
        sbl.append("- Adresse: ").append(this.adresse).append("\n");
        sbl.append("- Email: ").append(this.email).append("\n");
        sbl.append("- Téléphone: ").append(this.telephone).append("\n");
        sbl.append("- Ville: ").append(this.ville).append("\n");
        sbl.append("- Code Postal: ").append(this.codePostal).append("\n");
        return sbl.toString();
    }
}
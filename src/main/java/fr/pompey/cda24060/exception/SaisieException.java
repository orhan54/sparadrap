package fr.pompey.cda24060.exception;

public class SaisieException extends Exception {

    public SaisieException() {
        super("Erreur Saisie");
    }

    public SaisieException(String message) {
        super(message);
    }
}

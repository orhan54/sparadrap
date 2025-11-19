package fr.pompey.cda24060.DAO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique CRUD pour les DAO
 * @param <T> Type de l'entité
 */
public interface InterfaceDAO<T> {

    /**
     * Créer une nouvelle entité dans la base de données
     * @param obj L'objet à créer
     * @return L'objet créé avec son ID généré
     * @throws SQLException
     */
    T create(T obj) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Récupérer une entité par son ID
     * @param id L'identifiant de l'entité
     * @return L'entité trouvée ou null
     * @throws SQLException
     */
    T getById(int id) throws SQLException;

    /**
     * Récupérer toutes les entités
     * @return Liste de toutes les entités
     * @throws SQLException
     */
    List<T> getAll() throws SQLException;

    /**
     * Mettre à jour une entité existante
     * @param obj L'objet à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException
     */
    boolean update(T obj) throws SQLException;

    /**
     * Supprimer une entité par son ID
     * @param id L'identifiant de l'entité à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException
     */
    boolean delete(int id) throws SQLException;
}
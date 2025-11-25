package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.dataBase.Singleton;
import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Pharmacie;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Interface générique CRUD pour les DAO
 * @param <T> Type de l'entité
 */
public abstract class InterfaceDAO<T> {

    /**
     * The constant connection.
     */
    public static final Connection connection;

    static {
        try {
            connection = Singleton.getInstanceDB();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Créer une nouvelle entité dans la base de données
     * @param obj L'objet à créer
     * @return L'objet créé avec son ID généré
     * @throws SQLException
     */
    public abstract T create(T obj) throws SQLException, IOException, ClassNotFoundException;

    /**
     * Récupérer une entité par son ID
     * @param id L'identifiant de l'entité
     * @return L'entité trouvée ou null
     * @throws SQLException
     */
    public abstract T getById(int id) throws SQLException, SaisieException;

    /**
     * Récupérer toutes les entités
     * @return Liste de toutes les entités
     * @throws SQLException
     */
    public abstract List<T> getAll() throws SQLException;

    /**
     * Mettre à jour une entité existante
     * @param obj L'objet à mettre à jour
     * @return true si la mise à jour a réussi
     * @throws SQLException
     */
    public abstract boolean update(T obj) throws SQLException;

    /**
     * Supprimer une entité par son ID
     * @param id L'identifiant de l'entité à supprimer
     * @return true si la suppression a réussi
     * @throws SQLException
     */
    public abstract boolean delete(int id) throws SQLException, SaisieException;

    /**
     * Close connection.
     *
     * @throws SQLException the sql exception
     */
    public void closeConnection()throws SQLException{
        connection.close();
    }

}
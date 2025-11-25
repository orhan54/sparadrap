package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Commande;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class CommandeDAO extends InterfaceDAO<Commande> {
    @Override
    public Commande create(Commande obj) throws SQLException, IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public Commande getById(int id) throws SQLException, SaisieException {
        return null;
    }

    @Override
    public List<Commande> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public boolean update(Commande obj) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(int id) throws SQLException, SaisieException {
        return false;
    }
}

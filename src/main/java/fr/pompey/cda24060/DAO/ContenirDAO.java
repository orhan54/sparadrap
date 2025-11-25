package fr.pompey.cda24060.DAO;

import fr.pompey.cda24060.exception.SaisieException;
import fr.pompey.cda24060.model.Contenir;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class ContenirDAO extends InterfaceDAO<Contenir>{
    @Override
    public Contenir create(Contenir obj) throws SQLException, IOException, ClassNotFoundException {
        return null;
    }

    @Override
    public Contenir getById(int id) throws SQLException, SaisieException {
        return null;
    }

    @Override
    public List<Contenir> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public boolean update(Contenir obj) throws SQLException {
        return false;
    }

    @Override
    public boolean delete(int id) throws SQLException, SaisieException {
        return false;
    }
}

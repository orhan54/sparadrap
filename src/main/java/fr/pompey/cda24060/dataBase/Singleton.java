package fr.pompey.cda24060.dataBase;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Singleton {

    private static final Properties props = new Properties();
    private static Connection connection;

    private final String PATHCONF = "conf.properties";

    private Singleton() throws SQLException, IOException, ClassNotFoundException {

        try(InputStream is = Singleton.class.getClassLoader().getResourceAsStream(PATHCONF)) {

            // chargement de properties
            props.load(is);
            // chargement du driver
            Class.forName(props.getProperty("jdbc.driver.class"));
            // Création de la connection
            String  url = props.getProperty("jdbc.url");
            String  login = props.getProperty("jdbc.login");
            String  password = props.getProperty("jdbc.password");
            // création de la connexion
            connection = DriverManager.getConnection(url, login, password);

            // a retirer !!! juste informatif
            System.out.println("Connected to database : " + connection);
        }
    }

    public static Connection getInstanceDB() throws SQLException, IOException, ClassNotFoundException {

        try {
            if (getConnection() == null || getConnection().isClosed()) {

                new Singleton();
                System.out.println("Connected to database : " + getConnection());
            } else {
                System.out.println("Connection already existing");
            }
        } finally {

        }
        return connection;
    }

    public static void closeInstanceDB() throws SQLException {
        try {
            if (getConnection() != null && !getConnection().isClosed()) {
                getConnection().close();
            }
        } finally {

        }
    }

    private static Connection getConnection() {
        return connection;
    }
}
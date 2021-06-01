package ru.tronin.testSpring;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.tronin.testSpring.dao.DAO;
import ru.tronin.testSpring.dao.EngineDAO;
import ru.tronin.testSpring.model.Engine;

public class Application {


    public static void main(String[] args) {
        SessionFactory factory = null;
        try {
            factory = new Configuration().configure().buildSessionFactory();
            DAO<Engine, String> engineDAO = new EngineDAO(factory);
        final Engine engine =   new Engine("Suzuki", 230);
        engineDAO.create(engine);

        } catch (Exception e){
            e.printStackTrace();
        }

    }
}

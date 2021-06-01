package ru.tronin.test.model;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        Configuration configuration = new Configuration().configure();
        configuration.addAnnotatedClass(Country.class);
        configuration.addAnnotatedClass(Region.class);
        configuration.addAnnotatedClass(City.class);
        StandardServiceRegistryBuilder builder =  new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
        SessionFactory sessionFactory =  configuration.buildSessionFactory(builder.build());
        City city = null;

        for (int i = 0; i < 10; i++) {
            Session session = sessionFactory.openSession();
            Transaction transaction = session.beginTransaction();
            city= session.find(City.class, i);
            System.out.println(city);
            transaction.commit();
            session.close();
        }


    }
}

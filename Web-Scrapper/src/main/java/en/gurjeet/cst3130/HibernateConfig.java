package en.gurjeet.cst3130;
//Hibernate imports
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.util.List;

/**
 *  Hibernate configuration file
 * <p>
 *     Hibernate is split into small core functions
 * </p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */

public class HibernateConfig {

    //Creates new Sessions when we need to interact with the database
    private SessionFactory sessionFactory;


    /** Empty constructor */
    HibernateConfig() {    }


    /** Sets up the session factory.
     *  Call this method first.  */
    public void init(){
        try {
            //Create a builder for the standard service registry
            StandardServiceRegistryBuilder standardServiceRegistryBuilder = new StandardServiceRegistryBuilder();

            //Load configuration from hibernate configuration file.
            //Here we are using a configuration file that specifies Java annotations.
            standardServiceRegistryBuilder.configure("hibernate-annotations.cfg.xml");

            //Create the registry that will be used to build the session factory
            StandardServiceRegistry registry = standardServiceRegistryBuilder.build();
            try {
                //Create the session factory - this is the goal of the init method.
                sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
            }
            catch (Exception e) {
                    /* The registry would be destroyed by the SessionFactory,
                        but we had trouble building the SessionFactory, so destroy it manually */
                System.err.println("Session Factory build failed.");
                e.printStackTrace();
                StandardServiceRegistryBuilder.destroy( registry );
            }


        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("SessionFactory creation failed." + ex);
        }
    }


    /** create a new session
     * @return session
     * */
    public  org.hibernate.Session getSession(){
        Session session = sessionFactory.openSession();
        return session;
    }

    /** Destroy Session Factory */
    public void close(){
         sessionFactory.close();;
    }
    /** Saves the phone
     * @param phone is the phone to be saved
     * @return phone object
     * */
    public Phone savePhone(Phone phone){
        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();

        //Start transaction
        session.beginTransaction();

        //Add product to database - will not be stored until we commit the transaction
        session.saveOrUpdate(phone);

        //Commit transaction to save it to database
        session.getTransaction().commit();

        //Close the session and release database connection
        session.close();
        return phone;
    }
    /**Save the price from database
     * @param price is the price to be saved */
    public void savePrice(Price price){
        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();

        //Start transaction
        session.beginTransaction();

        //Add price to database - will not be stored until we commit the transaction
        session.saveOrUpdate(price);

        //Commit transaction to save it to database
        session.getTransaction().commit();

        //Close the session and release database connection
        session.close();
    }
    /** Find the price, if it is not found, then create a new price object
     * @param price is the price to be found
     * */
    public void findPrice(Price price){

                boolean priceNotFound = false;
            //Get a new Session instance from the session factory
            Session session = sessionFactory.getCurrentSession();

            //Start transaction
            session.beginTransaction();

            //Build query string and search for
            String queryStr = "from Price WHERE websiteUrl ='" + price.getWebsiteUrl() + "'";
            List<Price> degreeList = session.createQuery(queryStr).getResultList();
            //Found a single column then update the price.
            if(degreeList.size() == 1) {

                degreeList.get(0).setPrice(price.getPrice());

            }  else {
                priceNotFound = true;

            }
            //Commit transaction to save it to database
            session.getTransaction().commit();

        if (priceNotFound) {

            savePrice(price);
        }


    }
    /** Find the phone, if it is not found, then create a new phone object
     * @param phone is the phone to be find
     * @return A phone object either, from the database or the newly created
     * */
    public Phone findPhone(Phone phone){
        //Get a new Session instance from the session factory
        Session session = sessionFactory.getCurrentSession();

        //Start transaction
        session.beginTransaction();
        //Query query = session.createQuery("from Phone WHERE model = ' iPhone 13 Pro Max ' AND storage = '128gb' AND colour = 'Gold'"  );

        //Build query string
        String queryStr = "from Phone WHERE model ='" + phone.getModel() +"' AND storage ='"+phone.getStorage()+"' AND color ='"+phone.getColor()+"'";

        List<Phone> phoneList = session.createQuery(queryStr).getResultList();

        //Close the session and release database connection
        session.close();

        if (phoneList.size() == 1) {
            return phoneList.get(0);}
        else {
            return savePhone(phone);
        }
    }

}

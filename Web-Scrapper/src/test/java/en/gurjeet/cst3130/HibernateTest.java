package en.gurjeet.cst3130;

import org.hibernate.Session;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Hibernate Test Class
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HibernateTest {

    private static HibernateConfig hibernateConfig;
    private static Session session;
   // private Session session;

    @BeforeAll
    public static void init() {
        hibernateConfig = new HibernateConfig();
        hibernateConfig.init();
        session = hibernateConfig.getSession();
        System.out.println("Session Factory is created");
    }

    @AfterAll
    public static void clean() {
        if (hibernateConfig != null) hibernateConfig.close();
        System.out.println("Session Factory is destroyed");
    }

    @Test
    @Order(1)
    @DisplayName("Create a new record and insert it into database")
    public void testCreate() {
        System.out.println("Running testCreate...");

        session.beginTransaction();

        Phone phone = new Phone();

        phone.setBrand("Samsung");
        phone.setImage("None");
        phone.setModel("Iphone X");
        phone.setStorage("64Gb");
        phone.setColor("Space Grey");

        session.save(phone);

        session.getTransaction().commit();

        Assertions.assertTrue(phone.getId() > 0);

    }
    @Test
    @Order(2)
    @DisplayName("Find the inserted record from database and change model to 'iPhone 11' ")
    public void testUpdate() {
        Phone phone = new Phone();
        int phoneId;

        phone.setBrand("Samsung");
        phone.setImage("None");
        phone.setModel("Iphone X");
        phone.setStorage("64Gb");
        phone.setColor("Space Grey");

        //Build query string
        String queryStr = "from Phone WHERE model ='" + phone.getModel() +"' AND storage ='"+phone.getStorage()+"' AND colour ='"+phone.getColor()+"'";
        List<Phone> phoneList = session.createQuery(queryStr).getResultList();
        phoneId = phoneList.get(0).getId();
        phoneList.get(0).setModel("iPhone 11");
        session.beginTransaction();
        session.update(phoneList.get(0));
        session.getTransaction().commit();
        Phone updatedProduct = session.find(Phone.class, phoneId);
        assertEquals("iPhone 11", updatedProduct.getModel());
    }

    @Test
    @Order(3)
    @DisplayName("Verify data has been changed")
    public void testGet() {
        Phone phone = new Phone();
        phone.setBrand("Samsung");
        phone.setImage("None");
        phone.setModel("iPhone 11");
        phone.setStorage("64Gb");
        phone.setColor("Space Grey");
        Phone foundPhone = hibernateConfig.findPhone(phone);
        assertEquals(phone.getModel() +""+ phone.getBrand(),foundPhone.getModel()+""+ foundPhone.getBrand());
    }

    @Test
    @Order(4)
    @DisplayName("Delete a record from database")
    public void testDelete() {
        Phone phone = new Phone();
        int deletedPhoneId;
        phone.setBrand("Samsung");
        phone.setImage("None");
        phone.setModel("iPhone 11");
        phone.setStorage("64Gb");
        phone.setColor("Space Grey");

        //Build query string
        String queryStr = "from Phone WHERE model ='" + phone.getModel() +"' AND storage ='"+phone.getStorage()+"' AND colour ='"+phone.getColor()+"'";

        List<Phone> phoneList = session.createQuery(queryStr).getResultList();
        deletedPhoneId = phoneList.get(0).getId();
        session.beginTransaction();
        session.delete(phoneList.get(0));
        session.getTransaction().commit();
        Phone deletedProduct = session.find(Phone.class, deletedPhoneId);
        Assertions.assertNull(deletedProduct);

    }

    @Test
    @Order(5)
    @DisplayName("Run SQL Query in order to check if there is any duplicate data")
    public void testForAnyDuplicateRecordInPhoneTable() {
        System.out.println("Running Test to find out duplicates...");
        String queryStr = "select brand, model, color, storage, count(*) As id from  Phone GROUP BY brand, model, color, storage HAVING count(*) > 1 ";
        //This returns a different object which JAVA does not recognise, however, If the test fails, open the debugger and see the value inside "phoneList.get[0]". This value type "Long" reflects the amount of duplicates in the database
        List<Phone> phoneList =  session.createQuery(queryStr).getResultList();
        assertEquals(0, phoneList.size());
    }

    @BeforeEach
    public void openSession() {
        session = hibernateConfig.getSession();
        System.out.println("Session created");
    }

    @AfterEach
    public void closeSession() {
        if (session != null) session.close();
        System.out.println("Session closed\n");
    }
}
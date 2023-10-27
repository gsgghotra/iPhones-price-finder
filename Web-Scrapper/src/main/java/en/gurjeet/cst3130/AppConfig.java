package en.gurjeet.cst3130;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * Spring Configuration
 * <p>It mainly wires up all the thread classes and passes it to the ThreadManager <br>
 * In addition, each thread can call HibernateFactoryBean in order to get an instance</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
@Configuration
public class AppConfig {

    /*Thread Beans*/
    /**
     * Initialise each thread
     */
    @Bean
    public Thread amazon(){
        Amazon amazon = new Amazon();
        amazon.setUrl("https://www.amazon.co.uk/s?k=iphone&i=electronics&bbn=560798&rh=n%3A560798%2Cp_n_condition-type%3A12319067031%2Cp_89%3AApple%2Cp_76%3A419158031%2Cp_6%3AA3P5ROKL5A1OLE&dc&page=");
        amazon.setWebsite("Amazon");
        return new Thread(amazon);
    }
    @Bean
    public Thread ao(){
        Ao ao = new Ao();
        ao.setUrl("https://ao.com/l/mobile_phones-apple/1-6/304-305/?pagesize=150");
        ao.setWebsite("Ao");
        return new Thread(ao);
    }
    @Bean
    public Thread mobiles(){
        Mobiles mobiles = new Mobiles();
        mobiles.setUrl("https://www.mobiles.co.uk/sim-free-phones?filter_brand_featured%5B%5D=Apple&sort=popularity_desc");
        mobiles.setWebsite("Mobiles");
        return new Thread(mobiles);
    }
    @Bean
    public Thread argos(){
        Argos argos = new Argos();
        argos.setUrl("https://www.argos.co.uk/browse/technology/mobile-phones-and-accessories/sim-free-phones/c:30147/brands:apple/refurbished:no/opt/page:");
        argos.setWebsite("Argos");
        return new Thread(argos);
    }
    @Bean
    public Thread onbuy(){
        Onbuy onbuy = new Onbuy();
        onbuy.setUrl("https://www.onbuy.com/gb/mobile-phones~c12871/iphones~g12852/?category=12871&brand=274&condition=1&offset=");
        onbuy.setWebsite("Onbuy");
        return new Thread(onbuy);
    }
    //Hibernate Dependency Injection
    /**
     * Setup Hibernate Factory for each thread
     */
    @Bean
    public HibernateConfig hibernateFactoryBean(){
        //set up Hibernate
        HibernateConfig hibernateFactory = new HibernateConfig();
        //Build SessionFactory
        hibernateFactory.init();
        System.out.print("From Bean");
        return hibernateFactory;
    }
    /**
     * Thread manager is an instance of ScrapperManager.class, Thread Manager adds the threads into a list for easier manipulation.
     */
    @Bean
    public ScrapperManager threadManager(){
        ScrapperManager threadManager = new ScrapperManager();
        threadManager.addToList(onbuy());
        threadManager.addToList(argos());
        threadManager.addToList(mobiles()); //Not working in 2023
        threadManager.addToList(ao());
        threadManager.addToList(amazon());
        return threadManager;
    }

}


package en.gurjeet.cst3130;

import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

/**
 * Mobiles Thread
 * <p> This is a thread in charge to scrape products from Mobiles</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
public class Mobiles extends WebScrapper implements Runnable   {

    //Define Variables
    private String url;
    private String website;

    /**
     * Initialise variables with default values
     */
    Mobiles( ) {
        url = "none";
        website = "none";
    }

    //Getters and Setters
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    @Override
    public String getWebsite() { return website;  }
    @Override
    public void setWebsite(String website) {   this.website = website;  }

    //Start the run method
    @Override
    public void run() {
        //run
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        //Hibernate get Session Factory
        HibernateConfig hibernateConfig = (HibernateConfig) context.getBean("hibernateFactoryBean");

        //Get a Session
        Session session = hibernateConfig.getSession();

        //make a new Utility object
        Utility utility = new Utility();

        /* web scrapping starts here*/
        while (true){

            try {
                    final Document document = Jsoup.connect(url)
                            .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/118.0.0.0 Safari/537.36")
                            .get();

                //Elements url = document.select(".title");
                Elements colour = document.select(".listing-tile");
                Elements colour2 = colour.select(".add-to-basket-btn.view-deals-button");


                //get the images address for each product
                Elements imageDiv = document.select(".listing-tile-handset").select("img");
                //Select Images
                List<String> image= imageDiv.eachAttr("data-src");

                //get all the String values from the list
                String tmpImage=  image.toString().replace("[","")
                        .replace("]","");
                //Convert list object to an array
                String[] productImage = tmpImage.split(", |  ");

                //get the price for each product
                Elements tmpPriceList = colour.select(".listing-tile-cost-amount");
                //get all the String values from the list
                String tmpPrice=  tmpPriceList.text();
                //Convert list object to an array
                String[] productPrice = tmpPrice.split("£");

                //get the name for each product
                List<String> tmpNameList = colour2.eachAttr("data-name"); //this is the name
                //get all the String values from the list
                String tmpName=  tmpNameList.toString().replace("[", "").replace("  ", "").replace("]", "").replace("Apple ", "");
                //Convert list object to an array
                String[] productName = tmpName.split(",");

                //get the URL address for each product
                List<String> url = colour2.eachAttr("href");
                //get all the String values from the list
                String tmpUrl=  url.toString().replace("[", "").replace("]", "");
                //Convert list object to an array
                String[] productUrl = tmpUrl.split(", |  ");


                //Start loop to add objects to the database
                for (int i = 0; i < productName.length; i++) {

                    utility.findColorUrl(productUrl[i]);
                    utility.findSize(productUrl[i]);
                    utility.findBrand(tmpNameList.toString());
                    String tmpSize = utility.getSize();
                    String[] model = productName[i].split(tmpSize.toUpperCase());

                    Price price = new Price();
                    Double formattedPrice = Double.parseDouble(productPrice[i+1].replace(",",""));
                    Phone phone = new Phone();
                    phone.setStorage(utility.getSize());
                    phone.setModel(model[0].trim());
                    phone.setColor(utility.getColor());
                    price.setWebsiteUrl("https://www.mobiles.co.uk"+productUrl[i]);
                    price.setWebsite(getWebsite());
                    phone.setImage("https:"+productImage[i]);
                    phone.setBrand(utility.getBrand());
                    price.setPrice(formattedPrice);

                    //Find if the phone exists otherwise create a new one and save it
                    Phone foundPhone = hibernateConfig.findPhone(phone);
                    //Assign the phone to the price
                    price.setPhone(foundPhone);

                    //Find the price if it exists and updated otherwise save it
                    hibernateConfig.findPrice(price);

                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(200000 );
            }catch (InterruptedException e) {
              //  session.close();
                System.out.println("Session is closed now for Mobiles.co.uk");
                return;
            }
        } /* web scrapping stops here*/

    }
}
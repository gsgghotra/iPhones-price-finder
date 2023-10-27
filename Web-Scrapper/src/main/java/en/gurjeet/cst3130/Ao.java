package en.gurjeet.cst3130;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.List;

/**
 * Ao Thread
 * <p> This is a thread in charge to scrape products from Ao</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
public class Ao extends WebScrapper implements Runnable   {

    //Define Variables
    private String url;

    /**
     * Initialise variables with default values  *
     */
    Ao( ) {
        url = "none";
    }

    //Getters and Setters
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    //Start the run method
    @Override
    public void run() {
        //run
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        //Hibernate get Session Factory
        HibernateConfig hibernateConfig = (HibernateConfig) context.getBean("hibernateFactoryBean");

        //make a new Utility object
        Utility utility = new Utility();

        /* web scrapping starts here*/
        while (true){

            try {

                final Document document = Jsoup.connect(url).get();

                System.out.print("Pagination started 1 " + url + "\n");

                Elements products = document.select("script");

                for (Element script : products) {
                    String type = script.attr("type");
                    if (type.contentEquals("application/json")) {

                        JSONObject extractedData = new JSONObject(script.data()); // text from the script
                        JSONArray arrayOfProducts = extractedData.getJSONArray("Products");

                        for (int i = 0; i < arrayOfProducts.length(); i++) {   // Start loop to insert products into the database

                            //Fetch JSON data and clean it to get product name
                            JSONObject product = (JSONObject) arrayOfProducts.get(i);
                            //Store the name, size and model into a temporary variable
                            String tmpProductName = (String) product.get("FullTitle");
                            //Clean the name
                            String productName = tmpProductName.replace("Apple","")
                                    .replace(" in ", "")
                                    .replace("gb","GB")
                                    .replace("(PRODUCT) RED", "Red");
                                //    .replace("  ", "");
                            //Fetch JSON data and clean it to get product price
                            JSONObject itemPrice = (JSONObject) product.get("PricePodViewModel");
                            //Store the price into a temporary variable
                            String productPrice = (String) itemPrice.get("NowPrice").toString();
                            //Format the price into decimal
                            Double formattedPrice = Double.parseDouble(productPrice.trim());
                            //find the size and color of the product
                            utility.findColor(productName);
                            utility.findSize(productName);
                            utility.findBrand(tmpProductName);
                            //get the product model
                            String tmpSize = utility.getSize();
                            String[] model = productName.split(tmpSize.toUpperCase());

                            Price price = new Price();
                            Phone phone = new Phone();
                            phone.setStorage(utility.getSize());
                            phone.setModel(model[0].trim());
                            phone.setColor(utility.getColor());
                            price.setWebsiteUrl((String) product.get("FullProductUrl"));
                            price.setWebsite(getWebsite());
                            phone.setImage((String) "https:"+product.get("Image"));
                            phone.setBrand(utility.getBrand());
                            price.setPrice(formattedPrice);

                            //Find if the phone exists otherwise create a new one and save it
                            Phone foundPhone = hibernateConfig.findPhone(phone);
                            //Assign the phone to the price
                            price.setPhone(foundPhone);

                            //Find the price if it exists and updated otherwise save it
                            hibernateConfig.findPrice(price);

                        }

                        break;
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                Thread.sleep(200000);
            }catch (InterruptedException e) {

                System.out.println("Session is closed now for Ao");
                return;
            }
        } /* web scrapping stops here*/

    }
}
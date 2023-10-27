package en.gurjeet.cst3130;
import org.apache.commons.text.WordUtils;
import org.hibernate.Session;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Onbuy Thread
 * <p> This is a thread in charge to scrape products from Onbuy</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
public class Onbuy extends WebScrapper implements Runnable   {

    //Define Variables
    private String url;
    private String website;

    /**
     * Initialise variables with default values
     */
    Onbuy( ) {
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
                      int page = 0;
                for (int pagination = 0; pagination < 1; pagination++) {


                    System.out.print("Pagination started, scrapped 0 to  " + page + " products \n");
                    String myUrl =  url + page;
                    System.out.println(myUrl);

                    //Download HTML document from website
                    Document document = Jsoup.connect(myUrl)
                            .userAgent("Mozilla/5.0").get();


                    Elements products = document.select(".product");
                    System.out.println(products);


                    //get the name
                    Elements elementName = document.select(".name");


                    //Clean the product names
                    ArrayList<String> tmpProductList = new ArrayList<>();
                    //Delete unwanted text
                    tmpProductList.add(elementName.text().replace("|", "").
                            replace("(","").
                            replace(")","").
                            replace("Product","").
                            replace("2nd Generation","").
                            replace("Dual Sim","").
                            replace("Single Sim","").
                            replace("Single SIM","").
                            replace("Single SIM","").
                            replace("]","").
                        //    replace("  ","").
                         //   replace("  ","").
                            replace("e]",""). //check if needed
                            replace("[","").
                            replace("3GB RAM ","").
                            replace("PRODUCT", ""));


                    //get all the String values from the list
                    String tempName = tmpProductList.toString();

                    //Storage
                    List<String> productStorage = elementName.eachAttr("data-product_name");
                    System.out.println("STRG " + productStorage);
                    //Convert list object to an array
                    String[] productName = tempName.split("Apple");

                    //get the Price
                    Elements elementPrice = document.select(".value");
                    String[] productPrice = elementPrice.text().replace("From","").split("£");

                    //get the URL address for each product
                    List<String> url = products.select(".product.options").select("a").eachAttr("href");

                    //get all the String values from the list
                    String tmpUrl = url.toString().replace("[", "").
                                                   replace("]","");
                    //Convert list object to an array
                    String[] productUrl = tmpUrl.split(", |  ");

                  for (int i = 3; i < productName.length; i++) {

                    }

                     //Start loop to insert products into the database
                    for (int i = 3; i < productName.length; i++) {

                        utility.findSize(productUrl[i-3]);
                            utility.findColor(productName[i+1]);
                            utility.findSize(productUrl[i - 3]);
                            utility.findBrand(tempName);
                            String tmpColor =  WordUtils.capitalize(utility.getColor());
                            String[] model = productName[i+1].split(tmpColor);
                            Price price = new Price();
                            Double formattedPrice = Double.parseDouble(productPrice[i - 2].replace(",",""));
                            Phone phone = new Phone();
                            phone.setStorage("128GB");
                            phone.setModel(model[0].trim());
                            phone.setColor(utility.getColor());

                            price.setWebsiteUrl(productUrl[i - 3]);

                            String search = model[0].trim().replace(" ","-") + "  " + utility.getColor().replace(" ", "-");

                            final Document searchForImage = Jsoup.connect("https://www.argos.co.uk/search/"+search+"/category:42793786/brands:apple/").get();
                            Elements imageDiv = searchForImage.getElementsByAttributeValue("data-test","component-product-card-imageWrapper").select("img");
                            //Select Images
                            List<String> image= imageDiv.eachAttr("src");

                            //get all the String values from the list
                            String tmpImage=  image.toString().replace("[","")
                                    .replace("]","");
                            //Convert list object to an array
                            String[] productImage = tmpImage.split(", |  ");

                            phone.setImage("https:"+productImage[0]);
                            phone.setBrand(utility.getBrand());
                            price.setPrice(formattedPrice);
                            price.setWebsite(getWebsite());
                            //Find if the phone exists otherwise create a new one and save it
                            Phone foundPhone = hibernateConfig.findPhone(phone);
                            //Assign the phone to the price
                            price.setPhone(foundPhone);

                            //Find the price if it exists and updated otherwise save it
                            hibernateConfig.findPrice(price);

                    }//Finish loop to products to database

                    page += 60;

                }
            } catch (Exception ex) {

                ex.printStackTrace();

            }


            try {
                Thread.sleep(200000);
            }catch (InterruptedException e) {
                System.out.println("Session is closed now for Onbuy");
                break;

            }
        }

    }



}










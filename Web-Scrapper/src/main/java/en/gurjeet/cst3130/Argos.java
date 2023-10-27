package en.gurjeet.cst3130;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Amazon Thread
 * <p> This is a thread in charge to scrape products from Amazon</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */

public class Argos extends WebScrapper implements Runnable   {

    //Define Variables
    private String url;
    private String website;

    /**
     * Initialise variables with default values
     */
    Argos( ) {
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

            //Pagination
        int numberOfPage = 1;
            try {

                for (int pagination = 1; pagination <= numberOfPage; pagination++) {

                    System.out.println("Pagination started  " + pagination);
                    String myUrl =  url + pagination + "/";

                    final Document document = Jsoup.connect(myUrl).get();

                    //get the total number of pages
                    Elements elementPagination = document.select(".Paginationstyles__PageNumbers-sc-1temk9l-2");
                    //clean out
                    String[] paginationArray = elementPagination.text().split("of");
                    //set the total number of pages
                    numberOfPage = Integer.parseInt(paginationArray[1].replace(" ",""));
                    //get the name
                    //System.out.println(numberOfPage);
                    Elements elementName = document.select(".ProductCardstyles__Link-h52kot-13");
                    //System.out.println(elementName);

                    String[] productName = elementName.text().split("SIM Free ");
                    Elements elementPrice = document.select("strong");

                    //get the URL address for each product
                    List<String> url = elementName.eachAttr("href");
                    System.out.println("THIS IS URL "+url);
                    //get all the String values from the list
                    String tmpUrl=  url.toString().replace("[","")
                                                  .replace("]","");
                    //Convert list object to an array
                    String[] productUrl = tmpUrl.split(", |  ");
                    //System.out.println(url);

                    //Clean the product price array
                    ArrayList<String> tmpPriceList = new ArrayList<>();

                    String[] tmpPrice = elementPrice.html().split("£|iPhone");
                    String[] productPrice = new String[tmpPrice.length-2];

                    for (int i = 2; i < tmpPrice.length ; i++) {
                        tmpPriceList.add(tmpPrice[i-1]);
                            productPrice[i-2] = tmpPrice[i-1];
                    }
                    //Start loop to add objects to the database
                    for (int i = 0; i < productPrice.length; i++) {
                        //check if the product is empty before inserting into the database
                        if (productPrice[i].length() > 5 ) {

                            utility.findColor(productName[i+1]);
                            utility.findSize(productName[i+1]);
                            utility.findBrand(getUrl());
                            String tmpSize = utility.getSize();
                            String[] model = productName[i+1].split(tmpSize.toUpperCase());
                            Price price = new Price();
                            Double formattedPrice = Double.parseDouble(productPrice[i]);
                            Phone phone = new Phone();
                            phone.setStorage(utility.getSize());
                            phone.setModel(model[0].replace(" 5G ", "").trim());
                            phone.setColor(utility.getColor());
                            price.setWebsiteUrl("https://www.argos.co.uk"+productUrl[i]);
                            System.out.println("Debugger - " + productUrl);

                            //Start Searching for the Image required
                            String search = model[0].replace(" ","-") + utility.getColor().replace(" ", "-");
                            final Document searchForImage = Jsoup.connect("https://www.argos.co.uk/search/"+search+"/category:42793786/brands:apple/").get();
                            Elements imageDiv = searchForImage.getElementsByAttributeValue("data-test","component-product-card-imageWrapper").select("img");
                            //Select Images
                            List<String> image= imageDiv.eachAttr("src");
                            //get all the String values from the list
                            String tmpImage=  image.toString().replace("[","")
                                    .replace("]","");
                            //Convert list object to an array
                            String[] productImage = tmpImage.split(", |  ");
                            //Set the Image required
                            phone.setImage("https:"+productImage[0]);
                            phone.setBrand(utility.getBrand());
                            price.setPrice(formattedPrice);

                            //Find if the phone exists otherwise create a new one and save it
                            Phone foundPhone = hibernateConfig.findPhone(phone);
                            //Assign the phone to the price
                            price.setPhone(foundPhone);
                            price.setWebsite(getWebsite());
                            //Find the price if it exists and updated otherwise save it
                            hibernateConfig.findPrice(price);

                        }

                    } ///Finish loop to add products to database
            }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            /* web scrapping finishes here*/
            try {
                Thread.sleep(200000);
            }catch (InterruptedException e) {
              //  session.close();
                System.out.println("Session is closed now for Argos");
                return;
            }
        } /* web scrapping stops here*/

    }
}
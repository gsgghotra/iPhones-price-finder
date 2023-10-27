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
public class Amazon extends WebScrapper implements Runnable {

    //Define Variables
    private String url;
    private String website;

    /**
     * Initialise variables with default values
     */
    Amazon() {
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
        while (true) {

            try {

                for (int pagination = 1; pagination <= 4; pagination++) {


                    System.out.print("Pagination started  " + pagination);
                    String myUrl = url + pagination + "&qid=1629832058&rnid=419151031&ref=sr_pg_2";
                    final Document document = Jsoup.connect(myUrl).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/51.0.2704.103 Safari/537.36")
                            .header("_rails-root_session", "259-5876731-4877419")
                            .referrer("http://www.google.com")
                            .timeout(0)
                            .get();

                    //get the images address for each product
                    Elements imageDiv = document.select("img").select(".s-image");

                    //Select Images
                    List<String> image = imageDiv.eachAttr("src");

                    //get all the String values from the list
                    String tmpImage = image.toString().replace("[", "")
                            .replace("]", "");
                    //Convert list object to an array
                    String[] productImage = tmpImage.split(", |  ");

                    Elements elementName = document.select(".a-color-base.a-text-normal");

                    //Clean the product names
                    ArrayList<String> tmpProductList = new ArrayList<>();

                    tmpProductList.add(elementName.text().replace("with AppleCare+", "")
                            .replace("(", "")
                            .replace(")", "")
                            .replace("PRODUCT", "")
                            .replace(" \" ", " ")
                            .replace(" - ", " ")
                            .replace("includes EarPods, power adapter", ""));


                    //get all the String values from the list
                    String tempName = tmpProductList.toString();

                    //Convert list object to an array
                    String[] productName = tempName.split("Apple");

                    Elements elementUrl = document.select(".a-size-base.a-link-normal.a-text-normal");

                    //get the URL address for each product
                    List<String> url = elementUrl.eachAttr("href");
                    //get all the String values from the list
                    String tmpUrl = url.toString();
                    //Convert list object to an array
                    String[] productUrl = tmpUrl.split(", |  ");

                    //Get the Price for each product
                    Elements elementPrice = document.getElementsByAttributeValue("data-a-size", "l");

                    //Select only those prices which are displayed currently
                    elementPrice = elementPrice.select(".a-offscreen");

                    //Make an array of prices from a string
                    String[] productPrice = elementPrice.text().split("£");

                    //Start loop to add objects to the database
                    for (int i = 0; i < productUrl.length; i++) {
                        // Create a new amazon object
                        Price price = new Price();
                        Phone phone = new Phone();
                        //check if the product is empty before inserting into the database
                        if (productName[i].length() > 5) {

                            utility.findColor(productName[i]);
                            utility.findSize(productName[i]);
                            utility.findBrand(tempName);
                            String tmpSize = utility.getSize();
                            String[] model = productName[i].split(tmpSize.toUpperCase());
                            Double formattedPrice = Double.parseDouble(productPrice[i].replace(",", ""));
                            phone.setStorage(utility.getSize());
                            phone.setModel(model[0].trim());
                            phone.setColor(utility.getColor());
                            price.setWebsiteUrl("https://www.amazon.co.uk" + productUrl[i - 1]);
                            phone.setImage(productImage[i]);
                            phone.setBrand(utility.getBrand());
                            price.setPrice(formattedPrice);
                            price.setWebsite(getWebsite());
                            //Find if the phone exists otherwise create a new one and save it
                            Phone foundPhone = hibernateConfig.findPhone(phone);
                            //Assign the phone to the price
                            price.setPhone(foundPhone);
                            //Find the price if it exists and updated otherwise save it
                            hibernateConfig.findPrice(price);
                        }
                    } //Stop Loop for every item
                    //Sleep after scraping one page
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        return;
                    }

                } //Stop pagination Loop

            } catch (Exception ex) {

                ex.printStackTrace();

            }

            /* web scrapping finishes here*/

            try {
                Thread.sleep(200000);
            } catch (InterruptedException e) {
                System.out.println("Session is closed now for Amazon");
                return;

            }
        }

    }

}
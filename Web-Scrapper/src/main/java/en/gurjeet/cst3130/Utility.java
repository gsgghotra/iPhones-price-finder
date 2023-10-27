package en.gurjeet.cst3130;
/**
 * Utility class
 * <p> Contains useful methods used for the scrappers, such as finding colors and storage sizes</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
public class Utility {
    //Define Variables
    private final String[] colorMatches = new String[] {"Black", "Blue", "Purple", "White","Grey" , "Green", "Graphite", "Coral", "Red", "Pacific Blue", "Gold", "Yellow", "Space Grey", "Silver", "Coral", "RED","Red", "Pink", "Starlight", "Rose Gold", "Jet Black", "Midnight"};
    private final String[] sizeMatches = new String[] {"16gb","32gb","64 GB","1tb", "128 GB","128gb","64gb","256gb","256 GB","64GB", "128GB", "256GB", "512 GB", "512GB", "512gb", "1TB" };
    private String color;
    private String size;
    private String brand;
    //Getters and Setters
    public void setColor(String color) {this.color = color;}
    public String getColor() {return color;}
    public String getSize() {return size;}
    public void setSize(String size) {this.size = size; }
    public String getBrand() { return brand;  }
    public void setBrand(String brand) {   this.brand = brand;  }

    /**
     * Finds the colour if there is any match it will return the colour
     * @param colour is the full phone name such as "iPhone X 128GB Silver"
     */
    public void findColor(String colour){
        for (String s : colorMatches) {
            if (colour.contains(s))
            {
                setColor(s.toLowerCase());
            }
        }

    }
    /**
     * Finds the size if there is any match it will return the size
     * @param size is the full phone name such as "iPhone X 128GB Silver"
     */
    public void findSize(String size){

        for (String s : sizeMatches) {

            if (size.toLowerCase().contains(s.toLowerCase()))
            {
                setSize(s.toLowerCase());

            }
        }

    }
    /**
     * Finds the colour if there is any match it will return the colour
     * @param colour is the full URL
     */
    public void findColorUrl(String colour){ //Find colours from url

       String formattedColour = colour
               .replace("/sim-free-apple-iphone-", "")
               .replace("?tariffcode=simfreeprice&giftcode=NA&sort=popularity_desc&filter_brand_featured%5B0%5D=Apple", "")
               .replace("tariffcode=simfreeprice&giftcode=FCONOFVAR&sort=popularity_desc&filter_brand_featured%5B0%5D=Apple", "");

        for (String s : colorMatches) {

            if (formattedColour.contains(s.toLowerCase()))
            {
                setColor(s.toLowerCase());
            }
        }
    }

    /**
     * Finds the brand if there is any match it will return it
     * @param brand is the full phone name such as "Apple iPhone X 128GB Silver"
     */
    public void findBrand(String brand){


            if (brand.toLowerCase().contains("apple"))
            {
                setBrand("Apple");

            }


    }


}
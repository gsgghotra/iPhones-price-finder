package en.gurjeet.cst3130;
/**
 * Abstract Class implemented across all threads
 * <p> It specifies some of the requirements for each thread, such as having an url field for each</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
public abstract class WebScrapper {
    //Define Variables
    private String url;
    private String Website;
    // Getters and Setters
    public void setUrl(String url) {
        this.url = url;
    }
    public String getUrl() {
        return url;
    }
    public String getWebsite() {  return Website;  }
    public void setWebsite(String website) { Website = website;  }
}



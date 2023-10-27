package en.gurjeet.cst3130;
import java.util.ArrayList;
/**
 * Scrapper Manager Class is used manage and do operations such as add/start/stop scrappers
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
public class ScrapperManager {
    //Variables
    private ArrayList<Thread> scrapperList;
    //Setters And Getters
    public ArrayList<Thread> getScrapperList() {
        return scrapperList;
    }
    public void setScrapperList(ArrayList<Thread> scrapperList) {
        this.scrapperList = scrapperList;
    }
    public void addToList(Thread thread){
        scrapperList.add(thread);
    }
    ScrapperManager(){
        scrapperList = new ArrayList();
    }

    //Methods
    /**
     * Start all the threads
     */
    public void start (){
        for (Thread thread: scrapperList) {
            thread.start();
        }
    }
    /**
     * Stop all the threads
     */
    public void stop() {
        for (Thread thread: scrapperList) {
            thread.interrupt();
        }
    }
}

package en.gurjeet.cst3130;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.ArrayList;
import java.util.Scanner;



/**
 *  Web Scrapping Software
 * <p>
 * The software consists of 5 threads, with each thread being a particular website to be scrapped.
 * The data is then cleaned and exported to the database by using Hibernate.
 * <br> <b> Websites to be scrapped:</b>
 * <br> Amazon.co.uk
 * <br> Argos.co.uk
 * <br> Ao.co.uk
 * <br> Onbuy.com
 * <br> Mobiles.co.uk
 * </p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */

public class Main {
    public static void main(String[] args) throws InterruptedException {

        //Use Spring Annotation Configuration to set up and run Application
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        //Get Scrapper Manager Bean
        ScrapperManager threadManager = (ScrapperManager) context.getBean("threadManager");

        //Start the threads
         threadManager.start();

        //Read the user input and stop threads
        Scanner inputScanner = new Scanner(System.in);
        String userInput = inputScanner.nextLine();

        while(!userInput.equals("q")){
            userInput = inputScanner.nextLine();
        }


        threadManager.stop();

        System.out.print("Threads were stopped");

    }


}


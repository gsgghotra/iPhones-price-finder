package en.gurjeet.cst3130;

import jakarta.persistence.*;
/**
 * Price class mapped to SQL Database
 * <p> This class is used when inserting data, it contains the mapping to each column and table name</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
@Entity
@Table(name = "price")
public class Price {
    //Define Variables
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private Phone phone;
    @Column(name = "price")
    private Double Price;
    @Column(name = "websiteUrl")
    private String websiteUrl;
    @Column(name = "website")
    private String website;

    //Getters and Setters
    @Access(AccessType.PROPERTY)
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "phone_id", referencedColumnName = "id")
    public Phone getPhone() {
        return phone;
    }
    public void setPhone(Phone phone) {
        this.phone = phone;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public Double getPrice() {
        return Price;
    }
    public void setPrice(Double price) {
        Price = price;
    }
    public void deletePhone(Phone tmp){
        tmp = null;
    }
    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }
    public String getWebsiteUrl() {
        return websiteUrl;
    }
    public String getWebsite() { return website;   }
    public void setWebsite(String website) {  this.website = website;  }
}
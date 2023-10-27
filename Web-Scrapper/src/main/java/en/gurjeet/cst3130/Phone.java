package en.gurjeet.cst3130;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
/**
 * Phone class mapped to SQL Database
 * <p> This class is used when inserting data, it contains the mapping to each column and table name</p>
 *  @author Gurjeet Singh
 *  @version 1.0
 *  @since   2020-09-01
 */
@Entity
@Table(name = "phone")
public class Phone {
    //Define Variables
    private int id;
    private String model;
    private String brand;
    private String color;
    private String storage;
    private String image;
    private List<Price> priceEntity = new ArrayList<>();
    /**
     * Empty Constructor
     */
    public Phone() {
    }
    //Getters and Setters
    @Access(AccessType.PROPERTY)
    @OneToMany(mappedBy = "phone")
    public List<Price> getPriceEntity() {
        return priceEntity;
    }

    public void setPriceEntity(List<Price> priceEntity) {
        this.priceEntity = priceEntity;
    }
    @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    @Column(name = "model")
    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }
    @Column(name = "colour")
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
    @Column(name = "storage")
    public String getStorage() {
        return storage;
    }
    public void setStorage(String storage) {   this.storage = storage;   }
    public String getBrand() {   return brand;  }
    @Column(name = "brand")
    public void setBrand(String brand) {   this.brand = brand;    }
    public String getImage() {  return image;   }
    public void setImage(String image) {    this.image = image;  }
}

package en.gurjeet.cst3130;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

/**
 * Test Utility Class
 */
@DisplayName("Multiple Tests")
public class UtilityTest
{
    public String myVariable = "Apple iPhone 13 Pro 128GB  Graphite";
    @Test
    @DisplayName("Find the colour of a phone from a String")
    public void shouldAnswerWithTrueIfTheColourIsFound() {
    Utility utility = new Utility();
    utility.findColor(myVariable);
    assertEquals("graphite", utility.getColor());
    }

    @Test
    @DisplayName("Find the storage size of a phone from a String")
    public void shouldAnswerWithTrueIfTheStorageSizeIsFound() {
        Utility utility = new Utility();
        utility.findSize(myVariable);
        assertEquals("128gb", utility.getSize());
    }

}

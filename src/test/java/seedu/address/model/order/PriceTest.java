package seedu.address.model.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;


class PriceTest {

    @Test
    void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Price(null));
    }

    @Test
    void constructor_invalidPriceEmpty_throwsIllegalArgumentException() {
        String invalidPrice = "";
        assertThrows(IllegalArgumentException.class, () -> new Price(invalidPrice));
    }

    @Test
    void constructor_invalidPriceNegative_throwsIllegalArgumentException() {
        String invalidPrice = "-1";
        assertThrows(IllegalArgumentException.class, () -> new Price(invalidPrice));
    }

    @Test
    void isValidPrice() {

        assertThrows(NullPointerException.class, () -> Price.isValidPrice(null));
        assertFalse(Price.isValidPrice(""));

        assertFalse(Price.isValidPrice("-1"));

        assertFalse(Price.isValidPrice(" "));
        assertTrue(Price.isValidPrice("0"));

        assertTrue(Price.isValidPrice("1"));
        assertTrue(Price.isValidPrice("100"));
        assertTrue(Price.isValidPrice("1.5"));
        assertTrue(Price.isValidPrice("1.55"));
        assertTrue(Price.isValidPrice("1.500"));
    }

    @Test
    void testEquals() {
        Price price = new Price("1");

        // same values -> returns true
        assertEquals(price, new Price("1"));

        // same object -> returns true
        assertEquals(price, price);

        // null -> returns false
        assertNotEquals(null, price);

        // different types -> returns false
        assertNotEquals(price, 0.0);

        // different values -> returns false
        assertNotEquals(price, new Price("2"));
    }

}

package seedu.address.model.order;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.text.DecimalFormat;


/**
 * Represents the price of items for the order in the order book.
 * Guarantees: immutable; is valid as declared in {@link #isValidPrice(String)}
 */
public class Price {

    public static final String MESSAGE_CONSTRAINTS =
            "Price must be a positive number and can only include up to two decimal places";
    public final double value;

    /**
     * Constructs a {@code Price}.
     *
     * @param value A valid price.
     */
    public Price(String value) {
        requireNonNull(value);
        checkArgument(isValidPrice(value), MESSAGE_CONSTRAINTS);
        this.value = truncateToTwoDecimalPlaces(Double.parseDouble(value));
    }

    /**
     * Returns true if a given double is a valid price.
     *
     * @param test the price to be tested
     * @return true if the price is valid
     */
    public static boolean isValidPrice(String test) {
        try {
            return (!test.isEmpty()) && Double.parseDouble(test) >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Truncates a double value to two decimal places.
     *
     * @param value the double value to be truncated
     * @return the truncated value
     */
    private double truncateToTwoDecimalPlaces(double value) {
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(value));
    }

    @Override
    public String toString() {
        return String.format("%.2f", value + 0.0);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof Price)) {
            return false;
        }

        Price otherPrice = (Price) other;
        return value == otherPrice.value;
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }
}

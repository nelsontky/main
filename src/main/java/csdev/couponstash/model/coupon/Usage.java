package csdev.couponstash.model.coupon;

import static csdev.couponstash.commons.util.AppUtil.checkArgument;
import static java.util.Objects.requireNonNull;

import csdev.couponstash.commons.util.StringUtil;

/**
 * Represents a Coupon's usage in the CouponStash.
 * Guarantees: immutable; is valid as declared in {@link #isValidUsage(String)}
 */
public class Usage {

    public static final String MESSAGE_CONSTRAINTS =
            "Usage should only contain numbers, and it should at least be a value of 0";
    public static final String MESSAGE_UNEDITABLE = "Usage cannot be added or edited. "
            + "This is to keep the savings consistent.";
    public static final String VALIDATION_REGEX = "^\\d+$";
    public final int value;

    /**
     * Constructs a {@code Usage} with a String parameter.
     *
     * @param usage Valid usage number.
     */
    public Usage(String usage) {
        requireNonNull(usage);
        checkArgument(isValidUsage(usage), MESSAGE_CONSTRAINTS);

        value = Integer.parseInt(usage);
    }

    /**
     * Constructs a {@code Usage} with an int parameter.
     */
    public Usage(int usage) {
        requireNonNull(usage);

        value = usage;
    }

    /**
     * Constructs a {@code Usage} that defaults the current value to 0.
     */
    public Usage() {
        this(0);
    }

    /** Returns true if a given string is a valid usage number.
     *s
     * @param test String to be validated.
     */
    public static boolean isValidUsage(String test) {
        return test.matches(VALIDATION_REGEX) && !StringUtil.isIntegerOverflow(test);
    }

    /**
     * Returns true if {@code usage} current value is equals or greater than its {@code limit}.
     */
    public boolean isAtLimit(Limit limit) {
        int usageLimit = limit.value;
        return value >= usageLimit;
    }

    /**
     * Returns true if {@code usage} current value is greater than its {@code limit}.
     * @param limit Limit of the coupon to compare to.
     */
    public boolean isGreaterThanLimit(Limit limit) {
        int usageLimit = limit.value;
        return value > usageLimit;
    }

    /**
     * Returns new {@code Usage} with an increase of 1 in value.
     */
    public Usage increaseUsageByOne() {
        return increaseUsage(1);
    }

    /**
     * Returns new {@code Usage} with an increase of {@code numberOfTimes} in value.
     */
    public Usage increaseUsage(int numberOfTimes) {
        int finalValue = numberOfTimes + value;
        return new Usage(finalValue);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this
                || (other instanceof Usage
                && value == ((Usage) other).value);
    }

    @Override
    public int hashCode() {
        return value;
    }
}
